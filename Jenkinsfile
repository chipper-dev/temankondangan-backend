node{
    def app
    def build = "${env.BUILD_NUMBER}"
    def image = 'chipper/chipper-backend:1.'+ build
    def containerName = 'chipper-backend'
    def mvnHome = tool name: 'maven-default', type: 'maven'
    def mvnCMD = "${mvnHome}/bin/mvn"

    stage('Remove Existing Docker Image') {
            script {
                def cmd = "docker ps -aqf name=$containerName"
                def container = sh (returnStdout: true, script: cmd)

                if (container) {
                    echo 'Existing container found!!! Deleting...'
                    sh "docker stop \$($cmd)"
                    sh "docker rm \$($cmd)"
                    echo 'Done.'
                }

                sh "docker images chipper/chipper-backend -q | xargs --no-run-if-empty docker rmi -f"
            }
    }
    stage('SCM Checkout') {
        checkout scm
    }
    stage('Build Source Code') {
        sh "${mvnCMD} clean package -DskipTests"
    }
    stage('Build Docker Image') {
        app = docker.build(image)
    }
    stage('Push Image') {
        withCredentials([usernamePassword(credentialsId: 'dockerHub', passwordVariable: 'dockerHubPassword', usernameVariable: 'dockerHubUser')]) {
                  sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPassword}"
                  sh "docker push $image"
         }
    }
    stage('Run Application') {
        sh "docker run --name $containerName -p 8181:8181 --network chipper --restart always -d $image"
    }
}