pipeline {
    agent any

    environment {
        dockerhub = credentials('dockerhub')
        TARGET_HOST = credentials('target_back')
    }

    stages {
        stage("init") {

        }

        stage('backend dockerizing') {
            steps {
                sh "pwd"
                sh "chmod +x gradlew"
                sh "./gradlew clean"
                sh "./gradlew bootJar"

                sh "docker build -t wkemrm12/backend ."

            }
        }

        stage('pushing to dockerhub') {
            steps {
                sh "echo $dockerhub_PSW | docker login -u $dockerhub_USR --password-stdin"
                sh "docker push wkemrm12/backend"
            }
        }

        stage('deploy') {
            steps {
                sshagent (credentials: ['matching_backend_ssh']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${TARGET_HOST} '
                            hostname
                            docker pull leeworld9/backend
                            docker run -d -p 8080:8080 -it leeworld9/backend:latest
                        '
                    """
                    }
                }
            }
        }
    }
}