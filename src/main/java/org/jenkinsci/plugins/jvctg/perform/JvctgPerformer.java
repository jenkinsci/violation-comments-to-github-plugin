package org.jenkinsci.plugins.jvctg.perform;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
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
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_OAUTH2TOKEN;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_PASSWORD;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYNAME;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYOWNER;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USEOAUTH2TOKENCREDENTIALS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USERNAME;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USERNAMEPASSWORDCREDENTIALSID;
import static se.bjurr.violations.comments.github.lib.ViolationCommentsToGitHubApi.violationCommentsToGitHubApi;
import static se.bjurr.violations.lib.ViolationsReporterApi.violationsReporterApi;
import static se.bjurr.violations.lib.parsers.FindbugsParser.setFindbugsMessagesXml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import se.bjurr.violations.lib.model.Violation;

public class JvctgPerformer {

 @VisibleForTesting
 public static void doPerform(final ViolationsToGitHubConfig config, final File workspace, final TaskListener listener)
   throws MalformedURLException {
  if (isNullOrEmpty(config.getPullRequestId())) {
   listener.getLogger().println("No pull request id defined, will not send violation comments to GitHub.");
   return;
  }
  final Integer pullRequestIdInt = Integer.valueOf(config.getPullRequestId());

  final List<Violation> allParsedViolations = newArrayList();
  for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
   if (!isNullOrEmpty(violationConfig.getPattern())) {
    final List<Violation> parsedViolations = violationsReporterApi()//
      .findAll(violationConfig.getReporter())//
      .inFolder(workspace.getAbsolutePath())//
      .withPattern(violationConfig.getPattern())//
      .violations();
    allParsedViolations.addAll(parsedViolations);
    listener.getLogger().println("Found " + parsedViolations.size() + " violations from " + violationConfig + ".");
   }
  }

  String oAuth2Token = null;
  String username = null;
  String password = null;
  if (config.isUseOAuth2Token()) {
   oAuth2Token = checkNotNull(emptyToNull(config.getOAuth2Token()), "OAuth2Token selected but not set!");
   listener.getLogger().println("Using OAuth2Token");
  } else {
   username = checkNotNull(emptyToNull(config.getUsername()), "username not set!");
   password = checkNotNull(emptyToNull(config.getPassword()), "password not set!");
   listener.getLogger().println("Using username / password");
  }

  listener.getLogger().println("Will comment PR " + config.getRepositoryOwner() + "/" + config.getRepositoryName() + "/"
    + config.getPullRequestId() + " on " + config.getGitHubUrl());

