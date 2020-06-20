#!/usr/bin/env groovy

def call(String artifactVersion, boolean includeDependencies = false) {
	container(name: 'maven') {
		stage 'Enable pgp key if present'
		def gpgKey = sh(returnStatus:true, script: "[ -f /home/jenkins/gpg/privateKathra.key ]")
		stage 'Build And Push Maven Artifact'

		sh 'mvn versions:set -DnewVersion=' + artifactVersion
		def mvnDeploy = 'mvn clean deploy -U'

		if (gpgKey==0) {
			sh 'gpg --import /home/jenkins/gpg/privateKathra.key'
			mvnDeploy += ' -P gpg'
		}else{
			sh 'echo /home/jenkins/gpg/privateKathra.key not present : gpg encryption will be unavailable'
		}
		
		sh mvnDeploy

		stage ('Quality analysis') {
    		sh 'mvn sonar:sonar'
		}

		if(includeDependencies) {
			stage('Preparing resources for docker image...') {      
				sh 'mvn dependency:copy-dependencies'
			}
		}
	}
}

return this;