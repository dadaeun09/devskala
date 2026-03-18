pipeline {
    agent any

    environment {
        APP_NAME       = 'devskala-backend'
        IMAGE_NAME     = 'devskala-backend'
        IMAGE_TAG      = "${BUILD_NUMBER}"
        CONTAINER_NAME = 'devskala-backend-container'

        APP_PORT       = '8080'
        HOST_PORT      = '8081'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout from GitHub') {
            steps {
                echo 'GitHub에서 소스코드를 가져옵니다.'
                checkout scm
            }
        }

        stage('Build Jar') {
            steps {
                echo 'Gradle로 Spring Boot JAR를 빌드합니다.'
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Prepare App Jar') {
            steps {
                echo '실행용 JAR를 app.jar로 준비합니다.'
                sh '''
                    JAR_FILE=$(find build/libs -name "*.jar" ! -name "*plain.jar" | head -n 1)
                    cp "$JAR_FILE" app.jar
                    ls -al app.jar
                '''
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Docker 이미지를 생성합니다.'
                sh '''
                    docker build \
                      -t ${IMAGE_NAME}:${IMAGE_TAG} \
                      -t ${IMAGE_NAME}:latest \
                      .
                '''
            }
        }

        stage('Stop Old Container') {
            steps {
                echo '기존 컨테이너를 제거합니다.'
                sh 'docker rm -f ${CONTAINER_NAME} || true'
            }
        }

        stage('Run New Container') {
            steps {
                echo '새 컨테이너를 실행합니다.'
                sh '''
                    docker run -d \
                      --name ${CONTAINER_NAME} \
                      -p ${HOST_PORT}:${APP_PORT} \
                      ${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo '애플리케이션 상태를 확인합니다.'
                sh '''
                    sleep 10
                    curl -f http://host.docker.internal:${HOST_PORT}/health
                '''
            }
        }
    }

    post {
        success {
            echo "배포 성공: ${IMAGE_NAME}:${IMAGE_TAG}"
            sh 'docker ps -a'
        }
        failure {
            echo '배포 실패'
            sh 'docker ps -a || true'
            sh 'docker logs ${CONTAINER_NAME} || true'
        }
        always {
            echo '파이프라인 종료'
        }
    }
}
