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
}

return this;