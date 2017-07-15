package org.jenkinsci.plugins.jvctg.config;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import se.bjurr.violations.lib.reports.Parser;

public class ViolationsToGitHubConfigHelper {
  public static final String FIELD_COMMENTONLYCHANGEDCONTENT = "commentOnlyChangedContent";
  public static final String FIELD_CREATECOMMENTWITHALLSINGLEFILECOMMENTS =
      "createCommentWithAllSingleFileComments";
  public static final String FIELD_CREATESINGLEFILECOMMENTS = "createSingleFileComments";
  public static final String FIELD_GITHUBURL = "gitHubUrl";
  public static final String FIELD_OAUTH2TOKEN = "oAuth2Token";
  public static final String FIELD_OAUTH2TOKENCREDENTIALSID = "oAuth2TokenCredentialsId";
  public static final String FIELD_PASSWORD = "password";
  public static final String FIELD_PATTERN = "pattern";
  public static final String FIELD_REPORTER = "reporter";
  public static final String FIELD_PULLREQUESTID = "pullRequestId";
  public static final String FIELD_REPOSITORYNAME = "repositoryName";
  public static final String FIELD_REPOSITORYOWNER = "repositoryOwner";
  public static final String FIELD_USEOAUTH2TOKEN = "useOAuth2Token";
  public static final String FIELD_USEOAUTH2TOKENCREDENTIALS = "useOAuth2TokenCredentials";
  public static final String FIELD_USERNAME = "username";
  public static final String FIELD_USERNAMEPASSWORDCREDENTIALSID = "usernamePasswordCredentialsId";
  public static final String FIELD_USEUSERNAMEPASSWORD = "useUsernamePassword";
  public static final String FIELD_USEUSERNAMEPASSWORDCREDENTIALS =
      "useUsernamePasswordCredentials";
  public static final String FIELD_MINSEVERITY = "minSeverity";

  public static ViolationsToGitHubConfig createNewConfig() {
    ViolationsToGitHubConfig config = new ViolationsToGitHubConfig();
    List<ViolationConfig> violationConfigs = getAllViolationConfigs();
    config.setViolationConfigs(violationConfigs);
    return config;
  }

  public static List<ViolationConfig> getAllViolationConfigs() {
    List<ViolationConfig> violationConfigs = newArrayList();
    for (Parser parser : Parser.values()) {
      ViolationConfig violationConfig = new ViolationConfig();
      violationConfig.setParser(parser);
      violationConfigs.add(violationConfig);
    }
    return violationConfigs;
  }
}
