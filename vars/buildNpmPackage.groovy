#!/usr/bin/env groovy

def call(String artifactVersion) {
	container('npm'){
		stage('Building application...'){

			stage('Installing building packages...'){
				sh('npm i')
				sh('npm run post-install')
			}

			stage('Building application to dist/ ...'){
				sh('npm run build')
			}

			stage('Preparing docker files...') {

				stage("Writing version to sources...") {
					sh("npm version ${artifactVersion} --no-git-tag-version --allow-same-version")
				}

				stage("Removing npm cluter and Installing server dependencies only..."){
					sh('rm -rf node_modules')
					sh('npm i --production')
				}
			}
		}
	}
}

return this;