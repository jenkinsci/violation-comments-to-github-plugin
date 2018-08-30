package org.jenkinsci.plugins.jvctg.perform;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.logging.Level.SEVERE;
import static org.jenkinsci.plugins.jvctg.config.CredentialsHelper.findCredentials;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_COMMENTONLYCHANGEDCONTENT;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_CREATESINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_CREDENTIALSID;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_GITHUBURL;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_KEEP_OLD_COMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_MINSEVERITY;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_OAUTH2TOKEN;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYNAME;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYOWNER;
import static se.bjurr.violations.comments.github.lib.ViolationCommentsToGitHubApi.violationCommentsToGitHubApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.parsers.FindbugsParser.setFindbugsMessagesXml;

import com.cloudbees.plugins.credentials.common.StandardCredentials;
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
import hudson.util.Secret;
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
import se.bjurr.violations.comments.github.lib.ViolationCommentsToGitHubApi;
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
      final StandardCredentials credentials,
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
      final ViolationCommentsToGitHubApi g = violationCommentsToGitHubApi();

      if (!isNullOrEmpty(config.getoAuth2Token())) {
        g //
            .withoAuth2Token(config.getoAuth2Token());
      } else if (credentials instanceof StringCredentials) {
        final StringCredentials token = (StringCredentials) credentials;
        g //
            .withoAuth2Token(Secret.toString(token.getSecret()));
      } else if (credentials instanceof StandardUsernamePasswordCredentials) {
        final StandardUsernamePasswordCredentials usernamePassword =
            (StandardUsernamePasswordCredentials) credentials;
        g //
            .withUsername(usernamePassword.getUsername()) //
            .withPassword(Secret.toString(usernamePassword.getPassword()));
      }
      final String commentTemplate = config.getCommentTemplate();
      g //
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
          .withCommentTemplate(commentTemplate) //
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

    expanded.setCredentialsId(config.getCredentialsId());
    expanded.setoAuth2Token(environment.expand(config.getoAuth2Token()));
    expanded.setKeepOldComments(config.isKeepOldComments());
    expanded.setCommentTemplate(config.getCommentTemplate());
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
    final PrintStream logger = listener.getLogger();
    try {
      final EnvVars env = build.getEnvironment(listener);
      final ViolationsToGitHubConfig configExpanded = expand(configUnexpanded, env);
      logger.println("---");
      logger.println("--- Jenkins Violation Comments to GitHub ---");
      logger.println("---");
      logConfiguration(configExpanded, build, listener);

      final Optional<StandardCredentials> credentials =
          findCredentials(
              build.getParent(), configExpanded.getCredentialsId(), configExpanded.getGitHubUrl());

      if (!isNullOrEmpty(configExpanded.getoAuth2Token())) {
        logger.println("Using OAuth2Token");
      } else if (credentials.isPresent()) {
        final StandardCredentials standardCredentials = credentials.get();
        if (standardCredentials instanceof StandardUsernamePasswordCredentials) {
          logger.println("Using username / password");
        } else if (standardCredentials instanceof StringCredentials) {
          logger.println("Using OAuth2Token credential style");
        }
      } else {
        throw new IllegalStateException("No credentials found!");
      }

      logger.println("Running Jenkins Violation Comments To GitHub");
      logger.println("PR " + configExpanded.getPullRequestId());

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
              doPerform(configExpanded, workspace, credentials.orNull(), listener);
              return null;
            }
          });
    } catch (final Exception e) {
      Logger.getLogger(JvctgPerformer.class.getName()).log(SEVERE, "", e);
      final StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      logger.println(sw.toString());
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

    logger.println(FIELD_CREDENTIALSID + ": " + !isNullOrEmpty(config.getCredentialsId()));
    logger.println(FIELD_OAUTH2TOKEN + ": " + !isNullOrEmpty(config.getoAuth2Token()));

    logger.println(FIELD_CREATESINGLEFILECOMMENTS + ": " + config.getCreateSingleFileComments());
    logger.println(
        FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS
            + ": "
            + config.getCreateCommentWithAllSingleFileComments());
    logger.println(FIELD_COMMENTONLYCHANGEDCONTENT + ": " + config.getCommentOnlyChangedContent());

    logger.println(FIELD_MINSEVERITY + ": " + config.getMinSeverity());

    logger.println(FIELD_KEEP_OLD_COMMENTS + ": " + config.isKeepOldComments());

    logger.println("commentTemplate: " + config.getCommentTemplate());

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
