node {
    stage('Prepare') {
       echo "Prepare"
    }
    stage('Hello World') {
        echo "Hello from '${JOB_NAME}' (${BUILD_NUMBER})",
        echo "Please go to ${BUILD_URL} and verify the build"
    }
    stage('Cleanup') {
        echo "Cleanup"
    }
}
