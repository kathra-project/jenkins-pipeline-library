#!/usr/bin/groovy
def call(body) {

  /** Context configuration
   */
  def vars = [
    PRODUCT_NAME: "",
    GROUP_NAME: "",
    SERVICE_NAME: "",
    IMPL_NAME:"",
    IMPL_VERSION: "",
    IMAGE_VERSION: "",
    API_VERSION: "",
    DOCKER_URL: "",
    BRANCH_NAME: "",
    ENV_NAME: "",
    IMAGE_NAME: "",
    IMAGE_ALT_NAME: "",
    DEPLOYMANAGER_URL: "",
    PIP_REPO: ""
  ]
  
  /*
  def project = [
    PRODUCT_NAME: "kathra",
    GROUP_NAME: "kathra",
    SERVICE_NAME: "catalogmanager",
    IMPL_NAME:"k8s",
    IMPL_VERSION: "1.2.0-SNAPSHOT",
    API_VERSION: "1",
    DOCKER_URL: "registry.hub.docker.com",
    BRANCH_NAME: "dev",
    ENV_NAME: "kathra-staging",
    IMAGE_NAME: "registry.hub.docker.com/kathra/kathra-catalogmanager/kathra-catalogmanager-k8s",
    JOB_NAME: "kathra/kathra-catalogmanager/java/kathra-catalogmanager-k8s"
    JOB_NAME: "kathra-projects/group/subgroup/subgroup2/components/compoentName/implementations/JAVA/kathra-catalogmanager-k8s"
  ]
  */

  // Overriding predefined values with the ones passed in config object
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = vars
  body()
  
  /* Generating values for empty vars */
  if(vars.BRANCH_NAME == null || vars.BRANCH_NAME.isEmpty()) {
    vars.BRANCH_NAME = "${env.GIT_BRANCH}".replaceAll('/','-').replaceAll(':','-').toLowerCase();
    if(vars.BRANCH_NAME == null || vars.BRANCH_NAME.isEmpty()) {
      vars.BRANCH_NAME = "dev"
    }
  }
  
  // Determine variables for kathra-projects



  if(vars.DOCKER_URL == null || vars.DOCKER_URL.isEmpty()) {
    vars.DOCKER_URL = "${env.DOCKER_URL}";
  }

  if(vars.DEPLOYMANAGER_URL == null || vars.DEPLOYMANAGER_URL.isEmpty()) {
    vars.DEPLOYMANAGER_URL = "${env.DEPLOYMANAGER_URL}";
  }
  
  if(vars.PIP_REPO == null || vars.PIP_REPO.isEmpty()) {
    vars.PIP_REPO = "${env.PIP_REPO}";
  }
  
  def groupName
  def product
  def service
  def component
  def impl
  
  // Determine variables for kathra-projects
  tmp = "${env.JOB_NAME}".split('/')
  if (tmp[0].toLowerCase() == "kathra-projects") {
    groupName = tmp[1].toLowerCase()
    product = groupName
    int i=2;
    while (i<tmp.size()) {
      if (tmp[i]=="components") {
        component = tmp[i+1].toLowerCase();
      }
      else if (tmp[i]=="implementations") {
        impl = tmp[i+2].toLowerCase();
      }
      i++;
    }
      imageRootName = vars.DOCKER_URL + "/" + groupName + "/" + impl + ":"
  }

  // Old-fashioned kathra projects
  else {
    groupName = "${env.JOB_NAME}".split('/')[0].toLowerCase()
    tmp = "${env.JOB_BASE_NAME}".split('-')
    product = tmp[0].toLowerCase()
    imageRootName = (vars.DOCKER_URL + "/" + "${env.JOB_NAME}").toLowerCase() + ":"

    // Group name match product name
    if(tmp.size() != 3) {
      //Not kathra compliant, guessing (product-service/impl)
      service = tmp[tmp.size() -1].toLowerCase()
      impl = service
    }
    else {
      //Kathra compliant (product-service-impl)
      service = tmp[1].toLowerCase()
      impl = tmp[2].toLowerCase()
    }
  }

  if(vars.GROUP_NAME == null || vars.GROUP_NAME.isEmpty()) {
    vars.GROUP_NAME = groupName;
  }

  if(!vars.GROUP_NAME.equals(product)) {
    service=product+'-'+service
  }
  
  vars.PRODUCT_NAME = vars.GROUP_NAME
 


  if(vars.SERVICE_NAME == null || vars.SERVICE_NAME.isEmpty()) {
    vars.SERVICE_NAME = service;
  }

  if(vars.IMPL_NAME == null || vars.IMPL_NAME.isEmpty()) {
    vars.IMPL_NAME = impl;
  }

  if(vars.IMPL_VERSION == null || vars.IMPL_VERSION.isEmpty()) {
    vars.IMPL_VERSION = "0.0.1-SNAPSHOT";
  }

  def version = getVersion {
    projectVersion = vars.IMPL_VERSION
  }

  vars.IMPL_VERSION = version.binaryVersion

  if(vars.IMAGE_VERSION == null || vars.IMAGE_VERSION.isEmpty()) {
    vars.IMAGE_VERSION = version.dockerVersion  
  }
 
  vars.ENV_NAME = vars.GROUP_NAME
  if(!vars.BRANCH_NAME.equals("master")) {
      vars.IMAGE_ALT_NAME = imageRootName + "dev"
      vars.ENV_NAME = vars.ENV_NAME + "-dev"
  }

  vars.ENV_NAME = vars.ENV_NAME.toLowerCase().replaceAll("[^0-9a-z-]*","")
  
  // Set Image name
  vars.IMAGE_NAME = imageRootName + vars.IMAGE_VERSION
  // Temporary fix ?
  if(null == vars.IMAGE_ALT_NAME){
      vars.IMAGE_ALT_NAME = ""
  }
  
  return vars;
}