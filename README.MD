# KATHRA Pipeline Library

This git repository contains a library of reusable [Jenkins Pipeline](https://jenkins.io/doc/book/pipeline/) steps and functions that can be used in your `Jenkinsfile` to help improve your Continuous Delivery pipeline.

The idea is to try promote sharing of scripts across projects where it makes sense.

## How to use this library

To use the functions in this library just add call the methods you need to use in your Jenkinsfile:

### Making changes

Feel free to reuse a version of this library as is. However if you want to make changes, please create a new branch in this repository and change it in your branch!

If you do make local changes we'd love a `Merge Request` back though! We love contributions and merge requests!

### Requirements

These flows make use of the [Fabric8 DevOps Pipeline Steps](https://github.com/fabric8io/fabric8-jenkins-workflow-steps) and [kubernetes-plugin](https://github.com/jenkinsci/kubernetes-plugin) which help when working with [Fabric8 DevOps](http://fabric8.io/guide/cdelivery.html) in particular for clean integration with the [Hubot chat bot](https://hubot.github.com/) and human approval of staging, promotion and releasing.

### Functions from the Jenkins global library

#### initProject

- initialize usefull variables to handle multibranch builds and deployments
- return an object containing these variables :
    - ENV_NAME: The name of the platform or project (eg. KATHRA, VITAL, SCE, DCIDE, MOST, ISE)
    - BRANCH_NAME: The name of the branch which is currently being built (eg. master, test, feature1)
    - JOB_NAME: The name of the jenkins job (eg. SmartHome, htmlCleaner, kathra-catalog)
    - VERSION: Version which will be used in the image tag
    - STEPS: Steps to go through when building master branch(default:["Staging","Approve","Production"])
    - IMAGE_NAME: The name of the docker image with registry (eg. 10.2.133.23:80/kathra/kathra-platformmanager:1.2.1)

example:
```groovy
    def _project = initProject {}
```

You can override some parameters:
```groovy
    def _project = initProject {
        envName = "myPlatformName"
        version = "myVersion"
        steps = ["MyStep1","Approve","MyStep2"]
    }
```

**Note that you cannot use "Approve" as a first step**

After initialisation, you can access variables using the returned object:
```groovy
    def project = initProject {}
    echo "${project.ENV_NAME}"
```

#### applySteps

- run declared steps found in the parameter object (only for the master branch)
- possibles steps are:
    - Approve : requests approval in a pipeline (you cannot use this as a first step)
    - Staging : deploy pod in the namespace 'project.ENV_NAME-staging' (eg. kathra-staging, vital-staging, sce-staging...)
    - Production : deploy pod in the namespace 'project.ENV_NAME-production' (eg. kathra-production, vital-production, sce-production...)
- if the branch isn't 'master', you cannot choose steps and there is only one step which deploy pod in the namespace 'project.ENV_NAME-project.BRANCH_NAME' (eg. kathra-bugfix, vital-feature2, sce-test...)

example:    
```groovy
    def _project = initProject {}

    applySteps {
      project = _project
    }
```

You can pass a resource file to be deployed:
```groovy
    def rc = """
        ...KUBERNETES_RESOURCE...
    """
    applySteps {
      project = _project
      file = rc
    }
```