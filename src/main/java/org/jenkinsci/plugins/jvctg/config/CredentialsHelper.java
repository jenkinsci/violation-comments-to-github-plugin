package org.jenkinsci.plugins.jvctg.config;

import static com.cloudbees.plugins.credentials.CredentialsMatchers.allOf;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.firstOrNull;
import static com.cloudbees.plugins.credentials.CredentialsMatchers.withId;
import static com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.isNullOrEmpty;
import static hudson.security.ACL.SYSTEM;
import hudson.model.ItemGroup;
import hudson.util.ListBoxModel;

import java.util.List;

import org.acegisecurity.Authentication;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.common.AbstractIdCredentialsListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernameCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernameListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.google.common.base.Optional;

public class CredentialsHelper {

  public static ListBoxModel doFillOAuth2TokenCredentialsIdItems() {
    List<StringCredentials> credentials = getAllCredentials(StringCredentials.class);
    ListBoxModel listBoxModel =
        new StandardListBoxModel() //
            .includeEmptyValue() //
            .withAll(credentials);
    return listBoxModel;
  }

  public static ListBoxModel doFillUsernamePasswordCredentialsIdItems() {
    List<StandardUsernamePasswordCredentials> credentials =
        getAllCredentials(StandardUsernamePasswordCredentials.class);
    AbstractIdCredentialsListBoxModel<StandardUsernameListBoxModel, StandardUsernameCredentials>
        listBoxModel = new StandardUsernameListBoxModel().includeEmptyValue();
    for (StandardUsernamePasswordCredentials credential : credentials) {
      listBoxModel.with(credential);
    }
    return listBoxModel;
  }

  public static Optional<StringCredentials> findOAuth2TokenCredentials(
      String oAuth2TokenCredentialsId) {
    if (isNullOrEmpty(oAuth2TokenCredentialsId)) {
      return absent();
    }

    return fromNullable(
        firstOrNull(
            getAllCredentials(StringCredentials.class), allOf(withId(oAuth2TokenCredentialsId))));
  }

  public static Optional<StandardUsernamePasswordCredentials> findUsernamePasswordCredentials(
      String usernamePasswordCredentialsId) {
    if (isNullOrEmpty(usernamePasswordCredentialsId)) {
      return absent();
    }

    return fromNullable(
        firstOrNull(
            getAllCredentials(StandardUsernamePasswordCredentials.class),
            allOf(withId(usernamePasswordCredentialsId))));
  }

  private static <C extends Credentials> List<C> getAllCredentials(Class<C> type) {
    ItemGroup<?> itemGroup = null;
    Authentication authentication = SYSTEM;
    DomainRequirement domainRequirement = null;

    return lookupCredentials(type, itemGroup, authentication, domainRequirement);
  }
}
