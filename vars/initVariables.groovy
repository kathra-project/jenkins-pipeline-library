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
  def implementation
  

  // EXTRA INFO FROM PATH (to be improved with pipeline variables)
  def pathAsArray = "${env.JOB_NAME}".split('/')
  print "${env.JOB_NAME}"
  groupName = pathAsArray[1].toLowerCase()
  product = groupName
  int i=0;
  while (i<pathAsArray.size()) {
    if (pathAsArray[i]=="components") {
      component = pathAsArray[i+1].toLowerCase();
    }
    else if (pathAsArray[i]=="implementations") {
      implementation = pathAsArray[i+2].toLowerCase();
    }
    i++;
  }

  if (component == null || component.isEmpty()) {
    throw new Exception("Component's name is null or empty")
  }

  // GROUP DEFINITION
  if(vars.GROUP_NAME == null || vars.GROUP_NAME.isEmpty()) {
    vars.GROUP_NAME = groupName;
  }

  // PRODUCT DEFINITION
  vars.PRODUCT_NAME = component
  if(vars.PRODUCT_NAME == null || vars.PRODUCT_NAME.isEmpty()) {
    vars.PRODUCT_NAME = component;
  }

  // SERVICE DEFINITION
  if(vars.SERVICE_NAME == null || vars.SERVICE_NAME.isEmpty()) {
    vars.SERVICE_NAME = implementation;
  }
  if (vars.SERVICE_NAME != null) {
    vars.SERVICE_NAME = vars.SERVICE_NAME.toLowerCase().replaceAll("[^0-9a-z-]*","")
  }
  // IMPLEMENTATION DEFINITION
  if(vars.IMPL_NAME == null || vars.IMPL_NAME.isEmpty()) {
    vars.IMPL_NAME = implementation;
  }
  if (vars.IMPL_NAME != null) {
    vars.IMPL_NAME = vars.IMPL_NAME.toLowerCase().replaceAll("[^0-9a-z-]*","")
  }
  imageRootName = vars.DOCKER_URL + "/" + groupName + "/" + implementation + ":"

  // VERSION DEFINITION
  if(vars.IMPL_VERSION == null || vars.IMPL_VERSION.isEmpty()) {
    vars.IMPL_VERSION = "0.0.1-SNAPSHOT";
  }

  def version = getVersion {
    projectVersion = vars.IMPL_VERSION
  }

  vars.IMPL_VERSION = version.binaryVersion

  // DOCKER IMAGE VERSION
  if(vars.IMAGE_VERSION == null || vars.IMAGE_VERSION.isEmpty()) {
    vars.IMAGE_VERSION = version.dockerVersion  
  }
 
  // NAMESPACE DEFINITION
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