package org.jenkinsci.plugins.jvctg;

import hudson.Extension;
import hudson.model.Item;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.Serializable;

import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.jenkinsci.plugins.jvctg.config.CredentialsHelper;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

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

  private String gitHubUrl;
  private String oAuth2Token;
  private String credentialsId;
  private String repositoryOwner;
  @Deprecated private transient String username;
  @Deprecated private transient String password;
  @Deprecated private transient String oAuth2TokenCredentialsId;
  @Deprecated private transient String usernamePasswordCredentialsId;
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

  public String getGitHubUrl() {
    return this.gitHubUrl;
  }

  public String getoAuth2Token() {
    return this.oAuth2Token;
  }

  public String getOAuth2Token() {
    return oAuth2Token;
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

  @DataBoundSetter
  public void setGitHubUrl(final String gitHubUrl) {
    this.gitHubUrl = gitHubUrl;
  }

  @DataBoundSetter
  public void setoAuth2Token(final String oAuth2Token) {
    this.oAuth2Token = oAuth2Token;
  }

  @DataBoundSetter
  public void setRepositoryOwner(final String repositoryOwner) {
    this.repositoryOwner = repositoryOwner;
  }

  @DataBoundSetter
  public void setCredentialsId(final String credentialsId) {
    this.credentialsId = credentialsId;
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (gitHubUrl == null ? 0 : gitHubUrl.hashCode());
    result = prime * result + (minSeverity == null ? 0 : minSeverity.hashCode());
    result = prime * result + (oAuth2Token == null ? 0 : oAuth2Token.hashCode());
    result = prime * result + (repositoryOwner == null ? 0 : repositoryOwner.hashCode());
    result = prime * result + (credentialsId == null ? 0 : credentialsId.hashCode());
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
    return true;
  }

  @Override
  public String toString() {
    return "ViolationsToGitHubConfiguration [gitHubUrl="
        + gitHubUrl
        + ", oAuth2Token="
        + oAuth2Token
        + ", repositoryOwner="
        + repositoryOwner
        + ", credentialsId="
        + credentialsId
        + ", minSeverity="
        + minSeverity
        + "]";
  }
}
