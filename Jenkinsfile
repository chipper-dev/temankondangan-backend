node{
    def app
    def build = "${env.BUILD_NUMBER}"
    def image = 'chippermitrais/temankondangan-backend:2.'+ build
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

                sh "docker images chippermitrais/temankondangan-backend -q | xargs --no-run-if-empty docker rmi -f"
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
        withCredentials([
            usernamePassword(credentialsId: 'dbAuth', passwordVariable: 'dbAuthPassword', usernameVariable: 'dbAuthUser'),
            string(credentialsId: 'token-secret', variable: 'tokenSecret'),
            string(credentialsId: 'firebase-database', variable: 'firebaseDb')
            ]) {
                db_username = env.dbAuthUser
                db_password = env.dbAuthPassword
                token_secret = env.tokenSecret
                firebase = env.firebaseDb

                sh "docker run --name $containerName -p 80:8181 --network chipper -e DB_URL=jdbc:postgresql://chipper-db:5432/postgres -v  /home/ubuntu/backend-config:/backend-config -e DB_USERNAME=$db_username -e DB_PASSWORD=$db_password -e TOKEN_SECRET=$token_secret -e GOOGLE_APPLICATION_CREDENTIALS=/backend-config/serviceAccountKey.json -e FIREBASE_DATABASE= --restart always -d $image"
        }
    }
}