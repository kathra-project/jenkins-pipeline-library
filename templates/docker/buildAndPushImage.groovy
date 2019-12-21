#!/usr/bin/groovy
node("docker") {
  def vars

  checkout([$class: 'GitSCM', branches: [[name: '$GIT_BRANCH']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: GIT_URL]]])
  
  def groupName
  groupName = "${env.JOB_NAME}".toLowerCase().replaceAll(/[^0-9a-z]/, "-")

  buildAndPushDockerImage2 groupName+"/"+IMAGE_NAME+":"+IMAGE_TAG, REGISTRY_URL, REGISTRY_LOGIN, REGISTRY_PASSWORD

  callbackToKathraWebHook KATHRA_WEBHOOK_URL, "${env.JOB_NAME}", GIT_BRANCH
}
