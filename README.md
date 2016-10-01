# Violation Comments to GitHub Jenkins Plugin

[![Build Status](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-github-plugin/badge/icon)](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-github-plugin/)

This is a Jenkins plugin for [Violation Comments to GitHub Lib](https://github.com/tomasbjerre/violation-comments-to-github-lib). This plugin will find report files from static code analysis and comment GitHub pull requests with the content.

You can have a look at [violations-test](https://github.com/tomasbjerre/violations-test/pull/2) to see what the result may look like.

It supports:
 * [_AndoidLint_](http://developer.android.com/tools/help/lint.html)
 * [_Checkstyle_](http://checkstyle.sourceforge.net/) ([_ESLint_](https://github.com/sindresorhus/grunt-eslint) with `format: 'checkstyle'`)
 * [_CPPLint_](https://github.com/theandrewdavis/cpplint)
 * [_CPPCheck_](http://cppcheck.sourceforge.net/)
 * [_CSSLint_](https://github.com/CSSLint/csslint)
 * [_Findbugs_](http://findbugs.sourceforge.net/)
 * [_Flake8_](http://flake8.readthedocs.org/en/latest/) ([_PyLint_](https://www.pylint.org/), [_Pep8_](https://github.com/PyCQA/pycodestyle), [_Mccabe_](https://pypi.python.org/pypi/mccabe), [_PyFlakes_](https://pypi.python.org/pypi/pyflakes))
 * [_FxCop_](https://en.wikipedia.org/wiki/FxCop)
 * [_JSHint_](http://jshint.com/)
 * _Lint_ A common XML format, used by different linters.
 * [_PerlCritic_](https://github.com/Perl-Critic)
 * [_PiTest_](http://pitest.org/)
 * [_PMD_](https://pmd.github.io/)
 * [_ReSharper_](https://www.jetbrains.com/resharper/)
 * [_XMLLint_](http://xmlsoft.org/xmllint.html)

There is also:
 * A [Gradle plugin](https://github.com/tomasbjerre/violation-comments-to-github-gradle-plugin).
 * A [Maven plugin](https://github.com/tomasbjerre/violation-comments-to-github-maven-plugin).

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+GitHub+Plugin).

You will need to the **pull request id** for the pull request that was built. You may want to have a look at [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin), it provides the environment variable `ghprbPullId`.

# Screenshots

When installed, a post build action will be available.

![Post build action menu](https://github.com/jenkinsci/violation-comments-to-github-jenkins-plugin/blob/master/sandbox/jenkins-postbuildmenu.png)

The pull request will be commented like this.

![Pull request comment](https://github.com/jenkinsci/violation-comments-to-github-jenkins-plugin/blob/master/sandbox/github-pr-diff-comment.png)

## Job DSL Plugin

This plugin can be used with the Job DSL Plugin.

```
job('example') {
 publishers {
  violationsToGitHubRecorder {
   config {
    gitHubUrl("https://api.github.com/")
    repositoryOwner("tomasbjerre")
    repositoryName("violations-test")
    pullRequestId("2")
    useOAuth2Token(false)
    oAuth2Token("")
    useUsernamePasswordCredentials(false)
    usernamePasswordCredentialsId("")
    useUsernamePassword(true)
    username("")
    password("")
    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(true)
    commentOnlyChangedContent(true)
    violationConfigs {
     violationConfig {
      reporter("FINDBUGS")
      pattern(".*/findbugs/.*\\.xml\$")
     }
    }
   }
  }
 }
}
```

## Pipeline Plugin

This plugin can be used with the Pipeline Plugin:

```
node {

 checkout([
  $class: 'GitSCM', 
  branches: [[ name: '*/master' ]], 
  doGenerateSubmoduleConfigurations: false,
  extensions: [],
  submoduleCfg: [],
  userRemoteConfigs: [[ url: 'https://github.com/tomasbjerre/violations-test.git' ]]
 ])

 sh '''
 ./gradlew build
 '''

 step([
  $class: 'ViolationsToGitHubRecorder', 
  config: [
   gitHubUrl: 'https://api.github.com/', 
   repositoryOwner: 'tomasbjerre', 
   repositoryName: 'violations-test', 
   pullRequestId: '2', 
   useOAuth2Token: false, 
   oAuth2Token: '', 
   useUsernamePassword: true, 
   username: 'admin', 
   password: 'admin', 
   useUsernamePasswordCredentials: false, 
   usernamePasswordCredentialsId: '',
   createCommentWithAllSingleFileComments: true, 
   createSingleFileComments: true, 
   commentOnlyChangedContent: true, 
   violationConfigs: [
    [ pattern: '.*/checkstyle/.*\\.xml$', reporter: 'CHECKSTYLE' ], 
    [ pattern: '.*/findbugs/.*\\.xml$', reporter: 'FINDBUGS' ], 
   ]
  ]
 ])
}
```

# Plugin development
More details on Jenkins plugin development is available [here](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial).

There is a ```/build.sh``` that will perform a full build and test the plugin. You may have a look at sandbox/settings.xml on how to configure your Maven settings.

A release is created like this. You need to clone from jenkinsci-repo, with https and have username/password in settings.xml.
```
mvn release:prepare release:perform
```
