#!/usr/bin/env groovy

def call(String repoName) {
  container(name: 'pip') {
    //Deploy in pypi repo
    stage 'Build And Push Pip Package'
    sh 'pip install wheel'
    sh 'pip install twine'
    sh 'python3 setup.py sdist bdist_wheel'
    sh 'python3 -m twine upload dist/* -r '+repoName

    

  }

  stage ('Quality analysis') {

    container(name: 'pip') {
      sh 'apk add --no-cache curl gcc libc-dev'
      sh 'pip install nose coverage pytest-cov pylint nosexcover'

      sh 'echo "Scan $(ls -d */ | cut -f1 -d\'/\' | head -n 1)"'
      sh '[ -d tests ] && pytest --cov-branch --cov=$(ls -d */ | cut -f1 -d\'/\' | head -n 1) tests/ --cov-report xml:coverage.xml || echo "No test directory"'
      sh 'pylint $(ls -d */ | cut -f1 -d\'/\' | head -n 1) -r n --msg-template="{path}:{line}: [{msg_id}({symbol}), {obj}] {msg}" | tee pylint.txt'
    }

    container(name: 'sonar-scanner') {
      def PROJECT_NAME = readFile("setup.py").find("NAME *= *\"(.*)\"")
      PROJECT_NAME = (PROJECT_NAME =~ /NAME *= *\"(.*)\"/)[0][1]
      def PROJECT_VERSION = readFile("setup.py").find("VERSION *= *\"(.*)\"")
      PROJECT_VERSION = (PROJECT_VERSION =~ /VERSION *= *\"(.*)\"/)[0][1]
      def GIT_COMMIT = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%H'").trim()

      sh "sonar-scanner -Dsonar.python.xunit.reportPath=nosetests.xml -Dsonar.python.coverage.reportPaths=coverage.xml -Dsonar.sources=. -Dsonar.coverage.exclusions=**__init__**,tests/**,config.py,manage.py -Dsonar.exclusions=*.xml -Dsonar.scm.provider=git -Dsonar.scm.revision=${GIT_COMMIT} -Dsonar.links.ci=${env.JOB_URL} -Dsonar.links.scm=${GIT_URL} -Dsonar.python.pylint.reportPath=pylint.txt -Dsonar.language=py -Dproject.settings=\$SONAR_CONFIG_PATH -Dsonar.projectName=${PROJECT_NAME} -Dsonar.projectKey=${PROJECT_NAME} -Dsonar.projectVersion=${PROJECT_VERSION}"
    }
  }


}

return this;