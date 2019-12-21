#!/usr/bin/groovy

def call(callbackURL, pipeline, branch) {

    stage('Callback to Kathra') {      
        sh "curl ${callbackURL}?pipeline=${pipeline}&branch=${branch}"
    }
}