#!/usr/bin/groovy
node("master") {
  def vars

  //checkout([$class: 'GitSCM', branches: [[name: '$GIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: PULL_KEY, url: GIT_URL]]])
  checkout([$class: 'GitSCM', branches: [[name: '$GIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: GIT_URL]]])
  
  def localRepoName
  localRepoName = "${env.JOB_NAME}".toLowerCase().replaceAll(/[^0-9a-z]/, "-")

  helmPackageAndPushChart localRepoName, REPOSITORY_URL, REPOSITORY_LOGIN, REPOSITORY_PASSWORD

  callbackToKathraWebHook KATHRA_WEBHOOK_URL, "${env.JOB_NAME}", GIT_BRANCH
}
