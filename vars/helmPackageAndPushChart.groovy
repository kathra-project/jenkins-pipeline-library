#!/usr/bin/env groovy

def call(String repositoryName, String repositoryUrl, String username, String password) {
	container('helm') {
		stage('Install Helm Plugin') {
			sh "apk add git curl"
			sh "helm plugin list | grep push && echo 'Plugin push already installed' || helm plugin install https://github.com/chartmuseum/helm-push"
		}
        stage('Package Chart') {      
			sh "helm package ."
		}
		stage('Configure Helm') {      
			sh "helm repo add ${repositoryName} ${repositoryUrl} --username ${username} --password ${password}"
		}
        stage('Publish Chart') {      
			sh "helm push . ${repositoryName}"
		}
    }
}
return this;