node{
    def app
    def build = "${env.BUILD_NUMBER}"
    def image = 'chippermitrais/temankondangan-backend:2.'+ build
    def containerName = 'chipper-backend'
    def mvnHome = tool name: 'maven-default', type: 'maven'
    def mvnCMD = "${mvnHome}/bin/mvn"
    def remote = [:]
    remote.name = 'chippermitrais'
    remote.host = 'chippermitrais.ddns.net'
    remote.allowAnyHosts = true

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
    stage('Remove Existing Docker Image') {
        withCredentials([
            sshUserPrivateKey(credentialsId: 'chippermitrais', keyFileVariable: 'sshkey', usernameVariable: 'sshuname')
            ]) {
                remote.user = env.sshuname
                remote.identity = env.sshkey
                def cmd = "docker ps -aqf name=$containerName"
                def container = sshCommand remote: remote, command: cmd

                if (container) {
                    echo 'Existing container found!!! Deleting...'
                    sshCommand remote: remote, command: "docker stop \$($cmd)"
                    sshCommand remote: remote, command: "docker rm \$($cmd)"
                    echo 'Done.'
                }

                sshCommand remote: remote, command: "docker images chippermitrais/temankondangan-backend -q | xargs --no-run-if-empty docker rmi -f"
        }
    }
    stage('Run Application') {
        withCredentials([
            usernamePassword(credentialsId: 'dbAuth', passwordVariable: 'dbAuthPassword', usernameVariable: 'dbAuthUser'),
            string(credentialsId: 'token-secret', variable: 'tokenSecret'),
            string(credentialsId: 'firebase-database', variable: 'firebaseDb')
            sshUserPrivateKey(credentialsId: 'chippermitrais', keyFileVariable: 'sshkey', usernameVariable: 'sshuname')
            ]) {
                db_username = env.dbAuthUser
                db_password = env.dbAuthPassword
                token_secret = env.tokenSecret
                firebase = env.firebaseDb
                remote.user = env.sshuname
                remote.identity = env.sshkey

                sshCommand remote: remote, command: "docker run --name $containerName -p 80:8181 --network chipper -e DB_URL=jdbc:postgresql://chipper-db:5432/postgres -v  /home/ubuntu/backend-config:/backend-config -e DB_USERNAME=$db_username -e DB_PASSWORD=$db_password -e TOKEN_SECRET=$token_secret -e GOOGLE_APPLICATION_CREDENTIALS=/backend-config/serviceAccountKey.json -e FIREBASE_DATABASE= --restart always -d $image"
        }
    }
}