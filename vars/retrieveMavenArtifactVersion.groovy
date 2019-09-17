#!/usr/bin/env groovy

def call() {
  def m = readMavenPom file: 'pom.xml'
  return m.getVersion()
}