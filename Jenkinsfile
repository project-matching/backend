pipeline {
    agent any

    environment {
        DOCKERHUB = credentials('dockerhub')
        TARGET_HOST = credentials('target_back')
        DOCKER_REPOSITORY_NAME = 'backend'
        JASYPT_PASSWORD = credentials('jasypt_password')
    }
    stages {
        stage('backend test') {
            steps {
                sh "pwd"
                echo "빌드 실행 권한 추가"
                sh "chmod +x gradlew"
                sh "./gradlew clean"
                echo "테스트"
                sh "./gradlew test"
            }
        }

        stage('backend build') {
            steps {
                echo "빌드"
                sh "./gradlew bootJar"
            }
        }

        stage('backend dockerizing') {
            steps {
                script {
                    TAG = sh(script: 'echo $(docker images | awk -v DOCKER_REPOSITORY_NAME=$DOCKER_REPOSITORY_NAME \'{if ($1 == DOCKER_REPOSITORY_NAME) print $2;}\')', returnStdout: true).trim()
                    if(TAG ==~ /^[0-9]?[.]?[0-9]?[0-9]$/) {
                        NEW_TAG_VER= sh(script: "echo \$(echo ${TAG} 0.01 | awk '{print \$1+\$2}')", returnStdout: true).trim()
                        echo "현재 버전은 ${TAG} 입니다"
                        echo "새로운 버전은 ${NEW_TAG_VER} 입니다"
                    } else {
                        echo "새롭게 만들어진 이미지 입니다."
                        NEW_TAG_VER=0.01
                    }
                }

                echo "${NEW_TAG_VER} 버전 도커 빌드"
                sh "docker build -t $DOCKER_REPOSITORY_NAME:${NEW_TAG_VER} ."
            }
        }

        stage('before pushing to dockerhub') {
            steps {
                script {
                    if (NEW_TAG_VER != 0.01) {
                        echo "기존 ${TAG} 버전 도커 이미지 삭제"
                        sh "docker rmi $DOCKER_REPOSITORY_NAME:${TAG}"
                    }
                }
            }
        }

        stage('pushing to dockerhub') {
            steps {
                sh """
                    echo '도커 허브 로그인'
                    docker login -u $DOCKERHUB_USR -p $DOCKERHUB_PSW

                    echo '${NEW_TAG_VER} 버전 도커 TAG 생성'
                    docker tag $DOCKER_REPOSITORY_NAME:${NEW_TAG_VER} $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:${NEW_TAG_VER}

                    echo '${NEW_TAG_VER} 버전 도커 허브 푸시'
                    docker push $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:${NEW_TAG_VER}

                    echo 'LATEST 버전 도커 TAG 생성'
                    docker tag $DOCKER_REPOSITORY_NAME:${NEW_TAG_VER} $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:latest

                    echo 'LATEST 버전 도커 허브 푸시'
                    docker push $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:latest
                """
            }
        }

        stage('after pushing to dockerhub') {
            steps {
                sh """
                    echo '배포 서버에 남아 있는 도커 이미지 삭제'
                    docker rmi $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:latest
                    docker rmi $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:${NEW_TAG_VER}
                """
            }
        }

        stage('deploy') {
            steps {
                sshagent (credentials: ['matching_backend_ssh']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no $TARGET_HOST '
                            hostname
                            echo '도커 컨테이너 존재시 기존 컨테이너 중지 및 삭제'
                            if [ \$(docker ps -a -q | wc -l) -gt 0 ] ; then
                                docker stop \$(docker ps -a -q)
                                docker rm \$(docker ps -a -q)
                            fi

                            echo '도커 이미지 존재시 기존 이미지 삭제'
                            if [ \$(docker images -q | wc -l) -gt 0 ] ; then
                                docker rmi \$(docker images -q)
                            fi

                            echo '도커 허브에서 LATEST 버전 PULL'
                            docker pull $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:latest
                            echo 'LATEST 버전 실행'
                            docker run -d -p 8080:8080 -it -e JAVA_OPTS=-Djasypt.encryptor.password=$JASYPT_PASSWORD $DOCKERHUB_USR/$DOCKER_REPOSITORY_NAME:latest
                        '
                    """
                }
            }
        }
    }
}