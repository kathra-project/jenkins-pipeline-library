#!/usr/bin/groovy
node("docker") {
  def vars

  checkout([$class: 'GitSCM', branches: [[name: '$GIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: GIT_URL]]])
  
  def groupName
  groupName = "${env.JOB_NAME}".toLowerCase().replaceAll(/[^0-9a-z]/, "-")

  helmPackageAndPushChart groupName, BINARY_REPOSITORY_URL, BINARY_REPOSITORY_USERNAME, BINARY_REPOSITORY_PASSWORD

  callbackToKathraWebHook KATHRA_WEBHOOK_URL, "${env.JOB_NAME}", GIT_BRANCH
}
