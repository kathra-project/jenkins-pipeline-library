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
    version.dockerVersion = callConfig.projectVersion
    version.binaryVersion = callConfig.projectVersion
  }

  return version
}
