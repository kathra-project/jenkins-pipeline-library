#!/usr/bin/env groovy

def call() {
  // Get version in setup.py  
  def setupFile = readFile("setup.py")
  def version = setupFile.find("VERSION *= *\"(.*)\"")
  version = (version =~ /VERSION *= *\"(.*)\"/)[0][1]
  return [version, setupFile]
}