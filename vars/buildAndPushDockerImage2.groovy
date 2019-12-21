#!/usr/bin/env groovy

def call(String imageName, String registryUrl, String registryLogin, String registryPassword) {
	container('docker') {
		stage('Login to registry') {      
			sh "docker login --username ${registryLogin} --password ${registryPassword} ${registryUrl}"
		}
		stage('Build image') {      
			sh "docker build -t ${registryUrl}/${imageName} ."
		}
        
		stage('Build image') {      
			sh "docker push ${registryUrl}/${imageName}"
		}
    }
}
return this;