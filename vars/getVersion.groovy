#!/usr/bin/groovy

def call(body) {
  
  def callConfig = [
    projectVersion: '0.0.1-SNAPSHOT'
  ]

  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = callConfig
  body()

  def version = [
    dockerVersion: '',
    binaryVersion: ''
  ]

  stage('Preparing new version...'){
    
    def shortGitCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()+"-${BUILD_NUMBER}"
    
    // RELEASE BRANCH
    if(GIT_BRANCH.equals("master")){
      version.dockerVersion = callConfig.projectVersion
      version.binaryVersion = callConfig.projectVersion
    }
    // DEV BRANCH
    else if(GIT_BRANCH.equals("dev") ){
      version.dockerVersion = callConfig.projectVersion.replace("-SNAPSHOT", "") + "-" + shortGitCommit
      version.binaryVersion = callConfig.projectVersion
    }
    // RC BRANCH
    else if (GIT_BRANCH.toUpperCase().endsWith("-RC")){
      version.dockerVersion = callConfig.projectVersion.replace("-RC-SNAPSHOT", "-RC") + "-" + shortGitCommit
      version.binaryVersion = callConfig.projectVersion
    }
    // FEATURE BRANCH
    else {
      version.dockerVersion = callConfig.projectVersion.replace("-SNAPSHOT", "") + "-" + GIT_BRANCH + "-" + shortGitCommit
      version.binaryVersion = callConfig.projectVersion.replace("-SNAPSHOT", "-" + GIT_BRANCH + "-SNAPSHOT")
    }
  }

  return version
}
