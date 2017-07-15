package org.jenkinsci.plugins.jvctg.config;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.util.List;

import org.jenkinsci.plugins.jvctg.ViolationsToGitHubConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;

import se.bjurr.violations.lib.model.SEVERITY;

public class ViolationsToGitHubConfig implements Serializable {
  private static final long serialVersionUID = 4851568645021422528L;

  private boolean commentOnlyChangedContent;
  private boolean createCommentWithAllSingleFileComments;
  private boolean createSingleFileComments;
  private String gitHubUrl;
  private String oAuth2Token;
  private String oAuth2TokenCredentialsId;
  private String password;
  private String pullRequestId;
  private String repositoryName;
  private String repositoryOwner;
  private boolean useOAuth2Token;
  private boolean useOAuth2TokenCredentials;
  private String username;
  private String usernamePasswordCredentialsId;
  private boolean useUsernamePassword;
  private boolean useUsernamePasswordCredentials;
  private List<ViolationConfig> violationConfigs = newArrayList();
  private SEVERITY minSeverity;

  public ViolationsToGitHubConfig() {}

  @DataBoundConstructor
  public ViolationsToGitHubConfig(
      boolean createSingleFileComments,
      boolean createCommentWithAllSingleFileComments,
      String repositoryName,
      String repositoryOwner,
      String password,
      String username,
      String oAuth2Token,
      String pullRequestId,
      String gitHubUrl,
      boolean commentOnlyChangedContent,
      List<ViolationConfig> violationConfigs,
      String usernamePasswordCredentialsId,
      boolean useOAuth2Token,
      boolean useUsernamePasswordCredentials,
      boolean useUsernamePassword,
      String oAuth2TokenCredentialsId,
      boolean useOAuth2TokenCredentialsIdCredentials,
      SEVERITY minSeverity) {
    List<ViolationConfig> allViolationConfigs = includeAllReporters(violationConfigs);

    this.violationConfigs = allViolationConfigs;
    this.createSingleFileComments = createSingleFileComments;
    this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
    this.repositoryName = repositoryName;
    this.repositoryOwner = repositoryOwner;
    this.password = password;
    this.username = username;
    this.oAuth2Token = oAuth2Token;
    this.pullRequestId = pullRequestId;
    this.gitHubUrl = gitHubUrl;
    this.commentOnlyChangedContent = commentOnlyChangedContent;
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
    this.useOAuth2Token = useOAuth2Token;
    this.useUsernamePasswordCredentials = useUsernamePasswordCredentials;
    this.useUsernamePassword = useUsernamePassword;
    this.oAuth2TokenCredentialsId = oAuth2TokenCredentialsId;
    this.useOAuth2TokenCredentials = useOAuth2TokenCredentialsIdCredentials;
    this.minSeverity = minSeverity;
  }

  public ViolationsToGitHubConfig(ViolationsToGitHubConfig rhs) {
    this.violationConfigs = rhs.violationConfigs;
    this.createSingleFileComments = rhs.createSingleFileComments;
    this.createCommentWithAllSingleFileComments = rhs.createCommentWithAllSingleFileComments;
    this.repositoryName = rhs.repositoryName;
    this.repositoryOwner = rhs.repositoryOwner;
    this.password = rhs.password;
    this.username = rhs.username;
    this.oAuth2Token = rhs.oAuth2Token;
    this.pullRequestId = rhs.pullRequestId;
    this.gitHubUrl = rhs.gitHubUrl;
    this.commentOnlyChangedContent = rhs.commentOnlyChangedContent;
    this.usernamePasswordCredentialsId = rhs.usernamePasswordCredentialsId;
    this.useOAuth2Token = rhs.useOAuth2Token;
    this.useUsernamePasswordCredentials = rhs.useUsernamePasswordCredentials;
    this.useUsernamePassword = rhs.useUsernamePassword;
    this.oAuth2TokenCredentialsId = rhs.oAuth2TokenCredentialsId;
    this.useOAuth2TokenCredentials = rhs.useOAuth2TokenCredentials;
    this.minSeverity = rhs.minSeverity;
  }

