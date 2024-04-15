
pipeline {

  agent {
    docker {
      /* using a custom build image with a defined home directory for UID 1000 among other things */
      image 'nexus.intranda.com:4443/maven:3.9.3-eclipse-temurin-17'
      registryUrl 'https://nexus.intranda.com:4443'
      registryCredentialsId 'jenkins-docker'
      args '-v $HOME/.m2:/var/maven/.m2:z -v $HOME/.config:/var/maven/.config -v $HOME/.sonar:/var/maven/.sonar -u 1000 -ti -e _JAVA_OPTIONS=-Duser.home=/var/maven -e MAVEN_CONFIG=/var/maven/.m2'
    }
  }

  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '15', daysToKeepStr: '90', numToKeepStr: '')
  }

  stages {
    stage('prepare') {
      steps {
        sh 'git reset --hard HEAD && git clean -fdx'
      }
    }

    stage('build') {
      steps {
        sh 'mvn clean verify -U'
        recordIssues enabledForFailure: true, aggregatingResults: true, tools: [java(), javaDoc()]
      }
    }

    stage('sonarcloud') {
      when {
        anyOf {
          branch 'master'
          branch 'sonar_*'
        }
      }
      steps {
        withCredentials([string(credentialsId: 'jenkins-sonarcloud', variable: 'TOKEN')]) {
          sh 'mvn verify sonar:sonar -Dsonar.token=$TOKEN -U'
        }
      }
    }

    stage('deploy-snapshot-libs') {
      when {
        anyOf {
          branch 'develop'
        }
      }
      steps {
        script {
          if (fileExists('module-lib/pom.xml')) {
            sh 'cat pom.xml | grep "SNAPSHOT"'
            sh 'mvn -N deploy'
            sh 'mvn -f module-lib/pom.xml deploy'
          }
        }
      }
    }

    stage('deploy-release-libs') {
      when {
        anyOf {
          branch 'master'
        }
      }
      steps {
        script {
          if (fileExists('module-lib/pom.xml')) {
            sh 'cat pom.xml | grep "SNAPSHOT" || true'
            sh 'mvn -N deploy'
            sh 'mvn -f module-lib/pom.xml deploy'
          }
        }
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: "**/target/surefire-reports/*.xml"
      step([
        $class           : 'JacocoPublisher',
        execPattern      : '**/target/jacoco.exec',
        classPattern     : '**/target/classes/',
        sourcePattern    : '**/src/main/java',
        exclusionPattern : '**/*Test.class'
      ])
      recordIssues (
        enabledForFailure: true, aggregatingResults: false,
        tools: [checkStyle(pattern: 'target/checkstyle-result.xml', reportEncoding: 'UTF-8')]
      )
      dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
    }
    success {
      archiveArtifacts artifacts: '**/target/*.jar, install/*', fingerprint: true, onlyIfSuccessful: true
    }
    changed {
      emailext(
        subject: '${DEFAULT_SUBJECT}',
        body: '${DEFAULT_CONTENT}',
        recipientProviders: [requestor(),culprits()],
        attachLog: true
      )
    }
  }
}
