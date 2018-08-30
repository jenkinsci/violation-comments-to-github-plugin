package org.jenkinsci.plugins.jvctg.config;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import java.io.Serializable;
import java.util.List;
import org.jenkinsci.plugins.jvctg.ViolationsToGitHubConfiguration;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import se.bjurr.violations.lib.model.SEVERITY;

public class ViolationsToGitHubConfig extends AbstractDescribableImpl<ViolationsToGitHubConfig>
    implements Serializable {
  private static final long serialVersionUID = 4851568645021422528L;

  private boolean commentOnlyChangedContent;
  private boolean createCommentWithAllSingleFileComments;
  private boolean createSingleFileComments;
  private String gitHubUrl;
  private String oAuth2Token;
  private String pullRequestId;
  private String repositoryName;
  private String repositoryOwner;
  private String credentialsId;
  private List<ViolationConfig> violationConfigs = newArrayList();
  private SEVERITY minSeverity;
  @Deprecated private transient String username;
  @Deprecated private transient String password;
  @Deprecated private transient String usernamePasswordCredentialsId;
  @Deprecated private transient String oAuth2TokenCredentialsId;

  private boolean keepOldComments;
  private String commentTemplate;

  public ViolationsToGitHubConfig() {}

  @DataBoundConstructor
  public ViolationsToGitHubConfig(
      final String repositoryName,
      final String repositoryOwner,
      final String pullRequestId,
      final String gitHubUrl) {
    this.repositoryName = repositoryName;
    this.repositoryOwner = repositoryOwner;
    this.pullRequestId = pullRequestId;
    this.gitHubUrl = gitHubUrl;
  }

  public ViolationsToGitHubConfig(final ViolationsToGitHubConfig rhs) {
    this.violationConfigs = rhs.violationConfigs;
    this.createSingleFileComments = rhs.createSingleFileComments;
    this.createCommentWithAllSingleFileComments = rhs.createCommentWithAllSingleFileComments;
    this.repositoryName = rhs.repositoryName;
    this.repositoryOwner = rhs.repositoryOwner;
    this.oAuth2Token = rhs.oAuth2Token;
    this.pullRequestId = rhs.pullRequestId;
    this.gitHubUrl = rhs.gitHubUrl;
    this.commentOnlyChangedContent = rhs.commentOnlyChangedContent;
    this.credentialsId = rhs.credentialsId;
    this.minSeverity = rhs.minSeverity;
    this.keepOldComments = rhs.keepOldComments;
    this.commentTemplate = rhs.commentTemplate;
  }

  public void applyDefaults(final ViolationsToGitHubConfiguration defaults) {
    if (isNullOrEmpty(this.gitHubUrl)) {
      this.gitHubUrl = defaults.getGitHubUrl();
    }

    if (isNullOrEmpty(this.credentialsId)) {
      this.credentialsId = defaults.getCredentialsId();
    }

    if (isNullOrEmpty(this.oAuth2Token)) {
      this.oAuth2Token = defaults.getoAuth2Token();
    }

    if (isNullOrEmpty(this.repositoryOwner)) {
      this.repositoryOwner = defaults.getRepositoryOwner();
    }
    if (this.minSeverity == null) {
      this.minSeverity = defaults.getMinSeverity();
    }
  }

  public boolean getCommentOnlyChangedContent() {
    return this.commentOnlyChangedContent;
  }

  public boolean getCreateCommentWithAllSingleFileComments() {
    return this.createCommentWithAllSingleFileComments;
  }

  public boolean getCreateSingleFileComments() {
    return this.createSingleFileComments;
  }

  public String getGitHubUrl() {
    return this.gitHubUrl;
  }

  public String getoAuth2Token() {
    return this.oAuth2Token;
  }

  public String getOAuth2Token() {
    return oAuth2Token;
  }

  public String getPullRequestId() {
    return this.pullRequestId;
  }

  public String getRepositoryName() {
    return this.repositoryName;
  }

  public String getRepositoryOwner() {
    return this.repositoryOwner;
  }

  public String getCredentialsId() {
    return this.credentialsId;
  }

  public SEVERITY getMinSeverity() {
    return minSeverity;
  }

  @DataBoundSetter
  public void setMinSeverity(final SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public List<ViolationConfig> getViolationConfigs() {
    return this.violationConfigs;
  }

  @DataBoundSetter
  public void setCommentOnlyChangedContent(final boolean commentOnlyChangedContent) {
    this.commentOnlyChangedContent = commentOnlyChangedContent;
  }

  @DataBoundSetter
  public void setCreateCommentWithAllSingleFileComments(
      final boolean createCommentWithAllSingleFileComments) {
    this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
  }

  @DataBoundSetter
  public void setCreateSingleFileComments(final boolean createSingleFileComments) {
    this.createSingleFileComments = createSingleFileComments;
  }

  public void setGitHubUrl(final String gitHubUrl) {
    this.gitHubUrl = gitHubUrl;
  }

  @DataBoundSetter
  public void setoAuth2Token(final String oAuth2Token) {
    this.oAuth2Token = oAuth2Token;
  }

  public void setPullRequestId(final String string) {
    this.pullRequestId = string;
  }

  public void setRepositoryName(final String repositoryName) {
    this.repositoryName = repositoryName;
  }

  public void setRepositoryOwner(final String repositoryOwner) {
    this.repositoryOwner = repositoryOwner;
  }

  @DataBoundSetter
  public void setCredentialsId(final String credentialsId) {
    this.credentialsId = credentialsId;
  }

  @DataBoundSetter
  public void setViolationConfigs(final List<ViolationConfig> parsers) {
    this.violationConfigs = parsers;
  }

  public String getCommentTemplate() {
    return commentTemplate;
  }

  @DataBoundSetter
  public void setCommentTemplate(final String commentTemplate) {
    this.commentTemplate = commentTemplate;
  }

  private Object readResolve() {
    credentialsId =
        CredentialsHelper.checkCredentials(
            credentialsId,
            oAuth2TokenCredentialsId,
            usernamePasswordCredentialsId,
            username,
            password);
    return this;
  }

  @Override
  public String toString() {
    return "ViolationsToGitHubConfig [commentOnlyChangedContent="
        + commentOnlyChangedContent
        + ", createCommentWithAllSingleFileComments="
        + createCommentWithAllSingleFileComments
        + ", createSingleFileComments="
        + createSingleFileComments
        + ", gitHubUrl="
        + gitHubUrl
        + ", oAuth2Token="
        + oAuth2Token
        + ", pullRequestId="
        + pullRequestId
        + ", repositoryName="
        + repositoryName
        + ", repositoryOwner="
        + repositoryOwner
        + ", credentialsId="
        + credentialsId
        + ", violationConfigs="
        + violationConfigs
        + ", minSeverity="
        + minSeverity
        + ", keepOldComments="
        + keepOldComments
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (commentOnlyChangedContent ? 1231 : 1237);
    result = prime * result + (createCommentWithAllSingleFileComments ? 1231 : 1237);
    result = prime * result + (createSingleFileComments ? 1231 : 1237);
    result = prime * result + (gitHubUrl == null ? 0 : gitHubUrl.hashCode());
    result = prime * result + (keepOldComments ? 1231 : 1237);
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (oAuth2Token == null ? 0 : oAuth2Token.hashCode());
    result = prime * result + (pullRequestId == null ? 0 : pullRequestId.hashCode());
    result = prime * result + (repositoryName == null ? 0 : repositoryName.hashCode());
    result = prime * result + (repositoryOwner == null ? 0 : repositoryOwner.hashCode());
    result = prime * result + (credentialsId == null ? 0 : credentialsId.hashCode());
    result = prime * result + (violationConfigs == null ? 0 : violationConfigs.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ViolationsToGitHubConfig other = (ViolationsToGitHubConfig) obj;
    if (commentOnlyChangedContent != other.commentOnlyChangedContent) {
      return false;
    }
    if (createCommentWithAllSingleFileComments != other.createCommentWithAllSingleFileComments) {
      return false;
    }
    if (createSingleFileComments != other.createSingleFileComments) {
      return false;
    }
    if (gitHubUrl == null) {
      if (other.gitHubUrl != null) {
        return false;
      }
    } else if (!gitHubUrl.equals(other.gitHubUrl)) {
      return false;
    }
    if (keepOldComments != other.keepOldComments) {
      return false;
    }
    if (minSeverity != other.minSeverity) {
      return false;
    }
    if (oAuth2Token == null) {
      if (other.oAuth2Token != null) {
        return false;
      }
    } else if (!oAuth2Token.equals(other.oAuth2Token)) {
      return false;
    }
    if (pullRequestId == null) {
      if (other.pullRequestId != null) {
        return false;
      }
    } else if (!pullRequestId.equals(other.pullRequestId)) {
      return false;
    }
    if (repositoryName == null) {
      if (other.repositoryName != null) {
        return false;
      }
    } else if (!repositoryName.equals(other.repositoryName)) {
      return false;
    }
    if (repositoryOwner == null) {
      if (other.repositoryOwner != null) {
        return false;
      }
    } else if (!repositoryOwner.equals(other.repositoryOwner)) {
      return false;
    }
    if (credentialsId == null) {
      if (other.credentialsId != null) {
        return false;
      }
    } else if (!credentialsId.equals(other.credentialsId)) {
      return false;
    }
    if (violationConfigs == null) {
      if (other.violationConfigs != null) {
        return false;
      }
    } else if (!violationConfigs.equals(other.violationConfigs)) {
      return false;
    }
    return true;
  }

  public boolean isKeepOldComments() {
    return keepOldComments;
  }

  @DataBoundSetter
  public void setKeepOldComments(final boolean keepOldComments) {
    this.keepOldComments = keepOldComments;
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<ViolationsToGitHubConfig> {
    @NonNull
    @Override
    public String getDisplayName() {
      return "Violations To GitHub Server Config";
    }

    @Restricted(NoExternalUse.class)
    public ListBoxModel doFillMinSeverityItems() {
      final ListBoxModel items = new ListBoxModel();
      items.add("Default, Global Config or Info", "");
      for (final SEVERITY severity : SEVERITY.values()) {
        items.add(severity.name());
      }
      return items;
    }

    @SuppressWarnings("unused") // Used by stapler
    public ListBoxModel doFillCredentialsIdItems(
        @AncestorInPath final Item item,
        @QueryParameter final String credentialsId,
        @QueryParameter final String gitHubUrl) {
      return CredentialsHelper.doFillCredentialsIdItems(item, credentialsId, gitHubUrl);
    }

    @SuppressWarnings("unused") // Used by stapler
    public FormValidation doCheckCredentialsId(
        @AncestorInPath final Item item,
        @QueryParameter final String value,
        @QueryParameter final String gitHubUrl) {
      return CredentialsHelper.doCheckFillCredentialsId(item, value, gitHubUrl);
    }
  }
}
