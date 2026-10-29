def appImage

pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        IMAGE_NAME = 'kavyag3011/calculator-devops'
        // main builds are tagged with the plain build number, other branches get a prefix
        IMAGE_TAG = "${env.BRANCH_NAME == 'main' ? env.BUILD_NUMBER : env.BRANCH_NAME.replaceAll('[^A-Za-z0-9_.-]', '-') + '-' + env.BUILD_NUMBER}"
        // Compose files are copied here so the running stack does not depend on the build workspace
        DEPLOY_DIR = "${env.JENKINS_HOME}/deploy/calculator"
        APP_URL = 'http://localhost:8000'
        NOTIFY_TO = 'kavyagupta3030@gmail.com'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --oneline'
                script {
                    env.GIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    appImage = docker.build("${IMAGE_NAME}:${IMAGE_TAG}")
                }
                sh 'docker images "$IMAGE_NAME" --format "{{.Repository}}:{{.Tag}}  {{.Size}}" | head -5'
            }
        }

        stage('Push') {
            when { branch 'main' }
            steps {
                script {
                    docker.withRegistry('', 'DockerHubCred') {
                        appImage.push()
                        appImage.push('latest')
                    }
                }
            }
        }

        stage('Deploy') {
            when { branch 'main' }
            steps {
                sh '''
                    mkdir -p "$DEPLOY_DIR/nginx"
                    cp docker-compose.yml "$DEPLOY_DIR/"
                    cp nginx/nginx.conf "$DEPLOY_DIR/nginx/"
                    cd "$DEPLOY_DIR"

                    # Recreates only the services whose image or configuration changed
                    docker compose up -d --no-build --remove-orphans
                    docker compose exec -T nginx nginx -s reload
                    docker compose ps
                '''
                sh '''
                    for i in $(seq 1 30); do
                        if curl -fsS "$APP_URL/actuator/health" | grep -q '"status":"UP"'; then
                            echo "Health check passed (attempt $i)"
                            break
                        fi
                        if [ "$i" -eq 30 ]; then
                            echo "Health check failed after 60 seconds"
                            cd "$DEPLOY_DIR" && docker compose ps && docker compose logs --tail=50
                            exit 1
                        fi
                        sleep 2
                    done

                    RUNNING=$(docker inspect -f '{{.Config.Image}}' calculator-calculator-1)
                    echo "Running image: $RUNNING"
                    test "$RUNNING" = "$IMAGE_NAME:$IMAGE_TAG"

                    curl -fsS "$APP_URL/api/v1/add?a=2&b=3"
                    echo
                '''
            }
        }
    }

    post {
        success {
            mail to: "${env.NOTIFY_TO}",
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """Build succeeded.

Job:      ${env.JOB_NAME}
Build:    #${env.BUILD_NUMBER}
Branch:   ${env.BRANCH_NAME}
Commit:   ${env.GIT_SHORT ?: 'n/a'}
Image:    ${env.IMAGE_NAME}:${env.IMAGE_TAG}
Deploy:   ${env.BRANCH_NAME == 'main' ? 'deployed to ' + env.APP_URL : 'not deployed (only main is deployed)'}
Duration: ${currentBuild.durationString.replace(' and counting', '')}

Details: ${env.BUILD_URL}
"""
        }
        failure {
            mail to: "${env.NOTIFY_TO}",
                 subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """Build failed.

Job:      ${env.JOB_NAME}
Build:    #${env.BUILD_NUMBER}
Branch:   ${env.BRANCH_NAME}
Commit:   ${env.GIT_SHORT ?: 'n/a'}
Duration: ${currentBuild.durationString.replace(' and counting', '')}

Console log: ${env.BUILD_URL}console
Test results: ${env.BUILD_URL}testReport/
"""
        }
        cleanup {
            cleanWs()
        }
    }
}
