#!/usr/bin/groovy

def call(body) {

    // evaluate the body block, and collect configuration into the object
    def config = [cluster: '',
                  file: '',
                  commitId:'',
                  jobName:'',
                  branch:''
                  ]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def project = ''

    if (config.project == '') {
        project = initProject {}
    } else {
        project = config.project
    }

    envName = "toto"
    stage "Rollout ${envName}"
    sh "curl http://www.kathra.org"
    

}