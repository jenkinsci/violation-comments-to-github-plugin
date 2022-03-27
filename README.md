# Violation Comments to GitHub Jenkins Plugin


This is a Jenkins plugin for [Violation Comments to GitHub Lib](https://github.com/tomasbjerre/violation-comments-to-github-lib). This plugin will find report files from static code analysis and comment GitHub pull requests with the content.

It uses [Violation Comments to GitHub Lib](https://github.com/tomasbjerre/violation-comments-to-github-lib) and supports the same formats as [Violations Lib](https://github.com/tomasbjerre/violations-lib).

Example of supported reports are available [here](https://github.com/tomasbjerre/violations-lib/tree/master/src/test/resources).

You can also do this with a [command line tool](https://www.npmjs.com/package/violation-comments-to-github-command-line).

A number of **parsers** have been implemented. Some **parsers** can parse output from several **reporters**.

| Reporter | Parser | Notes
| --- | --- | ---
| [_ARM-GCC_](https://developer.arm.com/open-source/gnu-toolchain/gnu-rm)               | `CLANG`              | 
| [_AndroidLint_](http://developer.android.com/tools/help/lint.html)                    | `ANDROIDLINT`        | 
| [_AnsibleLint_](https://github.com/willthames/ansible-lint)                           | `FLAKE8`             | With `-p`
| [_Bandit_](https://github.com/PyCQA/bandit)                                           | `CLANG`              | With `bandit -r examples/ -f custom -o bandit.out --msg-template "{abspath}:{line}: {severity}: {test_id}: {msg}"`
| [_CLang_](https://clang-analyzer.llvm.org/)                                           | `CLANG`              | 
| [_CPD_](http://pmd.sourceforge.net/pmd-4.3.0/cpd.html)                                | `CPD`                | 
| [_CPPCheck_](http://cppcheck.sourceforge.net/)                                        | `CPPCHECK`           | With `cppcheck test.cpp --output-file=cppcheck.xml --xml`
| [_CPPLint_](https://github.com/theandrewdavis/cpplint)                                | `CPPLINT`            | 
| [_CSSLint_](https://github.com/CSSLint/csslint)                                       | `CSSLINT`            | 
| [_Checkstyle_](http://checkstyle.sourceforge.net/)                                    | `CHECKSTYLE`         | 
| [_CloudFormation Linter_](https://github.com/aws-cloudformation/cfn-lint)             | `JUNIT`              | `cfn-lint . -f junit --output-file report-junit.xml`
| [_CodeClimate_](https://codeclimate.com/)                                             | `CODECLIMATE`        | 
| [_CodeNarc_](http://codenarc.sourceforge.net/)                                        | `CODENARC`           | 
| [_Dart_](https://dart.dev/)                                                           | `MACHINE`            | With `dart analyze --format=machine`
| [_Detekt_](https://github.com/arturbosch/detekt)                                      | `CHECKSTYLE`         | With `--output-format xml`.
| [_DocFX_](http://dotnet.github.io/docfx/)                                             | `DOCFX`              | 
| [_Doxygen_](https://www.stack.nl/~dimitri/doxygen/)                                   | `CLANG`              | 
| [_ERB_](https://www.puppetcookbook.com/posts/erb-template-validation.html)            | `CLANG`              | With `erb -P -x -T '-' "${it}" \| ruby -c 2>&1 >/dev/null \| grep '^-' \| sed -E 's/^-([a-zA-Z0-9:]+)/${filename}\1 ERROR:/p' > erbfiles.out`.
| [_ESLint_](https://github.com/sindresorhus/grunt-eslint)                              | `CHECKSTYLE`         | With `format: 'checkstyle'`.
| [_Findbugs_](http://findbugs.sourceforge.net/)                                        | `FINDBUGS`           | 
| [_Flake8_](http://flake8.readthedocs.org/en/latest/)                                  | `FLAKE8`             | 
| [_FxCop_](https://en.wikipedia.org/wiki/FxCop)                                        | `FXCOP`              | 
| [_GCC_](https://gcc.gnu.org/)                                                         | `CLANG`              | 
| [_Gendarme_](http://www.mono-project.com/docs/tools+libraries/tools/gendarme/)        | `GENDARME`           | 
| [_Generic reporter_]()                                                                | `GENERIC`            | Will create one single violation with all the content as message.
| [_GoLint_](https://github.com/golang/lint)                                            | `GOLINT`             | 
| [_GoVet_](https://golang.org/cmd/vet/)                                                | `GOLINT`             | Same format as GoLint.
| [_GolangCI-Lint_](https://github.com/golangci/golangci-lint/)                         | `CHECKSTYLE`         | With `--out-format=checkstyle`.
| [_GoogleErrorProne_](https://github.com/google/error-prone)                           | `GOOGLEERRORPRONE`   | 
| [_HadoLint_](https://github.com/hadolint/hadolint/)                                   | `CHECKSTYLE`         | With `-f checkstyle`
| [_IAR_](https://www.iar.com/iar-embedded-workbench/)                                  | `IAR`                | With `--no_wrap_diagnostics`
| [_Infer_](http://fbinfer.com/)                                                        | `PMD`                | Facebook Infer. With `--pmd-xml`.
| [_JACOCO_](https://www.jacoco.org/)                                                   | `JACOCO`             | 
| [_JCReport_](https://github.com/jCoderZ/fawkez/wiki/JcReport)                         | `JCREPORT`           | 
| [_JSHint_](http://jshint.com/)                                                        | `JSLINT`             | With `--reporter=jslint` or the CHECKSTYLE parser with `--reporter=checkstyle`
| [_JUnit_](https://junit.org/junit4/)                                                  | `JUNIT`              | It only contains the failures.
| [_KTLint_](https://github.com/shyiko/ktlint)                                          | `CHECKSTYLE`         | 
| [_Klocwork_](http://www.klocwork.com/products-services/klocwork/static-code-analysis)  | `KLOCWORK`           | 
| [_KotlinGradle_](https://github.com/JetBrains/kotlin)                                 | `KOTLINGRADLE`       | Output from Kotlin Gradle Plugin.
| [_KotlinMaven_](https://github.com/JetBrains/kotlin)                                  | `KOTLINMAVEN`        | Output from Kotlin Maven Plugin.
| [_Lint_]()                                                                            | `LINT`               | A common XML format, used by different linters.
| [_MSBuildLog_](https://docs.microsoft.com/en-us/visualstudio/msbuild/obtaining-build-logs-with-msbuild?view=vs-2019)  | `MSBULDLOG`          | With `-fileLogger` use `.*msbuild\\.log$` as pattern or `-fl -flp:logfile=MyProjectOutput.log;verbosity=diagnostic` for a custom output filename
| [_MSCpp_](https://visualstudio.microsoft.com/vs/features/cplusplus/)                  | `MSCPP`              | 
| [_Mccabe_](https://pypi.python.org/pypi/mccabe)                                       | `FLAKE8`             | 
| [_MyPy_](https://pypi.python.org/pypi/mypy-lang)                                      | `MYPY`               | 
| [_NullAway_](https://github.com/uber/NullAway)                                        | `GOOGLEERRORPRONE`   | Same format as Google Error Prone.
| [_PCLint_](http://www.gimpel.com/html/pcl.htm)                                        | `PCLINT`             | PC-Lint using the same output format as the Jenkins warnings plugin, [_details here_](https://wiki.jenkins.io/display/JENKINS/PcLint+options)
| [_PHPCS_](https://github.com/squizlabs/PHP_CodeSniffer)                               | `CHECKSTYLE`         | With `phpcs api.php --report=checkstyle`.
| [_PHPPMD_](https://phpmd.org/)                                                        | `PMD`                | With `phpmd api.php xml ruleset.xml`.
| [_PMD_](https://pmd.github.io/)                                                       | `PMD`                | 
| [_Pep8_](https://github.com/PyCQA/pycodestyle)                                        | `FLAKE8`             | 
| [_PerlCritic_](https://github.com/Perl-Critic)                                        | `PERLCRITIC`         | 
| [_PiTest_](http://pitest.org/)                                                        | `PITEST`             | 
| [_ProtoLint_](https://github.com/yoheimuta/protolint)                                 | `PROTOLINT`          | 
| [_Puppet-Lint_](http://puppet-lint.com/)                                              | `CLANG`              | With `-log-format %{fullpath}:%{line}:%{column}: %{kind}: %{message}`
| [_PyDocStyle_](https://pypi.python.org/pypi/pydocstyle)                               | `PYDOCSTYLE`         | 
| [_PyFlakes_](https://pypi.python.org/pypi/pyflakes)                                   | `FLAKE8`             | 
| [_PyLint_](https://www.pylint.org/)                                                   | `PYLINT`             | With `pylint --output-format=parseable`.
| [_ReSharper_](https://www.jetbrains.com/resharper/)                                   | `RESHARPER`          | 
| [_RubyCop_](http://rubocop.readthedocs.io/en/latest/formatters/)                      | `CLANG`              | With `rubycop -f clang file.rb`
| [_SARIF_](https://github.com/oasis-tcs/sarif-spec)                                    | `SARIF`              | 
| [_SbtScalac_](http://www.scala-sbt.org/)                                              | `SBTSCALAC`          | 
| [_Scalastyle_](http://www.scalastyle.org/)                                            | `CHECKSTYLE`         | 
| [_Simian_](http://www.harukizaemon.com/simian/)                                       | `SIMIAN`             | 
| [_Sonar_](https://www.sonarqube.org/)                                                 | `SONAR`              | With `mvn sonar:sonar -Dsonar.analysis.mode=preview -Dsonar.report.export.path=sonar-report.json`. Removed in 7.7, see [SONAR-11670](https://jira.sonarsource.com/browse/SONAR-11670) but can be retrieved with: `curl --silent 'http://sonar-server/api/issues/search?componentKeys=unique-key&resolved=false' \| jq -f sonar-report-builder.jq > sonar-report.json`.
| [_Spotbugs_](https://spotbugs.github.io/)                                             | `FINDBUGS`           | 
| [_StyleCop_](https://stylecop.codeplex.com/)                                          | `STYLECOP`           | 
| [_SwiftLint_](https://github.com/realm/SwiftLint)                                     | `CHECKSTYLE`         | With `--reporter checkstyle`.
| [_TSLint_](https://palantir.github.io/tslint/usage/cli/)                              | `CHECKSTYLE`         | With `-t checkstyle`
| [_Valgrind_](https://valgrind.org/)                                                   | `VALGRIND`           | With `--xml=yes`.
| [_XMLLint_](http://xmlsoft.org/xmllint.html)                                          | `XMLLINT`            | 
| [_XUnit_](https://xunit.net/)                                                         | `XUNIT`              | It only contains the failures.
| [_YAMLLint_](https://yamllint.readthedocs.io/en/stable/index.html)                    | `YAMLLINT`           | With `-f parsable`
| [_ZPTLint_](https://pypi.python.org/pypi/zptlint)                                     | `ZPTLINT`            |

48 parsers and 74 reporters.

Missing a format? Open an issue [here](https://github.com/tomasbjerre/violations-lib/issues)!

There is also:
 * A [Gradle plugin](https://github.com/tomasbjerre/violation-comments-to-github-gradle-plugin).
 * A [Maven plugin](https://github.com/tomasbjerre/violation-comments-to-github-maven-plugin).

Available in Jenkins [here](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+GitHub+Plugin).


## Notify Jenkins from GitHub

You will need to the **pull request id** for the pull request that was built. 

* You may trigger with a [webhook](https://developer.github.com/v3/activity/events/types/#pullrequestevent) in GitHub. And consume it with [Generic Webhook Trigger plugin](https://github.com/jenkinsci/generic-webhook-trigger-plugin) to get the variables you need.

* Or, trigger with [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin), it provides the environment variable `ghprbPullId`.

## Merge

**You must perform the merge before build**. If you don't perform the merge, the reported violations will refer to other lines then those in the pull request. The merge can be done with a shell script like this.

```
echo ---
echo --- Merging from $FROM in $FROMREPO to $TO in $TOREPO
echo ---
git clone $TOREPO
cd *
git reset --hard $TO
git status
git remote add from $FROMREPO
git fetch from
git merge $FROM
git --no-pager log --max-count=10 --graph --abbrev-commit

Your build command here!
```

# Screenshots

When installed, a post build action will be available.

![Post build action menu](https://github.com/jenkinsci/violation-comments-to-github-jenkins-plugin/blob/master/sandbox/jenkins-postbuildmenu.png)

The pull request will be commented like this.

![Pull request comment](https://github.com/jenkinsci/violation-comments-to-github-jenkins-plugin/blob/master/sandbox/github-pr-diff-comment.png)

## Job DSL Plugin

This plugin can be used with the Job DSL Plugin. In this example the [GitHub Pull Request Builder Plugin](https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin) is used to trigger, merge and provide environment variables needed.

```groovy
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

    // Only specify one of these!
    oAuth2Token("")
    credentialsId("githubtoken")
    
    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(true)
    commentOnlyChangedContent(true)
    commentOnlyChangedFiles(true)
    minSeverity('INFO')
    maxNumberOfViolations(99999)
    keepOldComments(false)
    
    commentTemplate("""
    **Reporter**: {{violation.reporter}}{{#violation.rule}}
    
    **Rule**: {{violation.rule}}{{/violation.rule}}
    **Severity**: {{violation.severity}}
    **File**: {{violation.file}} L{{violation.startLine}}{{#violation.source}}
    
    **Source**: {{violation.source}}{{/violation.source}}
    
    {{violation.message}}
    """)
    
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

```groovy
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

    // Only specify one of these!
    oAuth2Token("oh no!")
    credentialsId("githubtoken")

    createSingleFileComments(true)
    createCommentWithAllSingleFileComments(true)
    commentOnlyChangedContent(true)
    commentOnlyChangedFiles(true)
    minSeverity('INFO')
    maxNumberOfViolations(99999)
    keepOldComments(false)
    
    commentTemplate("""
    **Reporter**: {{violation.reporter}}{{#violation.rule}}
    
    **Rule**: {{violation.rule}}{{/violation.rule}}
    **Severity**: {{violation.severity}}
    **File**: {{violation.file}} L{{violation.startLine}}{{#violation.source}}
    
    **Source**: {{violation.source}}{{/violation.source}}
    
    {{violation.message}}
    """)
    
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

```groovy
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

    // Only specify one of these!
    oAuth2Token: '',
    credentialsId: '',

    createCommentWithAllSingleFileComments: true, 
    createSingleFileComments: true, 
    commentOnlyChangedContent: true, 
    commentOnlyChangedFiles: true,
    minSeverity: 'INFO',
    maxNumberOfViolations: 99999,
    keepOldComments: false,
 
    commentTemplate: """
    **Reporter**: {{violation.reporter}}{{#violation.rule}}
    
    **Rule**: {{violation.rule}}{{/violation.rule}}
    **Severity**: {{violation.severity}}
    **File**: {{violation.file}} L{{violation.startLine}}{{#violation.source}}
    
    **Source**: {{violation.source}}{{/violation.source}}
    
    {{violation.message}}
    """,

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
