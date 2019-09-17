#!/usr/bin/groovy

def call(language, body) {
  
  // evaluate the body block, and collect configuration into the object
  def conf = [
    "PRODUCT_NAME": "my-product",
    "GROUP_NAME": "my-group",
    "IMPL_NAME": "my-app",
    "SERVICE_NAME": "my-app",
    "DESC": "My App description",
    "IMAGE_NAME": null,
    "PORT": "8080",
    "RESOURCES_CPU_MIN": "100m",
    "RESOURCES_CPU_MAX": "300m",
    "RESOURCES_MEM_MIN": "200Mi",
    "RESOURCES_MEM_MAX": "500Mi",
    "PROJECT_VARS": ""
  ]
  
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = conf
  body()

  if(!conf.PROJECT_VARS.isEmpty()) {
    conf.PROJECT_VARS.each { key, value ->
      conf[key]=value
    }
  }
  if(conf.get("IMAGE_NAME") == null){
    println "Image name is not set. Aborting manifest creation."
    sh "exit 1"
  }
  
  def manifest
  def manifestPath

  def hasFormParam = false
  def hasRepoFile = false
  
  // Testing file parameter
  manifestPath = extractFileParam('manifest.yaml', 'formParamManifest.yaml')
  try {
    manifest = readFile(manifestPath)
    if(!manifest.equals("")) {
      hasFormParam = true
    }
    else {
      println "Build parameter manifest not found"
    }
  }
  catch(Exception e){
    println "Build parameter manifest not found"
  }

  // Testing repo file
  try {
    manifest = readFile('manifest.yaml')
    if(!manifest.equals("")) {
      hasRepoFile = true;
    }
  }
  catch(Exception e){
    println "Repository manifest not found"
  }

  // Get manifest from build parameter
  if(hasFormParam) {
    manifest = readFile(manifestPath)
    println 'Got manifest from build parameter !'
  }
  // Get manifest from repository
  else if(hasRepoFile) {
    try {
      manifest = readFile('manifest.yaml')
      println 'Got manifest from repository !'
    }
    catch(Exception e){
      println "Repository manifest not found"
    }
  }
  // Get manifest from templates
  else {
    try {
      manifest = libraryResource("manifests/${language}.yaml")
    }
    catch(Exception e){
      println "Template ${language}.yaml could not be found. Aborting manifest creation.";
      sh "exit 1"
    }
  }
  
  def engine = new groovy.text.GStringTemplateEngine()
  def template = engine.createTemplate(manifest)

  Writable resolvedTemplate = template.make(conf)
  echo 'Template resolved'
  
  return resolvedTemplate.toString()
}