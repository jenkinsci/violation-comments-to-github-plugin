# Violation Comments to GitHub Jenkins Plugin

[![Build Status](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-github-plugin/badge/icon)](https://jenkins.ci.cloudbees.com/job/plugins/job/violation-comments-to-github-plugin/)

This is a Jenkins plugin for [Violation Comments to GitHub Lib](https://github.com/tomasbjerre/violation-comments-to-github-lib). This plugin will find report files from static code analysis and comment GitHub pull requests with the content.

You can have a look at [violations-test](https://github.com/tomasbjerre/violations-test/pull/2) to see what the result may look like.

It uses [Violation Comments to GitHub Lib](https://github.com/tomasbjerre/violation-comments-to-github-lib) and supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

It supports:
 * [_AndroidLint_](http://developer.android.com/tools/help/lint.html)
 * [_Checkstyle_](http://checkstyle.sourceforge.net/)
   * [_Detekt_](https://github.com/arturbosch/detekt) with `--output-format xml`.
   * [_ESLint_](https://github.com/sindresorhus/grunt-eslint) with `format: 'checkstyle'`.
   * [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer) with `phpcs api.php --report=checkstyle`.
 * [_CLang_](https://clang-analyzer.llvm.org/)
   * [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/) with `rubycop -f clang file.rb`
 * [_CodeNarc_](http://codenarc.sourceforge.net/)
 * [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)
 * [_CPPLint_](https://github.com/theandrewdavis/cpplint)
 * [_CPPCheck_](http://cppcheck.sourceforge.net/)
 * [_CSSLint_](https://github.com/CSSLint/csslint)
 * [_Findbugs_](http://findbugs.sourceforge.net/)
 * [_Flake8_](http://flake8.readthedocs.org/en/latest/)
   * [_AnsibleLint_](https://github.com/willthames/ansible-lint) with `-p`
   * [_Mccabe_](https://pypi.python.org/pypi/mccabe)
   * [_Pep8_](https://github.com/PyCQA/pycodestyle)
   * [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)
 * [_FxCop_](https://en.wikipedia.org/wiki/FxCop)
 * [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)
 * [_GoLint_](https://github.com/golang/lint)
   * [_GoVet_](https://golang.org/cmd/vet/) Same format as GoLint.
 * [_JSHint_](http://jshint.com/)
 * _Lint_ A common XML format, used by different linters.
 * [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)
 * [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)
 * [_MyPy_](https://pypi.python.org/pypi/mypy-lang)
 * [_PerlCritic_](https://github.com/Perl-Critic)
 * [_PiTest_](http://pitest.org/)
 * [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)
 * [_PyLint_](https://www.pylint.org/)
 * [_PMD_](https://pmd.github.io/)
   * [_Infer_](http://fbinfer.com/) Facebook Infer. With `--pmd-xml`.
   * [_PHPPMD_](https://phpmd.org/) with `phpmd api.php xml ruleset.xml`.
 * [_ReSharper_](https://www.jetbrains.com/resharper/)
 * [_SbtScalac_](http://www.scala-sbt.org/)
 * [_Simian_](http://www.harukizaemon.com/simian/)
 * [_StyleCop_](https://stylecop.codeplex.com/)
 * [_XMLLint_](http://xmlsoft.org/xmllint.html)
 * [_ZPTLint_](https://pypi.python.org/pypi/zptlint)


There is also:
 * A [Gradle plugin](https://github.com/tomasbjerre/violation-comments-to-github-gradle-plugin).
 * A [Maven plugin](https://github.com/tomasbjerre/violation-comments-to-github-maven-plugin).

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+GitHub+Plugin).

You will need to the **pull request id** for the pull request that was built. You may want to have a look at [Generic Webhook Trigger plugin](https://github.com/jenkinsci/generic-webhook-trigger-plugin) or [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin), it provides the environment variable `ghprbPullId`.

You must perform the merge before doing the analysis for the lines to match the lines in the pull request.

```
Shell script build step
git clone $TOREPO
cd *
git reset --hard $TO
git status
git remote add from $FROMREPO
git fetch from
git merge $FROM
git --no-pager log --max-count=10 --graph --abbrev-commit

your build command here!
``` 

# Screenshots

When installed, a post build action will be available.

![Post build action menu](https://github.com/jenkinsci/violation-comments-to-github-jenkins-plugin/blob/master/sandbox/jenkins-postbuildmenu.png)

The pull request will be commented like this.

![Pull request comment](https://github.com/jenkinsci/violation-comments-to-github-jenkins-plugin/blob/master/sandbox/github-pr-diff-comment.png)

## Job DSL Plugin

This plugin can be used with the Job DSL Plugin. In this example the [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin) is used to trigger, merge and provide environment variables needed.

```
job('GitHub_PR_Builder') {
 concurrentBuild()
 quietPeriod(0)
 scm {
  git {
   remote {
    github('tomasbjerre/violations-test')
    refspec('+refs/pull/*:refs/remotes/origin/pr/*')
   }
   branch('${sha1}')
  }
 }

 triggers {
  githubPullRequest {
   cron('* * * * *')
   permitAll()
   extensions {
    buildStatus {
     completedStatus('SUCCESS', 'There were no errors, go have a cup of coffee...')
     completedStatus('FAILURE', 'There were errors, for info, please see...')
     completedStatus('ERROR', 'There was an error in the infrastructure, please contact...')
    }
   }
  }
 }

 steps {
  shell('''
./gradlew build
  ''')
 }

 publishers {
  violationsToGitHubRecorder {
   config {
    gitHubUrl("https://api.github.com/")
    repositoryOwner("tomasbjerre")
    repositoryName("violations-test")
    pullRequestId("\$ghprbPullId")

    useOAuth2Token(false)
    oAuth2Token("")

    useOAuth2TokenCredentialsIdCredentials(true)
    oAuth2TokenCredentialsId("githubtoken")

    useUsernamePasswordCredentials(false)
    usernamePasswordCredentialsId("")

    useUsernamePassword(false)
    username("")
    password("")
    
    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(true)
    commentOnlyChangedContent(true)
    minSeverity('INFO')
    keepOldComments(false)
    
    violationConfigs {
     violationConfig {
      parser("FINDBUGS")
      reporter("Findbugs")
      pattern(".*/findbugs/.*\\.xml\$")
     }
     violationConfig {
      parser("CHECKSTYLE")
      reporter("Checkstyle")
      pattern(".*/checkstyle/.*\\.xml\$")
     }
    }
   }
  }
 }
}
```

Here is another example using [Generic Webhook Trigger plugin](https://github.com/jenkinsci/generic-webhook-trigger-plugin). You will need to add a webhook in GitHub and point it to `http://JENKINS_URL/generic-webhook-trigger/invoke`. You may want to combine this with [HTTP Request Plugin](https://wiki.jenkins-ci.org/display/JENKINS/HTTP+Request+Plugin) to comment the pull requests with a link to the job. And also [Conditional BuildStep Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Conditional+BuildStep+Plugin) to have different comments depending on build status.

```
job('GitHub_PR_Builder Generic') {
 concurrentBuild()
 quietPeriod(0)
 parameters {
  stringParam('PULL_REQUEST_HEAD_URL', '')
  stringParam('PULL_REQUEST_BASE_URL', '')
  stringParam('PULL_REQUEST_HEAD_REF', '')
  stringParam('PULL_REQUEST_BASE_REF', '')
 }
 scm {
  git {
   remote {
    name('origin')
    url('$PULL_REQUEST_BASE_URL')
   }
   remote {
    name('upstream')
    url('$PULL_REQUEST_HEAD_URL')
   }
   branch('$PULL_REQUEST_HEAD_REF')
   extensions {
    mergeOptions {
     remote('upstream')
     branch('$PULL_REQUEST_BASE_REF')
    }
   }
  }
 }
 triggers {
  genericTrigger {
   genericVariables {
    genericVariable {
     key("PULL_REQUEST_HEAD_URL")
     value("\$.pull_request.head.repo.clone_url")
     expressionType("JSONPath")
     regexpFilter("")
    }
    genericVariable {
     key("PULL_REQUEST_HEAD_REF")
     value("\$.pull_request.head.ref")
     expressionType("JSONPath")
     regexpFilter("")
    }
    genericVariable {
     key("PULL_REQUEST_BASE_URL")
     value("\$.pull_request.base.repo.clone_url")
     expressionType("JSONPath")
     regexpFilter("")
    }
    genericVariable {
     key("PULL_REQUEST_BASE_REF")
     value("\$.pull_request.base.ref")
     expressionType("JSONPath")
     regexpFilter("")
    }
    genericVariable {
     key("PULL_REQUEST_BASE_OWNER")
     value("\$.pull_request.base.repo.owner.login")
     expressionType("JSONPath")
     regexpFilter("")
    }
    genericVariable {
     key("PULL_REQUEST_BASE_REPO")
     value("\$.pull_request.base.repo.name")
     expressionType("JSONPath")
     regexpFilter("")
    }
    genericVariable {
     key("PULL_REQUEST_ID")
     value("\$.number")
     expressionType("JSONPath")
     regexpFilter("")
    }
    genericVariable {
     key("ACTION")
     value("\$.action")
     expressionType("JSONPath")
     regexpFilter("")
    }
   }
   regexpFilterText("\$ACTION")
   regexpFilterExpression("opened|reopened|synchronize")
  }
 }

 steps {
  shell('./gradlew build')
 }

 publishers {
  violationsToGitHubRecorder {
   config {
    gitHubUrl("https://api.github.com/")
    repositoryOwner("\$PULL_REQUEST_BASE_OWNER")
    repositoryName("\$PULL_REQUEST_BASE_REPO")
    pullRequestId("\$PULL_REQUEST_ID")

    useOAuth2Token(true)
    oAuth2Token("oh no!")

    useOAuth2TokenCredentialsIdCredentials(true)
    oAuth2TokenCredentialsId("githubtoken")

    useUsernamePasswordCredentials(false)
    usernamePasswordCredentialsId("")

    useUsernamePassword(false)
    username("")
    password("")
    
    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(true)
    commentOnlyChangedContent(true)
    minSeverity('INFO')
    keepOldComments(false)
    
    violationConfigs {
     violationConfig {
      parser("FINDBUGS")
      reporter("Findbugs")
      pattern(".*/findbugs/.*\\.xml\$")
     }
     violationConfig {
      parser("CHECKSTYLE")
      reporter("Checkstyle")
      pattern(".*/checkstyle/.*\\.xml\$")
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
 def mvnHome = tool 'Maven 3.3.9'
 deleteDir()
 
 stage('Merge') {
  sh "git init"
  sh "git fetch --no-tags --progress git@git:group/reponame.git +refs/heads/*:refs/remotes/origin/* --depth=200"
  sh "git checkout origin/${env.targetBranch}"
  sh "git merge origin/${env.sourceBranch}"
  sh "git log --graph --abbrev-commit --max-count=10"
 }

 stage('Static code analysis') {
  sh "${mvnHome}/bin/mvn package -DskipTests -Dmaven.test.failure.ignore=false -Dsurefire.skip=true -Dmaven.compile.fork=true -Dmaven.javadoc.skip=true"

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
    minSeverity: 'INFO',
    keepOldComments: false,
    violationConfigs: [
     [ pattern: '.*/checkstyle-result\\.xml$', parser: 'CHECKSTYLE', reporter: 'Checkstyle' ], 
     [ pattern: '.*/findbugsXml\\.xml$', parser: 'FINDBUGS', reporter: 'Findbugs' ], 
     [ pattern: '.*/pmd\\.xml$', parser: 'PMD', reporter: 'PMD' ], 
    ]
   ]
  ])
 }
}
```

# Plugin development
More details on Jenkins plugin development is available [here](https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial).

There is a ```/build.sh``` that will perform a full build and test the plugin.

If you have release-permissions this is how you do a release:

```
mvn release:prepare release:perform
```
