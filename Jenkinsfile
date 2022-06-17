pipeline {
    agent any

    environment {
        dockerhub = credentials('dockerhub')
    }

    stages {
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

        stage('upload') {
            steps {
                   withAWS(credentials:'AwsCredentials') {
                    sh 'echo "hello Jenkins">hello.txt'
                    s3Upload(file:'project-matching.jar', bucket:'elasticbeanstalk-ap-northeast-2-406669924561', path:'/var/lib/jenkins/workspace/jenkins-ci-cd/build/libs/project-matching.jar')
                   }
            }
        }
    }
}