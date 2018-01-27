package org.jenkinsci.plugins.jvctg.config;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.jenkinsci.plugins.jvctg.config.CredentialsHelper.migrateCredentials;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jvctg.ViolationsToGitHubConfiguration;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import se.bjurr.violations.lib.model.SEVERITY;

public class ViolationsToGitHubConfig extends AbstractDescribableImpl<ViolationsToGitHubConfig>
    implements Serializable {
  private static final long serialVersionUID = 4851568645021422528L;

  private boolean commentOnlyChangedContent;
  private boolean createCommentWithAllSingleFileComments;
  private boolean createSingleFileComments;
  private String gitHubUrl;
  private String oAuth2Token;
  private String oAuth2TokenCredentialsId;
  @Deprecated private transient String password;
  private String pullRequestId;
  private String repositoryName;
  private String repositoryOwner;
  @Deprecated private transient String username;
  private String usernamePasswordCredentialsId;
  private List<ViolationConfig> violationConfigs = newArrayList();
  private SEVERITY minSeverity;

  private boolean keepOldComments;

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
    this.usernamePasswordCredentialsId = rhs.usernamePasswordCredentialsId;
    this.oAuth2TokenCredentialsId = rhs.oAuth2TokenCredentialsId;
    this.minSeverity = rhs.minSeverity;
    this.keepOldComments = rhs.keepOldComments;
  }

  public void applyDefaults(final ViolationsToGitHubConfiguration defaults) {
    if (isNullOrEmpty(this.gitHubUrl)) {
      this.gitHubUrl = defaults.getGitHubUrl();
    }

    if (isNullOrEmpty(this.usernamePasswordCredentialsId)) {
      this.usernamePasswordCredentialsId = defaults.getUsernamePasswordCredentialsId();
    }

    if (isNullOrEmpty(this.oAuth2TokenCredentialsId)) {
      this.oAuth2TokenCredentialsId = defaults.getOAuth2TokenCredentialsId();
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
    return this.oAuth2Token;
  }

  public String getOAuth2TokenCredentialsId() {
    return this.oAuth2TokenCredentialsId;
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

  public String getUsernamePasswordCredentialsId() {
    return this.usernamePasswordCredentialsId;
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

  @DataBoundSetter
  public void setoAuth2TokenCredentialsId(final String oAuth2TokenCredentialsId) {
    this.oAuth2TokenCredentialsId = oAuth2TokenCredentialsId;
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
  public void setUsernamePasswordCredentialsId(final String usernamePasswordCredentialsId) {
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
  }

  @DataBoundSetter
  public void setViolationConfigs(final List<ViolationConfig> parsers) {
    this.violationConfigs = parsers;
  }

  private Object readResolve() {
    if (StringUtils.isBlank(usernamePasswordCredentialsId)
        && username != null
        && password != null) {
      usernamePasswordCredentialsId = migrateCredentials(username, password);
    }
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
        + ", oAuth2TokenCredentialsId="
        + oAuth2TokenCredentialsId
        + ", pullRequestId="
        + pullRequestId
        + ", repositoryName="
        + repositoryName
        + ", repositoryOwner="
        + repositoryOwner
        + ", usernamePasswordCredentialsId="
        + usernamePasswordCredentialsId
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
    result =
        prime * result
            + (oAuth2TokenCredentialsId == null ? 0 : oAuth2TokenCredentialsId.hashCode());
    result = prime * result + (pullRequestId == null ? 0 : pullRequestId.hashCode());
    result = prime * result + (repositoryName == null ? 0 : repositoryName.hashCode());
    result = prime * result + (repositoryOwner == null ? 0 : repositoryOwner.hashCode());
    result =
        prime * result
            + (usernamePasswordCredentialsId == null
                ? 0
                : usernamePasswordCredentialsId.hashCode());
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
    if (oAuth2TokenCredentialsId == null) {
      if (other.oAuth2TokenCredentialsId != null) {
        return false;
      }
    } else if (!oAuth2TokenCredentialsId.equals(other.oAuth2TokenCredentialsId)) {
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
    if (usernamePasswordCredentialsId == null) {
      if (other.usernamePasswordCredentialsId != null) {
        return false;
      }
    } else if (!usernamePasswordCredentialsId.equals(other.usernamePasswordCredentialsId)) {
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
    @Nonnull
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

    public ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
      return CredentialsHelper.doFillUsernamePasswordCredentialsIdItems();
    }

    public ListBoxModel doFillOAuth2TokenCredentialsIdItems() {
      return CredentialsHelper.doFillOAuth2TokenCredentialsIdItems();
    }
  }
}
