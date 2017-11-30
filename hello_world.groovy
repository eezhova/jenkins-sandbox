node {
    stage('Prepare') {
       echo "Prepare"
    }
    stage('Hello World') {
        echo "Hello World!"
    }
    stage('Cleanup') {
        echo "Cleanup"
    }
}
