pipeline {
    agent any

    environment {
        dockerhub = credentials('dockerhub')
        TARGET_HOST = credentials('target_back')
    }
    stages {
        stage('backend dockerizing') {
            steps {
                sh "pwd"
                sh "chmod +x gradlew"
                sh "./gradlew clean"
                sh "./gradlew bootJar"

                sh """
                ID=$dockerhub_USR
                PW=$dockerhub_PSW
                DOCKER_REPOSITORY_NAME=backend

                TAG=$(docker images | awk -v DOCKER_REPOSITORY_NAME=$DOCKER_REPOSITORY_NAME '{if ($1 == DOCKER_REPOSITORY_NAME) print $2;}')

                # 만약 [0-9]\.[0-9]{1,2} 으로 버전이 관리된 기존의 이미지 일 경우
                if [[ $TAG =~ [0-9]\.[0-9]{1,2} ]]; then
                    NEW_TAG_VER=$(echo $TAG 0.01 | awk '{print $1+$2}')
                    echo "현재 버전은 $TAG 입니다."
                    echo "새로운 버전은 $NEW_TAG_VER 입니다"

                # 그 외 새롭게 만들거나, lastest or lts 등 tag 일 때
                else
                    # echo "새롭게 만들어진 이미지 입니다."
                    NEW_TAG_VER=0.01
                fi

                # 현재 위치에 존재하는 DOCKER FILE을 사용하여 빌드
                docker build -t $DOCKER_REPOSITORY_NAME:$NEW_TAG_VER .

                docker login -u $ID -p $PW

                if [ $NEW_TAG_VER != "0.01" ]; then
                    docker rmi $DOCKER_REPOSITORY_NAME:$TAG
                fi

                docker tag $DOCKER_REPOSITORY_NAME:$NEW_TAG_VER $ID/$DOCKER_REPOSITORY_NAME:$NEW_TAG_VER
                docker push $ID/$DOCKER_REPOSITORY_NAME:$NEW_TAG_VER

                docker tag $DOCKER_REPOSITORY_NAME:$NEW_TAG_VER $ID/$DOCKER_REPOSITORY_NAME:latest

                docker push $ID/$DOCKER_REPOSITORY_NAME:latest

                # 버전 관리에 문제가 있어 latest를 삭제
                docker rmi $ID/$DOCKER_REPOSITORY_NAME:latest
                docker rmi $ID/$DOCKER_REPOSITORY_NAME:$NEW_TAG_VER
                """
            }
        }



//         stage('pushing to dockerhub') {
//             steps {
//                 sh "echo $dockerhub_PSW | docker login -u $dockerhub_USR --password-stdin"
//                 sh "docker push wkemrm12/backend"
//             }
//         }

//         stage('deploy') {
//             steps {
//                 sshagent (credentials: ['matching_backend_ssh']) {
//                     sh """
//                         ssh -o StrictHostKeyChecking=no ${TARGET_HOST} '
//                             hostname
//                             docker stop $(docker ps -a -q)
//                             docker rm $(docker ps -a -q)
//                             docker pull wkemrm12/backend
//                             docker run -d -p 8080:8080 -it wkemrm12/backend:latest
//                         '
//                     """
//                 }
//             }
//         }
    }
}