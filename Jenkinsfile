pipeline {
  agent any
  tools { jdk 'jdk17'; maven 'maven' }
  stages {
    stage('Build and test') { steps { sh 'mvn -B clean verify' } }
    stage('Build image') { steps { sh 'docker build -t weather-prediction:${BUILD_NUMBER} .' } }
    stage('Publish locally') {
      when { branch 'main' }
      steps {
        withCredentials([string(credentialsId: 'OPENWEATHER_API_KEY', variable: 'OPENWEATHER_API_KEY')]) {
          sh 'docker rm -f weather-prediction || true'
          sh 'docker run -d --rm --name weather-prediction -p 8081:8080 -e OPENWEATHER_API_KEY="$OPENWEATHER_API_KEY" weather-prediction:${BUILD_NUMBER}'
        }
      }
    }
  }
  post { always { junit 'target/surefire-reports/*.xml' } }
}
