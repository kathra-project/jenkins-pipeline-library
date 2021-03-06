#!/usr/bin/env groovy

def call(String imageName, String imageAltName) {
	container('docker') {
		stage('Build And Push Docker Image') {      
			sh "docker build -t ${imageName} . ${DOCKER_BUILD_EXTRA_ARGS}"
			sh "docker push ${imageName}"
		}
       if (imageAltName != null && !imageAltName.isEmpty()) {
           sh "docker tag ${imageName} ${imageAltName}"
           sh "docker push ${imageAltName}"
    	}
    }
}
return this;