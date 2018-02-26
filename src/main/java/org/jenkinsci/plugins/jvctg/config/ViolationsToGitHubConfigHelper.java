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
  public static final String FIELD_PULLREQUESTID = "pullRequestId";
  public static final String FIELD_REPOSITORYNAME = "repositoryName";
  public static final String FIELD_REPOSITORYOWNER = "repositoryOwner";
  public static final String FIELD_CREDENTIALSID = "credentialsId";
  public static final String FIELD_MINSEVERITY = "minSeverity";
  public static final String FIELD_KEEP_OLD_COMMENTS = "keepOldComments";

  public static ViolationsToGitHubConfig createNewConfig() {
    final ViolationsToGitHubConfig config = new ViolationsToGitHubConfig();
    final List<ViolationConfig> violationConfigs = getAllViolationConfigs();
    config.setViolationConfigs(violationConfigs);
    return config;
  }

  public static List<ViolationConfig> getAllViolationConfigs() {
    final List<ViolationConfig> violationConfigs = newArrayList();
    for (final Parser parser : Parser.values()) {
      final ViolationConfig violationConfig = new ViolationConfig();
      violationConfig.setParser(parser);
      violationConfigs.add(violationConfig);
    }
    return violationConfigs;
  }
}
