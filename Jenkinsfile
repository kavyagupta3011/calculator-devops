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
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --oneline'
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
    }
}
