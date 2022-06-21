pipeline {
    agent any

    environment {
        dockerhub = credentials('dockerhub')
        TARGET_HOST = credentials('target_back')
        DOCKER_REPOSITORY_NAME = 'backend'
        AA='BB'
    }
    stages {

//         stage('backend build') {
//             steps {
//                 sh "pwd"
//                 sh "chmod +x gradlew"
//                 sh "./gradlew clean"
//                 sh "./gradlew bootJar"
//             }
//         }

        stage('backend dockerizing') {
            steps {
                script {
                    TAG = sh(script: 'echo $(docker images | awk -v DOCKER_REPOSITORY_NAME=$DOCKER_REPOSITORY_NAME \'{if ($1 == DOCKER_REPOSITORY_NAME) print $2;}\')', returnStdout: true).trim()
                    if(TAG =~ /^[0-9][.][0-9][0-9]$/) {
                        NEW_TAG_VER= sh(script: 'echo $(echo $TAG 0.01 | awk \'{print $1+$2}\')', returnStdout: true).trim()
                        echo "현재 버전은 ${TAG} 입니다"
                        echo "새로운 버전은 ${NEW_TAG_VER} 입니다"
                    } else {
                        echo "새롭게 만들어진 이미지 입니다."
                        NEW_TAG_VER=0.01
                    }
                    echo "before ${NEW_TAG_VER}"
                }
                echo "after ${NEW_TAG_VER}"

                sh "docker build -t $DOCKER_REPOSITORY_NAME:${NEW_TAG_VER} ."
                sh "echo before:${TAG}"
            }
        }

        stage('before pushing to dockerhub') {
            steps {
                script {
                    if (NEW_TAG_VER != 0.01) {
                        sh "docker rmi $DOCKER_REPOSITORY_NAME:${TAG}"
                    }
                }
            }
        }

        stage('pushing to dockerhub') {
            steps {
                sh '''
                    ID=$dockerhub_USR
                    PW=$dockerhub_PSW
                    docker login -u $ID -p $PW
                    echo "$DOCKER_REPOSITORY_NAME"
                    docker tag $DOCKER_REPOSITORY_NAME:$NEW_TAG_VER $ID/$DOCKER_REPOSITORY_NAME:$NEW_TAG_VER
                    docker push $ID/$DOCKER_REPOSITORY_NAME:$NEW_TAG_VER

                    docker tag $DOCKER_REPOSITORY_NAME:$NEW_TAG_VER $ID/$DOCKER_REPOSITORY_NAME:latest

                    docker push $ID/$DOCKER_REPOSITORY_NAME:latest
                '''
            }
        }

        stage('after pushing to dockerhub') {
            steps {
                sh '''
                    docker rmi $ID/$DOCKER_REPOSITORY_NAME:latest
                    docker rmi $ID/$DOCKER_REPOSITORY_NAME:$NEW_TAG_VER
                '''
            }
        }

        stage('deploy') {
            steps {
                sshagent (credentials: ['matching_backend_ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ${TARGET_HOST} '
                            hostname
                            ID=$dockerhub_USR
                            docker stop $(docker ps -a -q)
                            docker rm $(docker ps -a -q)
                            docker rmi $(docker images -q)
                            docker pull $ID/$DOCKER_REPOSITORY_NAME:latest
                            docker run -d -p 8080:8080 -it $ID/$DOCKER_REPOSITORY_NAME:latest
                        '
                    '''
                }
            }
        }
    }
}