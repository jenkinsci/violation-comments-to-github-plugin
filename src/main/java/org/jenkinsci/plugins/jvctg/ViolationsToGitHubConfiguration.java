package org.jenkinsci.plugins.jvctg;

import java.io.Serializable;

import org.jenkinsci.plugins.jvctg.config.CredentialsHelper;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import se.bjurr.violations.lib.model.SEVERITY;

/** Created by magnayn on 07/04/2016. */
@Extension
public class ViolationsToGitHubConfiguration extends GlobalConfiguration implements Serializable {

  private static final long serialVersionUID = -2832851253933848205L;

  /**
   * Returns this singleton instance.
   *
   * @return the singleton.
   */
  public static ViolationsToGitHubConfiguration get() {
    return GlobalConfiguration.all().get(ViolationsToGitHubConfiguration.class);
  }

  public String gitHubUrl;
  public String oAuth2Token;
  public String password;
  public String repositoryOwner;
  public String username;
  private String oAuth2TokenCredentialsId;
  private String usernamePasswordCredentialsId;
  private SEVERITY minSeverity;

  public ViolationsToGitHubConfiguration() {
    load();
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
    req.bindJSON(this, json);
    save();
    return true;
  }

  public ListBoxModel doFillOAuth2TokenCredentialsIdItems() {
    return CredentialsHelper.doFillOAuth2TokenCredentialsIdItems();
  }

  public ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
    return CredentialsHelper.doFillUsernamePasswordCredentialsIdItems();
  }

  public String getGitHubUrl() {
    return this.gitHubUrl;
  }

  public String getoAuth2Token() {
    return this.oAuth2Token;
  }

  public String getOAuth2TokenCredentialsId() {
    return this.oAuth2TokenCredentialsId;
  }

  public String getPassword() {
    return this.password;
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

  @DataBoundSetter
  public void setMinSeverity(SEVERITY minSeverity) {
    this.minSeverity = minSeverity;
  }

  @DataBoundSetter
  public void setGitHubUrl(String gitHubUrl) {
    this.gitHubUrl = gitHubUrl;
  }

  @DataBoundSetter
  public void setoAuth2Token(String oAuth2Token) {
    this.oAuth2Token = oAuth2Token;
  }

  @DataBoundSetter
  public void setoAuth2TokenCredentialsId(String oAuth2TokenCredentialsId) {
    this.oAuth2TokenCredentialsId = oAuth2TokenCredentialsId;
  }

  @DataBoundSetter
  public void setPassword(String password) {
    this.password = password;
  }

  @DataBoundSetter
  public void setRepositoryOwner(String repositoryOwner) {
    this.repositoryOwner = repositoryOwner;
  }

  @DataBoundSetter
  public void setUsername(String username) {
    this.username = username;
  }

  @DataBoundSetter
  public void setUsernamePasswordCredentialsId(String usernamePasswordCredentialsId) {
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (gitHubUrl == null ? 0 : gitHubUrl.hashCode());
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (oAuth2Token == null ? 0 : oAuth2Token.hashCode());
    result =
        prime * result
            + (oAuth2TokenCredentialsId == null ? 0 : oAuth2TokenCredentialsId.hashCode());
    result = prime * result + (password == null ? 0 : password.hashCode());
    result = prime * result + (repositoryOwner == null ? 0 : repositoryOwner.hashCode());
    result = prime * result + (username == null ? 0 : username.hashCode());
    result =
        prime * result
            + (usernamePasswordCredentialsId == null
                ? 0
                : usernamePasswordCredentialsId.hashCode());
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
    ViolationsToGitHubConfiguration other = (ViolationsToGitHubConfiguration) obj;
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
    if (repositoryOwner == null) {
      if (other.repositoryOwner != null) {
        return false;
      }
    } else if (!repositoryOwner.equals(other.repositoryOwner)) {
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
    return true;
  }

  @Override
  public String toString() {
    return "ViolationsToGitHubConfiguration [gitHubUrl="
        + gitHubUrl
        + ", oAuth2Token="
        + oAuth2Token
        + ", password="
        + password
        + ", repositoryOwner="
        + repositoryOwner
        + ", username="
        + username
        + ", oAuth2TokenCredentialsId="
        + oAuth2TokenCredentialsId
        + ", usernamePasswordCredentialsId="
        + usernamePasswordCredentialsId
        + ", minSeverity="
        + minSeverity
        + "]";
  }
}
