#!/usr/bin/groovy
node("docker") {
  def vars

  checkout([$class: 'GitSCM', branches: [[name: '$GIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: GIT_URL]]])
  
  def groupName
  groupName = "${env.JOB_NAME}".toLowerCase().replaceAll(/[^0-9a-z]/, "-")

  helmPackageAndPushChart groupName, CATALOG_URL, CATALOG_LOGIN, CATALOG_PASSWORD

  callbackToKathraWebHook KATHRA_WEBHOOK_URL, "${env.JOB_NAME}", GIT_BRANCH
}
