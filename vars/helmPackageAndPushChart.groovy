#!/usr/bin/env groovy

def call(String repositoryName, String repositoryUrl, String username, String password) {
	container('helm') {
        stage('Package Chart') {      
			sh "helm package ."
		}
		stage('Configure Helm') {      
			sh "helm add repo ${repositoryName} ${repositoryUrl}"
		}
        stage('Publish Chart') {      
			sh "helm --username ${username} --password ${password} . ${repositoryName}"
		}
    }
}
return this;