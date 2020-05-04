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
    withCredentials([
        usernamePassword(credentialsId: 'dbAuth', passwordVariable: 'dbAuthPassword', usernameVariable: 'dbAuthUser'),
        string(credentialsId: 'firebase-database', variable: 'firebaseDb'),
        usernamePassword(credentialsId: 'emailAuth', passwordVariable: 'emailPassword', usernameVariable: 'emailUser')
        ]) {
            sh "${mvnCMD} clean package -Dspring.datasource.url=jdbc:postgresql://chippermitrais.ddns.net:5432/postgres -Dspring.datasource.username=$env.dbAuthUser -Dspring.datasource.password=$env.dbAuthPassword -Dapp.firebase.databaseUrl=$env.firebaseDb -Dapp.firebase.googleCredentials=/backend-config/serviceAccountKey.json -Dspring.mail.username=$env.emailUser -Dspring.mail.password=$env.emailPassword"
        }
    }
    stage('SonarQube analysis') {
        withSonarQubeEnv('sonarqube') {
            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.6.0.1398:sonar'
        }

        sleep 10
        timeout(time: 5, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout
            def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
            if (qg.status == 'ERROR') {
                error "Pipeline aborted due to quality gate failure: ${qg.status}"
            }
        }
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
    stage('Remove Existing Docker Image & Run Application') {
        withCredentials([
            usernamePassword(credentialsId: 'dbAuth', passwordVariable: 'dbAuthPassword', usernameVariable: 'dbAuthUser'),
            string(credentialsId: 'token-secret', variable: 'tokenSecret'),
            string(credentialsId: 'firebase-database', variable: 'firebaseDb'),
            usernamePassword(credentialsId: 'emailAuth', passwordVariable: 'emailPassword', usernameVariable: 'emailUser'),
            sshUserPrivateKey(credentialsId: 'chippermitrais', keyFileVariable: 'sshkey', usernameVariable: 'sshuname')
            ]) {
                remote.user = env.sshuname
                remote.identityFile = env.sshkey
                def cmd = "docker ps -aqf name=$containerName"
                def container = sshCommand remote: remote, command: cmd

                if (container) {
                    echo 'Existing container found!!! Deleting...'
                    sshCommand remote: remote, command: "docker stop \$($cmd)"
                    sshCommand remote: remote, command: "docker rm \$($cmd)"
                    echo 'Done.'
                }

                sshCommand remote: remote, command: "docker images chippermitrais/temankondangan-backend -q | xargs --no-run-if-empty docker rmi -f"

                db_username = env.dbAuthUser
                db_password = env.dbAuthPassword
                token_secret = env.tokenSecret
                user_email = env.emailUser
                password_email = env.emailPassword
                firebase = env.firebaseDb

                sshCommand remote: remote, command: "docker run --name $containerName -p 80:8181 --network chipper -e DB_URL=jdbc:postgresql://chipper-db:5432/postgres -v  /home/ubuntu/backend-config:/backend-config -e DB_USERNAME=$db_username -e DB_PASSWORD=$db_password -e TOKEN_SECRET=$token_secret -e GOOGLE_APPLICATION_CREDENTIALS=/backend-config/serviceAccountKey.json -e FIREBASE_DATABASE=$firebase -e EMAIL_USER=$user_email -e EMAIL_PASSWORD=$emailPassword --restart always -d $image"
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}