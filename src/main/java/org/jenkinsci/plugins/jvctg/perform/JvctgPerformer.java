package org.jenkinsci.plugins.jvctg.perform;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvctg.config.CredentialsHelper.findOAuth2TokenCredentials;
import static org.jenkinsci.plugins.jvctg.config.CredentialsHelper.findUsernamePasswordCredentials;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_COMMENTONLYCHANGEDCONTENT;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_CREATESINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_GITHUBURL;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_KEEP_OLD_COMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_MINSEVERITY;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_OAUTH2TOKEN;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYNAME;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYOWNER;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USEOAUTH2TOKENCREDENTIALS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USERNAMEPASSWORDCREDENTIALSID;
import static se.bjurr.violations.comments.github.lib.ViolationCommentsToGitHubApi.violationCommentsToGitHubApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.parsers.FindbugsParser.setFindbugsMessagesXml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

import org.jenkinsci.plugins.jvctg.config.ViolationConfig;
import org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfig;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.remoting.RoleChecker;

import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.reports.Parser;
import se.bjurr.violations.lib.util.Filtering;

public class JvctgPerformer {
  private static Logger LOG = Logger.getLogger(JvctgPerformer.class.getSimpleName());

  @VisibleForTesting
  public static void doPerform(
      final ViolationsToGitHubConfig config,
      final File workspace,
      final Optional<StandardUsernamePasswordCredentials> usernameCredentials,
      final Optional<StringCredentials> oauth2Credentials,
      final TaskListener listener)
      throws MalformedURLException {
    if (isNullOrEmpty(config.getPullRequestId())) {
      listener
          .getLogger()
          .println("No pull request id defined, will not send violation comments to GitHub.");
      return;
    }
    final Integer pullRequestIdInt = Integer.valueOf(config.getPullRequestId());

    final List<Violation> allParsedViolations = newArrayList();
    for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
      if (!isNullOrEmpty(violationConfig.getPattern())) {
        List<Violation> parsedViolations =
            violationsApi() //
                .findAll(violationConfig.getParser()) //
                .withReporter(violationConfig.getReporter()) //
                .inFolder(workspace.getAbsolutePath()) //
                .withPattern(violationConfig.getPattern()) //
                .violations();
        final SEVERITY minSeverity = config.getMinSeverity();
        if (minSeverity != null) {
          parsedViolations = Filtering.withAtLEastSeverity(parsedViolations, minSeverity);
        }
        allParsedViolations.addAll(parsedViolations);
        listener
            .getLogger()
            .println(
                "Found " + parsedViolations.size() + " violations from " + violationConfig + ".");
      }
    }

    String username = null;
    String password = null;
    String oAuth2Token = config.getOAuth2Token();
    if (isNullOrEmpty(oAuth2Token) && oauth2Credentials.isPresent()) {
      oAuth2Token = oauth2Credentials.get().getSecret().getPlainText();
    } else if (isNullOrEmpty(oAuth2Token) && usernameCredentials.isPresent()) {
      username = usernameCredentials.get().getUsername();
      password = usernameCredentials.get().getPassword().getPlainText();
    }

    if (!isNullOrEmpty(oAuth2Token)) {
      listener.getLogger().println("Using OAuth2Token");
    } else if (!isNullOrEmpty(username) && !isNullOrEmpty(password)) {
      listener.getLogger().println("Using username / password");
    } else {
      throw new IllegalStateException("No credentials found!");
    }

    listener
        .getLogger()
        .println(
            "PR: "
                + config.getRepositoryOwner()
                + "/"
                + config.getRepositoryName()
                + "/"
                + config.getPullRequestId()
                + (isNullOrEmpty(config.getGitHubUrl()) ? "" : " on " + config.getGitHubUrl()));