  public void applyDefaults(ViolationsToGitHubConfiguration defaults) {
    if (isNullOrEmpty(this.gitHubUrl)) {
      this.gitHubUrl = defaults.getGitHubUrl();
    }

    if (isNullOrEmpty(this.usernamePasswordCredentialsId)) {
      this.usernamePasswordCredentialsId = defaults.getUsernamePasswordCredentialsId();
    }

    if (isNullOrEmpty(this.oAuth2TokenCredentialsId)) {
      this.oAuth2TokenCredentialsId = defaults.getoAuth2TokenCredentialsId();
    }

    if (isNullOrEmpty(this.username)) {
      this.username = defaults.getUsername();
    }

    if (isNullOrEmpty(this.password)) {
      this.password = defaults.getPassword();
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

  public String getoAuth2TokenCredentialsId() {
    return this.oAuth2TokenCredentialsId;
  }

  public String getPassword() {
    return this.password;
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

  public String getUsername() {
    return this.username;
  }

  public String getUsernamePasswordCredentialsId() {
    return this.usernamePasswordCredentialsId;
  }

  public SEVERITY getMinSeverity() {
    return minSeverity;
  }

  public void setMinSeverity(SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  public List<ViolationConfig> getViolationConfigs() {
    return this.violationConfigs;
  }

  public boolean isUseOAuth2Token() {
    return this.useOAuth2Token;
  }

  public boolean isUseOAuth2TokenCredentials() {
    return this.useOAuth2TokenCredentials;
  }

  public boolean isUseUsernamePassword() {
    return this.useUsernamePassword;
  }

  public boolean isUseUsernamePasswordCredentials() {
    return this.useUsernamePasswordCredentials;
  }

  public void setCommentOnlyChangedContent(boolean commentOnlyChangedContent) {
    this.commentOnlyChangedContent = commentOnlyChangedContent;
  }

  public void setCreateCommentWithAllSingleFileComments(
      boolean createCommentWithAllSingleFileComments) {
    this.createCommentWithAllSingleFileComments = createCommentWithAllSingleFileComments;
  }

  public void setCreateSingleFileComments(boolean createSingleFileComments) {
    this.createSingleFileComments = createSingleFileComments;
  }

  public void setGitHubUrl(String gitHubUrl) {
    this.gitHubUrl = gitHubUrl;
  }

  public void setoAuth2Token(String oAuth2Token) {
    this.oAuth2Token = oAuth2Token;
  }

  public void setoAuth2TokenCredentialsId(String oAuth2TokenCredentialsId) {
    this.oAuth2TokenCredentialsId = oAuth2TokenCredentialsId;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPullRequestId(String string) {
    this.pullRequestId = string;
  }

  public void setRepositoryName(String repositoryName) {
    this.repositoryName = repositoryName;
  }

  public void setRepositoryOwner(String repositoryOwner) {
    this.repositoryOwner = repositoryOwner;
  }

  public void setUseOAuth2Token(boolean useOAuth2Token) {
    this.useOAuth2Token = useOAuth2Token;
  }

  public void setUseOAuth2TokenCredentials(boolean useOAuth2TokenCredentials) {
    this.useOAuth2TokenCredentials = useOAuth2TokenCredentials;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setUsernamePasswordCredentialsId(String usernamePasswordCredentialsId) {
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
  }

  public void setUseUsernamePassword(boolean useUsernamePassword) {
    this.useUsernamePassword = useUsernamePassword;
  }

  public void setUseUsernamePasswordCredentials(boolean useUsernamePasswordCredentials) {
    this.useUsernamePasswordCredentials = useUsernamePasswordCredentials;
  }

  public void setViolationConfigs(List<ViolationConfig> parsers) {
    this.violationConfigs = parsers;
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
        + ", password="
        + password
        + ", pullRequestId="
        + pullRequestId
        + ", repositoryName="
        + repositoryName
        + ", repositoryOwner="
        + repositoryOwner
        + ", useOAuth2Token="
        + useOAuth2Token
        + ", useOAuth2TokenCredentials="
        + useOAuth2TokenCredentials
        + ", username="
        + username
        + ", usernamePasswordCredentialsId="
        + usernamePasswordCredentialsId
        + ", useUsernamePassword="
        + useUsernamePassword
        + ", useUsernamePasswordCredentials="
        + useUsernamePasswordCredentials
        + ", violationConfigs="
        + violationConfigs
        + ", minSeverity="
        + minSeverity
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
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (oAuth2Token == null ? 0 : oAuth2Token.hashCode());
    result =
        prime * result
            + (oAuth2TokenCredentialsId == null ? 0 : oAuth2TokenCredentialsId.hashCode());
    result = prime * result + (password == null ? 0 : password.hashCode());
    result = prime * result + (pullRequestId == null ? 0 : pullRequestId.hashCode());
    result = prime * result + (repositoryName == null ? 0 : repositoryName.hashCode());
    result = prime * result + (repositoryOwner == null ? 0 : repositoryOwner.hashCode());
    result = prime * result + (useOAuth2Token ? 1231 : 1237);
    result = prime * result + (useOAuth2TokenCredentials ? 1231 : 1237);
    result = prime * result + (useUsernamePassword ? 1231 : 1237);
    result = prime * result + (useUsernamePasswordCredentials ? 1231 : 1237);
    result = prime * result + (username == null ? 0 : username.hashCode());
    result =
        prime * result
            + (usernamePasswordCredentialsId == null
                ? 0
                : usernamePasswordCredentialsId.hashCode());
    result = prime * result + (violationConfigs == null ? 0 : violationConfigs.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ViolationsToGitHubConfig other = (ViolationsToGitHubConfig) obj;
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
    if (password == null) {
      if (other.password != null) {
        return false;
      }
    } else if (!password.equals(other.password)) {
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
    if (useOAuth2Token != other.useOAuth2Token) {
      return false;
    }
    if (useOAuth2TokenCredentials != other.useOAuth2TokenCredentials) {
      return false;
    }
    if (useUsernamePassword != other.useUsernamePassword) {
      return false;
    }
    if (useUsernamePasswordCredentials != other.useUsernamePasswordCredentials) {
      return false;
    }
    if (username == null) {
      if (other.username != null) {
        return false;
      }
    } else if (!username.equals(other.username)) {
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

  private List<ViolationConfig> includeAllReporters(List<ViolationConfig> violationConfigs) {
    List<ViolationConfig> allViolationConfigs =
        ViolationsToGitHubConfigHelper.getAllViolationConfigs();
    for (ViolationConfig candidate : allViolationConfigs) {
      for (ViolationConfig input : violationConfigs) {
        if (candidate.getParser() == input.getParser()) {
          candidate.setPattern(input.getPattern());
          candidate.setReporter(input.getReporter());
        }
      }
    }
    return allViolationConfigs;
  }
}
