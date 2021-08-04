# JHipster microsercises example
Example microservices application intended to help in the migration of Artemis to microservices.

Steps for running:
1. Build Artemis gateway
- Navigate to ./Artemis
- Build for Docker using the command
``./gradlew bootJar -Pprod jibDockerBuild``
3. Build the Lecture microservice
- Navigato to ./Lecture
- Build using the same command
``./gradlew bootJar -Pprod jibDockerBuild``
5. Run the microservices application
- Navigate to ./docker
- Run with Docker Compose
``docker-compose up``