    try {
      violationCommentsToGitHubApi() //
          .withoAuth2Token(oAuth2Token) //
          .withUsername(username) //
          .withPassword(password) //
          .withGitHubUrl(config.getGitHubUrl()) //
          .withPullRequestId(pullRequestIdInt) //
          .withRepositoryName(config.getRepositoryName()) //
          .withRepositoryOwner(config.getRepositoryOwner()) //
          .withViolations(allParsedViolations) //
          .withCreateCommentWithAllSingleFileComments(
              config.getCreateCommentWithAllSingleFileComments()) //
          .withCreateSingleFileComments(config.getCreateSingleFileComments()) //
          .withCommentOnlyChangedContent(config.getCommentOnlyChangedContent()) //
          .withKeepOldComments(config.isKeepOldComments()) //
          .toPullRequest();
    } catch (final Exception e) {
      Logger.getLogger(JvctgPerformer.class.getName()).log(SEVERE, "", e);
      final StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      listener.getLogger().println(sw.toString());
    }
  }

  /** Makes sure any Jenkins variable, used in the configuration fields, are evaluated. */
  @VisibleForTesting
  static ViolationsToGitHubConfig expand(
      final ViolationsToGitHubConfig config, final EnvVars environment) {
    final ViolationsToGitHubConfig expanded = new ViolationsToGitHubConfig();
    expanded.setGitHubUrl(environment.expand(config.getGitHubUrl()));
    expanded.setPullRequestId(environment.expand(config.getPullRequestId()));
    expanded.setRepositoryName(environment.expand(config.getRepositoryName()));
    expanded.setRepositoryOwner(environment.expand(config.getRepositoryOwner()));

    expanded.setCreateCommentWithAllSingleFileComments(
        config.getCreateCommentWithAllSingleFileComments());
    expanded.setCreateSingleFileComments(config.getCreateSingleFileComments());
    expanded.setCommentOnlyChangedContent(config.getCommentOnlyChangedContent());

    expanded.setMinSeverity(config.getMinSeverity());

    expanded.setUsernamePasswordCredentialsId(config.getUsernamePasswordCredentialsId());

    expanded.setoAuth2Token(environment.expand(config.getOAuth2Token()));
    expanded.setoAuth2TokenCredentialsId(config.getOAuth2TokenCredentialsId());
    expanded.setKeepOldComments(config.isKeepOldComments());
    for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
      final String pattern = environment.expand(violationConfig.getPattern());
      final String reporter = violationConfig.getReporter();
      final Parser parser = violationConfig.getParser();
      if (isNullOrEmpty(pattern) || parser == null) {
        LOG.fine("Ignoring violationConfig because of null/empty -values: " + violationConfig);
        continue;
      }
      final ViolationConfig p = new ViolationConfig();
      p.setPattern(pattern);
      p.setReporter(reporter);
      p.setParser(parser);
      expanded.getViolationConfigs().add(p);
    }
    return expanded;
  }

  public static void jvctsPerform(
      final ViolationsToGitHubConfig configUnexpanded,
      final FilePath fp,
      final Run<?, ?> build,
      final TaskListener listener) {
    try {
      final EnvVars env = build.getEnvironment(listener);
      final ViolationsToGitHubConfig configExpanded = expand(configUnexpanded, env);
      listener.getLogger().println("---");
      listener.getLogger().println("--- Jenkins Violation Comments to GitHub ---");
      listener.getLogger().println("---");
      logConfiguration(configExpanded, build, listener);

      final Optional<StandardUsernamePasswordCredentials> usernameCredentials =
          findUsernamePasswordCredentials(configExpanded.getUsernamePasswordCredentialsId());
      final Optional<StringCredentials> oauth2Credentials =
          findOAuth2TokenCredentials(configExpanded.getOAuth2TokenCredentialsId());

      listener.getLogger().println("Running Jenkins Violation Comments To GitHub");
      listener.getLogger().println("PR " + configExpanded.getPullRequestId());

      fp.act(
          new FileCallable<Void>() {

            private static final long serialVersionUID = 6166111757469534436L;

            @Override
            public void checkRoles(final RoleChecker checker) throws SecurityException {}

            @Override
            public Void invoke(final File workspace, final VirtualChannel channel)
                throws IOException, InterruptedException {
              setupFindBugsMessages();
              listener.getLogger().println("Workspace: " + workspace.getAbsolutePath());
              doPerform(
                  configExpanded, workspace, usernameCredentials, oauth2Credentials, listener);
              return null;
            }
          });
    } catch (final Exception e) {
      Logger.getLogger(JvctgPerformer.class.getName()).log(SEVERE, "", e);
      final StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      listener.getLogger().println(sw.toString());
      return;
    }
  }

  private static void logConfiguration(
      final ViolationsToGitHubConfig config, final Run<?, ?> build, final TaskListener listener) {
    final PrintStream logger = listener.getLogger();
    logger.println(FIELD_GITHUBURL + ": " + config.getGitHubUrl());
    logger.println(FIELD_REPOSITORYOWNER + ": " + config.getRepositoryOwner());
    logger.println(FIELD_REPOSITORYNAME + ": " + config.getRepositoryName());
    logger.println(FIELD_PULLREQUESTID + ": " + config.getPullRequestId());

    logger.println(
        FIELD_USERNAMEPASSWORDCREDENTIALSID
            + ": "
            + !isNullOrEmpty(config.getUsernamePasswordCredentialsId()));
    logger.println(
        FIELD_USEOAUTH2TOKENCREDENTIALS
            + ": "
            + !isNullOrEmpty(config.getOAuth2TokenCredentialsId()));
    logger.println(FIELD_OAUTH2TOKEN + ": " + !isNullOrEmpty(config.getOAuth2Token()));

    logger.println(FIELD_CREATESINGLEFILECOMMENTS + ": " + config.getCreateSingleFileComments());
    logger.println(
        FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS
            + ": "
            + config.getCreateCommentWithAllSingleFileComments());
    logger.println(FIELD_COMMENTONLYCHANGEDCONTENT + ": " + config.getCommentOnlyChangedContent());

    logger.println(FIELD_MINSEVERITY + ": " + config.getMinSeverity());

    logger.println(FIELD_KEEP_OLD_COMMENTS + ": " + config.isKeepOldComments());

    for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
      logger.println(
          violationConfig.getReporter() + " with pattern " + violationConfig.getPattern());
    }
  }

  private static void setupFindBugsMessages() {
    try {
      final String findbugsMessagesXml =
          CharStreams.toString(
              new InputStreamReader(
                  JvctgPerformer.class.getResourceAsStream("findbugs-messages.xml"), UTF_8));
      setFindbugsMessagesXml(findbugsMessagesXml);
    } catch (final IOException e) {
      propagate(e);
    }
  }
}