  try {
   violationCommentsToGitHubApi()//
     .withoAuth2Token(oAuth2Token)//
     .withUsername(username)//
     .withPassword(password)//
     .withGitHubUrl(config.getGitHubUrl())//
     .withPullRequestId(pullRequestIdInt)//
     .withRepositoryName(config.getRepositoryName())//
     .withRepositoryOwner(config.getRepositoryOwner())//
     .withViolations(allParsedViolations)//
     .withCreateCommentWithAllSingleFileComments(config.getCreateCommentWithAllSingleFileComments())//
     .withCreateSingleFileComments(config.getCreateSingleFileComments())//
     .withCommentOnlyChangedContent(config.getCommentOnlyChangedContent())//
     .toPullRequest();
  } catch (final Exception e) {
   Logger.getLogger(JvctgPerformer.class.getName()).log(SEVERE, "", e);
   final StringWriter sw = new StringWriter();
   e.printStackTrace(new PrintWriter(sw));
   listener.getLogger().println(sw.toString());
  }
 }

 /**
  * Makes sure any Jenkins variable, used in the configuration fields, are
  * evaluated.
  */
 @VisibleForTesting
 static ViolationsToGitHubConfig expand(final ViolationsToGitHubConfig config, final EnvVars environment) {
  final ViolationsToGitHubConfig expanded = new ViolationsToGitHubConfig();
  expanded.setGitHubUrl(environment.expand(config.getGitHubUrl()));
  expanded.setPullRequestId(environment.expand(config.getPullRequestId()));
  expanded.setRepositoryName(environment.expand(config.getRepositoryName()));
  expanded.setRepositoryOwner(environment.expand(config.getRepositoryOwner()));
  expanded.setCreateCommentWithAllSingleFileComments(config.getCreateCommentWithAllSingleFileComments());
  expanded.setCreateSingleFileComments(config.getCreateSingleFileComments());
  expanded.setCommentOnlyChangedContent(config.getCommentOnlyChangedContent());

  expanded.setUseUsernamePassword(config.isUseUsernamePassword());
  expanded.setUsername(environment.expand(config.getUsername()));
  expanded.setPassword(environment.expand(config.getPassword()));
  expanded.setUseUsernamePasswordCredentials(config.isUseUsernamePasswordCredentials());
  expanded.setUsernamePasswordCredentialsId(config.getUsernamePasswordCredentialsId());

  expanded.setUseOAuth2Token(config.isUseOAuth2Token());
  expanded.setoAuth2Token(environment.expand(config.getOAuth2Token()));
  expanded.setUseOAuth2TokenCredentials(config.isUseOAuth2TokenCredentials());
  expanded.setoAuth2TokenCredentialsId(config.getoAuth2TokenCredentialsId());

  for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
   final ViolationConfig p = new ViolationConfig();
   p.setPattern(environment.expand(violationConfig.getPattern()));
   p.setReporter(violationConfig.getReporter());
   expanded.getViolationConfigs().add(p);
  }
  return expanded;
 }

 public static void jvctsPerform(final ViolationsToGitHubConfig configUnexpanded, final FilePath fp,
   final Run<?, ?> build, final TaskListener listener) {
  try {
   final EnvVars env = build.getEnvironment(listener);
   final ViolationsToGitHubConfig configExpanded = expand(configUnexpanded, env);
   listener.getLogger().println("---");
   listener.getLogger().println("--- Jenkins Violation Comments to GitHub ---");
   listener.getLogger().println("---");
   logConfiguration(configExpanded, build, listener);

   setUsernamePasswordCredentials(configExpanded, listener);
   setOAuth2TokenCredentials(configExpanded, listener);

   listener.getLogger().println("Running Jenkins Violation Comments To GitHub");
   listener.getLogger().println("Will comment " + configExpanded.getPullRequestId());

   fp.act(new FileCallable<Void>() {

    private static final long serialVersionUID = 6166111757469534436L;

    @Override
    public void checkRoles(final RoleChecker checker) throws SecurityException {

    }

    @Override
    public Void invoke(final File workspace, final VirtualChannel channel) throws IOException, InterruptedException {
     setupFindBugsMessages();
     listener.getLogger().println("Workspace: " + workspace.getAbsolutePath());
     doPerform(configExpanded, workspace, listener);
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

 private static void logConfiguration(final ViolationsToGitHubConfig config, final Run<?, ?> build,
   final TaskListener listener) {
  listener.getLogger().println(FIELD_GITHUBURL + ": " + config.getGitHubUrl());
  listener.getLogger().println(FIELD_REPOSITORYOWNER + ": " + config.getRepositoryOwner());
  listener.getLogger().println(FIELD_REPOSITORYNAME + ": " + config.getRepositoryName());
  listener.getLogger().println(FIELD_PULLREQUESTID + ": " + config.getPullRequestId());

  listener.getLogger()
    .println(FIELD_USERNAMEPASSWORDCREDENTIALSID + ": " + !isNullOrEmpty(config.getUsernamePasswordCredentialsId()));
  listener.getLogger().println(FIELD_USERNAME + ": " + !isNullOrEmpty(config.getUsername()));
  listener.getLogger().println(FIELD_PASSWORD + ": " + !isNullOrEmpty(config.getPassword()));
  listener.getLogger()
    .println(FIELD_USEOAUTH2TOKENCREDENTIALS + ": " + !isNullOrEmpty(config.getoAuth2TokenCredentialsId()));
  listener.getLogger().println(FIELD_OAUTH2TOKEN + ": " + !isNullOrEmpty(config.getOAuth2Token()));

  listener.getLogger().println(FIELD_CREATESINGLEFILECOMMENTS + ": " + config.getCreateSingleFileComments());
  listener.getLogger()
    .println(FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS + ": " + config.getCreateCommentWithAllSingleFileComments());
  listener.getLogger().println(FIELD_COMMENTONLYCHANGEDCONTENT + ": " + config.getCommentOnlyChangedContent());

  for (final ViolationConfig violationConfig : config.getViolationConfigs()) {
   listener.getLogger().println(violationConfig.getReporter() + " with pattern " + violationConfig.getPattern());
  }
 }

 private static void setOAuth2TokenCredentials(final ViolationsToGitHubConfig configExpanded,
   final TaskListener listener) {
  if (configExpanded.isUseOAuth2TokenCredentials()) {
   final String getoAuth2TokenCredentialsId = configExpanded.getoAuth2TokenCredentialsId();
   if (!isNullOrEmpty(getoAuth2TokenCredentialsId)) {
    final Optional<StringCredentials> credentials = findOAuth2TokenCredentials(getoAuth2TokenCredentialsId);
    if (credentials.isPresent()) {
     final StringCredentials stringCredential = checkNotNull(credentials.get(),
       "Credentials OAuth2 token selected but not set!");
     configExpanded.setoAuth2Token(stringCredential.getSecret().getPlainText());
     configExpanded.setUseOAuth2Token(true);
     listener.getLogger().println("Using OAuth2 token from credentials");
    } else {
     listener.getLogger().println("OAuth2 credentials not found!");
     return;
    }
   } else {
    listener.getLogger().println("OAuth2 credentials checked but not selected!");
    return;
   }
  }
 }

 private static void setupFindBugsMessages() {
  try {
   final String findbugsMessagesXml = CharStreams
     .toString(new InputStreamReader(JvctgPerformer.class.getResourceAsStream("findbugs-messages.xml"), UTF_8));
   setFindbugsMessagesXml(findbugsMessagesXml);
  } catch (final IOException e) {
   propagate(e);
  }
 }

 private static void setUsernamePasswordCredentials(final ViolationsToGitHubConfig configExpanded,
   final TaskListener listener) {
  if (configExpanded.isUseUsernamePasswordCredentials()) {
   final String usernamePasswordCredentialsId = configExpanded.getUsernamePasswordCredentialsId();
   if (!isNullOrEmpty(usernamePasswordCredentialsId)) {
    final Optional<StandardUsernamePasswordCredentials> credentials = findUsernamePasswordCredentials(
      usernamePasswordCredentialsId);
    if (credentials.isPresent()) {
     final String username = checkNotNull(emptyToNull(credentials.get().getUsername()),
       "Credentials username selected but not set!");
     final String password = checkNotNull(emptyToNull(credentials.get().getPassword().getPlainText()),
       "Credentials password selected but not set!");
     configExpanded.setUsername(username);
     configExpanded.setPassword(password);
     configExpanded.setUseUsernamePassword(true);
     listener.getLogger().println("Using username and password from credentials");
    } else {
     listener.getLogger().println("Username credentials not found!");
     return;
    }
   } else {
    listener.getLogger().println("Username credentials checked but not selected!");
    return;
   }
  }
 }
}
