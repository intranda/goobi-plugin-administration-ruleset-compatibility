def mavenDockerImage = 'maven:3-eclipse-temurin-21'
def mavenDockerArgs = '-v $HOME/.m2:/var/maven/.m2:z -v $HOME/.config:/var/maven/.config -v $HOME/.sonar:/var/maven/.sonar -u 1000 -e _JAVA_OPTIONS=-Duser.home=/var/maven -e MAVEN_CONFIG=/var/maven/.m2'

pipeline {

  agent any

  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '15', daysToKeepStr: '90', numToKeepStr: '')
    disableConcurrentBuilds()
    timeout(time: 10, unit: 'MINUTES')
  }

  stages {

    // ─────────────────────────────────────────────────────────────────────────
    // 1. BUILD
    // ─────────────────────────────────────────────────────────────────────────
    stage('build') {
      agent {
        docker {
          image mavenDockerImage
          args mavenDockerArgs
          reuseNode true
        }
      }
      steps {
        sh 'git reset --hard HEAD && git clean -fdx'
        script {
          env.BUILD_VERSION = env.TAG_NAME ? env.TAG_NAME.replaceAll('^v', '') : 'dev-SNAPSHOT'
        }
        sh "mvn clean install -U -Drevision=\$BUILD_VERSION -DskipTests -Dcheckstyle.skip=true -Djacoco.skip=true -P '!local-development' --no-transfer-progress"
        archiveArtifacts artifacts: '**/target/*.jar, install/*', fingerprint: true, allowEmptyArchive: true, onlyIfSuccessful: true
      }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. TEST + LINT  (not v*: parallel unit tests and checkstyle)
    // ─────────────────────────────────────────────────────────────────────────
    stage('test + lint') {
      when {
        not { branch 'v*' }
      }
      parallel {

        stage('test') {
          agent {
            docker {
              image mavenDockerImage
              args mavenDockerArgs
              reuseNode true
            }
          }
          steps {
            script {
              def strict = env.BRANCH_NAME == 'master'
              def cmd = "mvn test -Dmaven.main.skip=true -Drevision=\$BUILD_VERSION -P '!local-development' --no-transfer-progress"
              if (strict) {
                sh cmd
              } else {
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                  sh cmd
                }
              }
            }
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            step([
                    $class           : 'JacocoPublisher',
                    execPattern      : '**/target/jacoco.exec',
                    classPattern     : '**/target/classes/',
                    sourcePattern    : '**/src/main/java',
                    exclusionPattern : '**/*Test.class'
            ])
          }
        }

        stage('checkstyle') {
          agent {
            docker {
              image mavenDockerImage
              args mavenDockerArgs
              reuseNode true
            }
          }
          steps {
            script {
              def strict = (env.BRANCH_NAME == 'master') && !env.NO_STRICT_CHECKSTYLE
              def cmd = "mvn checkstyle:check -Drevision=\$BUILD_VERSION -P '!local-development' --no-transfer-progress"
              if (strict) {
                sh cmd
              } else {
                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                  sh cmd
                }
              }
            }
            recordIssues(
                    id: 'checkstyle-plugin',
                    tools: [checkStyle(pattern: '**/target/checkstyle-result.xml')],
                    qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
            )
          }
        }

      }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. DEPLOY  (develop only: root pom + lib module if present)
    //    Private plugins (DO_NOT_PUBLISH) deploy to internal Nexus
    // ─────────────────────────────────────────────────────────────────────────
    stage('deploy') {
      when {
        branch 'develop'
      }
      agent {
        docker {
          image mavenDockerImage
          args mavenDockerArgs
          reuseNode true
        }
      }
      steps {
        script {
          if (fileExists('module-lib/pom.xml')) {
            def altRepo = fileExists('DO_NOT_PUBLISH')
                    ? "-DaltDeploymentRepository=\$NEXUS_INTERNAL_REPO -DaltSnapshotDeploymentRepository=\$NEXUS_INTERNAL_REPO"
                    : ''
            sh "mvn -N deploy -Dmaven.main.skip=true -Dmaven.test.skip=true -Drevision=\$BUILD_VERSION -U ${altRepo} --no-transfer-progress"
            sh "mvn -f module-lib/pom.xml deploy -Dmaven.main.skip=true -Dmaven.test.skip=true -Drevision=\$BUILD_VERSION -U ${altRepo} --no-transfer-progress"
          }
        }
      }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. UPDATE PARENT  (master only: advance submodule pointer in core or collection)
    // ─────────────────────────────────────────────────────────────────────────
    stage('update-parent') {
      when {
        branch 'master'
      }
      agent {
        docker {
          image mavenDockerImage
          args mavenDockerArgs
          reuseNode true
        }
      }
      steps {
        withCredentials([gitUsernamePassword(credentialsId: '93f7e7d3-8f74-4744-a785-518fc4d55314', gitToolName: 'git-tool')]) {
          sh '''#!/bin/bash -xe
            PLUGIN_NAME=$(basename $(git remote get-url origin) .git)

            if [ -f "DO_NOT_PUBLISH" ]; then
              REPO_URL="$COLLECTION_REPO_URL"
              SUBMODULE_PATH="private-plugins/$PLUGIN_NAME"
            else
              REPO_URL="$CORE_REPO_URL"
              SUBMODULE_PATH="plugins/$PLUGIN_NAME"
            fi

            WORK_DIR=$(mktemp -d)
            git clone --depth 1 --branch develop "$REPO_URL" "$WORK_DIR"
            cd "$WORK_DIR"
            git submodule update --init --remote -- "$SUBMODULE_PATH"
            git add "$SUBMODULE_PATH"
            if git diff --cached --quiet; then
              echo "Submodule already up to date."
            else
              git commit -m "Update ${PLUGIN_NAME} to latest master"
              git push origin develop
            fi
            rm -rf "$WORK_DIR"
          '''
        }
      }
    }

  }

  post {
    changed {
      emailext(
              subject: '${DEFAULT_SUBJECT}',
              body: '${DEFAULT_CONTENT}',
              recipientProviders: [requestor(), culprits()],
              attachLog: true
      )
    }
  }

}
/* vim: set ts=2 sw=2 tw=120 et :*/