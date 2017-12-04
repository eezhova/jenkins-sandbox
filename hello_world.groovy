gerrit = new com.mirantis.mk.Gerrit()

properties([parameters([string(defaultValue: "World", description: "Whom should I greet?", name: "Name")])])

def gerritRef
try {
  gerritRef = GERRIT_REFSPEC
} catch (MissingPropertyException e) {
  gerritRef = null
}

def defaultGitRef, defaultGitUrl
try {
    defaultGitRef = DEFAULT_GIT_REF
    defaultGitUrl = DEFAULT_GIT_URL
} catch (MissingPropertyException e) {
    defaultGitRef = null
    defaultGitUrl = null
}

node("docker") {
    stage('Checkout source code') {
       checkouted = false
    if (gerritRef) {
      // job is triggered by Gerrit
      checkouted = gerrit.gerritPatchsetCheckout ([
        credentialsId : "gerrit",
      ])
    } else if(defaultGitRef && defaultGitUrl) {
      checkouted = gerrit.gerritPatchsetCheckout(defaultGitUrl, defaultGitRef, "HEAD", GERRIT_CREDENTIALS)
    }
    if(!checkouted){
      throw new Exception("Cannot checkout gerrit patchset, GERRIT_REFSPEC and DEFAULT_GIT_REF is null")
    }
    }
    stage('Testing') {
        echo "Hello, ${params.Name}, I'm '${JOB_NAME}' (${BUILD_NUMBER})"
        echo "GERRIT_REFSPEC is ${GERRIT_REFSPEC}"
        echo "Please go to ${BUILD_URL} and verify the build"
        src_path = "${env.WORKSPACE}/avi-loadbalancer"
        image = docker.image("ubuntu:16.04")
        sh "ls -la ${env.WORKSPACE}"
        image.inside("-u root") {
            echo "Inside docker container"
            sh "uname -a"
            sh "ls -la"
            sh """apt-get update \
    && apt-get install --yes --no-install-recommends \
        software-properties-common \
        python2.7 \
        python2.7-dev \
        python-pip \
        python-requests \
    && apt-add-repository -yu ppa:ansible/ansible \
    && apt-get install --yes  --no-install-recommends ansible \
    && rm -Rf /var/lib/apt/lists/* \
    && apt-get clean \
    && pip install avisdk --upgrade \
    && ansible-galaxy install -c avinetworks.aviconfig \
    && echo "[local]\nlocalhost ansible_connection=local" > /etc/ansible/hosts"""
            def playbooks = sh script: "find avi-loadbalancer/ansible/clouds/openstack/ -name '*.yml'", returnStdout: true
            for (pb in playbooks.tokenize()) {
                sh "ansible-playbook --syntax-check ${pb}"
            }
        }
        
        
    }
    stage('Cleanup') {
        echo "Cleanup"
        currentBuild.result = 'SUCCESS'
    }
}