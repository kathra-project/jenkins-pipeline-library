#!/usr/bin/groovy

def call(body) {
  
  def conf = [
    CLUSTER: 'kathra-prod'
  ]

  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = conf
  body()

  if(conf.get("FILE") == null || conf.get("FILE").equals("")){
    println "Manifest file not found. Aborting deployment.";
    sh "exit FAILURE"
  }

  if(conf.get("DEPLOYMANAGER_URL") == null || conf.get("DEPLOYMANAGER_URL").equals("")){
    println "Deploymanger URL not found. Aborting deployment.";
    sh "exit FAILURE"
  }


  def filecontent =  conf.get("FILE")
  def deployManagerURL = conf.get("DEPLOYMANAGER_URL")

  println filecontent
  
  writeFile file: 'manifest.yaml', text: filecontent
  
  def postFormParams =  "-F file=@./manifest.yaml"
  postFormParams +=     " -F cluster=${conf.CLUSTER}"
  postFormParams +=     " -F jobName=${conf.JOB_NAME}"
  postFormParams +=     " -F branch=${conf.BRANCH}"
  postFormParams +=     " -F namespace=${conf.NAMESPACE}"
  postFormParams +=     " -F commitId=${conf.COMMIT_ID}"

  println postFormParams;

  def response = sh("curl -X POST ${postFormParams} ${deployManagerURL}/deploy")
  println response
}
