package org.jenkinsci.plugins.jvctg;

import static org.jenkinsci.plugins.jvctg.config.CredentialsHelper.migrateCredentials;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.jvctg.config.CredentialsHelper;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import se.bjurr.violations.lib.model.SEVERITY;

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
  @Deprecated public transient String password;
  public String repositoryOwner;
  @Deprecated public transient String username;
  private String oAuth2TokenCredentialsId;
  private String usernamePasswordCredentialsId;
  private SEVERITY minSeverity = SEVERITY.INFO;

  public ViolationsToGitHubConfiguration() {
    load();
  }

  @Override
  public boolean configure(final StaplerRequest req, final JSONObject json) throws FormException {
    req.bindJSON(this, json);
    save();
    return true;
  }

  @Restricted(NoExternalUse.class)
  public ListBoxModel doFillMinSeverityItems() {
    final ListBoxModel items = new ListBoxModel();
    for (final SEVERITY severity : SEVERITY.values()) {
      items.add(severity.name());
    }
    return items;
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

  @DataBoundSetter
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

  @DataBoundSetter
  public void setRepositoryOwner(final String repositoryOwner) {
    this.repositoryOwner = repositoryOwner;
  }

  @DataBoundSetter
  public void setUsernamePasswordCredentialsId(final String usernamePasswordCredentialsId) {
    this.usernamePasswordCredentialsId = usernamePasswordCredentialsId;
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (gitHubUrl == null ? 0 : gitHubUrl.hashCode());
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (oAuth2Token == null ? 0 : oAuth2Token.hashCode());
    result =
        prime * result
            + (oAuth2TokenCredentialsId == null ? 0 : oAuth2TokenCredentialsId.hashCode());
    result = prime * result + (repositoryOwner == null ? 0 : repositoryOwner.hashCode());
    result =
        prime * result
            + (usernamePasswordCredentialsId == null
                ? 0
                : usernamePasswordCredentialsId.hashCode());
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
    final ViolationsToGitHubConfiguration other = (ViolationsToGitHubConfiguration) obj;
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
