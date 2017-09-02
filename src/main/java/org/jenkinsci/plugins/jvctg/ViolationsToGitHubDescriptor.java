package org.jenkinsci.plugins.jvctg;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_COMMENTONLYCHANGEDCONTENT;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_CREATESINGLEFILECOMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_GITHUBURL;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_KEEP_OLD_COMMENTS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_MINSEVERITY;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_OAUTH2TOKEN;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_OAUTH2TOKENCREDENTIALSID;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_PASSWORD;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_PATTERN;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_PULLREQUESTID;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPORTER;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYNAME;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_REPOSITORYOWNER;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USEOAUTH2TOKEN;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USEOAUTH2TOKENCREDENTIALS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USERNAME;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USERNAMEPASSWORDCREDENTIALSID;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USEUSERNAMEPASSWORD;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_USEUSERNAMEPASSWORDCREDENTIALS;
import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.createNewConfig;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.ListBoxModel;

import java.util.List;

import net.sf.json.JSONObject;

import org.jenkinsci.plugins.jvctg.config.CredentialsHelper;
import org.jenkinsci.plugins.jvctg.config.ViolationConfig;
import org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfig;
import org.kohsuke.stapler.StaplerRequest;

import se.bjurr.violations.lib.model.SEVERITY;

public final class ViolationsToGitHubDescriptor extends BuildStepDescriptor<Publisher> {
  private ViolationsToGitHubConfig config;

  public ViolationsToGitHubDescriptor() {
    super(ViolationsToGitHubRecorder.class);
    load();
    if (this.config == null
        || this.config.getViolationConfigs().size()
            != createNewConfig().getViolationConfigs().size()) {
      this.config = createNewConfig();
    }
  }

  public ListBoxModel doFillOAuth2TokenCredentialsIdItems() {
    return CredentialsHelper.doFillOAuth2TokenCredentialsIdItems();
  }

  public ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
    return CredentialsHelper.doFillUsernamePasswordCredentialsIdItems();
  }

  @Override
  public String getDisplayName() {
    return "Report Violations to GitHub";
  }

  @Override
  public String getHelpFile() {
    return super.getHelpFile();
  }

  /** Create new blank configuration. Used when job is created. */
  public ViolationsToGitHubConfig getNewConfig() {
    return createNewConfig();
  }

  @Override
  public boolean isApplicable(
      @SuppressWarnings("rawtypes") final Class<? extends AbstractProject> jobType) {
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Publisher newInstance(StaplerRequest req, JSONObject formData)
      throws hudson.model.Descriptor.FormException {
    final ViolationsToGitHubConfig config = createNewConfig();
    config.setGitHubUrl(formData.getString(FIELD_GITHUBURL));
    config.setRepositoryOwner(formData.getString(FIELD_REPOSITORYOWNER));
    config.setRepositoryName(formData.getString(FIELD_REPOSITORYNAME));
    config.setPullRequestId(formData.getString(FIELD_PULLREQUESTID));

    config.setUseUsernamePassword(formData.getBoolean(FIELD_USEUSERNAMEPASSWORD));
    config.setUsername(formData.getString(FIELD_USERNAME));
    config.setPassword(formData.getString(FIELD_PASSWORD));

    config.setUseOAuth2Token(formData.getBoolean(FIELD_USEOAUTH2TOKEN));
    config.setoAuth2Token(formData.getString(FIELD_OAUTH2TOKEN));
    config.setUseOAuth2TokenCredentials(formData.getBoolean(FIELD_USEOAUTH2TOKENCREDENTIALS));
    config.setOAuth2TokenCredentialsId(formData.getString(FIELD_OAUTH2TOKENCREDENTIALSID));

    config.setUseUsernamePasswordCredentials(
        formData.getBoolean(FIELD_USEUSERNAMEPASSWORDCREDENTIALS));
    config.setUsernamePasswordCredentialsId(
        formData.getString(FIELD_USERNAMEPASSWORDCREDENTIALSID));

    final String minSeverityString = formData.getString(FIELD_MINSEVERITY);
    if (!isNullOrEmpty(minSeverityString)) {
      config.setMinSeverity(SEVERITY.valueOf(minSeverityString));
    } else {
      config.setMinSeverity(null);
    }

    config.setCreateCommentWithAllSingleFileComments(
        formData.getString(FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS).equalsIgnoreCase("true"));
    config.setCreateSingleFileComments(
        formData.getString(FIELD_CREATESINGLEFILECOMMENTS).equalsIgnoreCase("true"));
    config.setCommentOnlyChangedContent(
        formData.getString(FIELD_COMMENTONLYCHANGEDCONTENT).equalsIgnoreCase("true"));
    config.setKeepOldComments(formData.getString(FIELD_KEEP_OLD_COMMENTS).equalsIgnoreCase("true"));
    int i = 0;
    final List<String> patterns = (List<String>) formData.get(FIELD_PATTERN);
    final List<String> reporters = (List<String>) formData.get(FIELD_REPORTER);
    for (final String pattern : patterns) {
      final ViolationConfig violationConfig = config.getViolationConfigs().get(i);
      violationConfig.setPattern(pattern);
      final String reporter = reporters.get(i);
      violationConfig.setReporter(reporter);
      i++;
    }
    final ViolationsToGitHubRecorder publisher = new ViolationsToGitHubRecorder();
    publisher.setConfig(config);
    return publisher;
  }
}
