# JHipster microsercises example
Example microservices application intended to help in the migration of Artemis to microservices.
The monolith Artemis application is intended to become the JHipster gateway application which could be found in folder Artemis. It will serve the Angular client, work as an API Gateway and include the server code which will be extracted to separate microservices over the time.
The Lecture folder is an example for an extracted microservice application. It includes a Lecture entity and all CRUD actions on this entity are supported. Client code relaetd to this entity can be found in the gateway application. 

## Steps for running:
1. Build Artemis gateway
- Navigate to ./Artemis
- Build for Docker using the command
``./gradlew bootJar -Pprod jibDockerBuild``
2. Build the Lecture microservice
- Navigato to ./Lecture
- Build using the same command
``./gradlew bootJar -Pprod jibDockerBuild``
3. Run the microservices application
- Navigate to ./docker
- Run with Docker Compose
``docker-compose up``
4. Open http://localhost:8080
