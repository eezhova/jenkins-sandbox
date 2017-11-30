node {
    stage('Prepare') {
       echo "Prepare"
    }
    stage('Hello World') {
        mail to: 'ezhovaelena19@gmail.com',
            subject: "Hello from '${JOB_NAME}' (${BUILD_NUMBER})",
            body: "Please go to ${BUILD_URL} and verify the build"
    }
    stage('Cleanup') {
        echo "Cleanup"
    }
}
