# Ark Invest API - take home task

The goal is to create simple but production ready CRUD API using Spring Boot and Java

To run server using Maven:
```shell
./mvnw spring-boot:run
```

To run whole application with DB locally using Docker:
```shell
docker compose up
```

## Design decisions and considerations

On whether to use JDBC vs JPA
We are choosing JDBC here for somplicity and flexability,
if we need to use a fancy index or join, it should be easier to implement.

Some parameter validation implemented, mostly for demonstration, there still might be possibilities to brake the system.
All functionality covered with unit tests (we are using in-memory Sqlite DB for repository testing).
Sqlite have certain limitations, if we want to use it - we will have to use simple alternatives in Postgres (or other DB system in Prod).

## Things that sill need work to make this truly prod-ready system

Logging - currently we're only relying on Spring existing logging/error handling, for prod system we will need to
setup application logging, preferably by using lightwigh logging framework to avoid log4j surprises.
Observability setup is also highly encouraged especially in this will be part of big microservice application
to add the ability to track request/responce among several services.

Monitoring and alerting - outages will happen, we would need to be notified about them and be able to fail over or
restore from a backup in another service provider. We can use systems like Sentry.io to get notified.

Deployment - currently this can be either run in maven/IDE or in Docker using Docker compose.
For Prod we would need to deploy it to, say, AWS Fargate with API Gateway or similar technology.
Depending on if this will be external facing app the requirements will be much stricter.

Parameter validation/Business logic separation
Currently business logic is trivial, we moved it to `business` package just for demo, but we would need to 
thing how to best structure it depending on the tasks and needs.
For params validation - rule of thumb - we should be strict with our output that we produce, and permissinve with 
the input we get from the outside (as long as we can make sense of it).

Secrets management - currently we have .env file, but in real app we would need to use something like 
HarshiCorp vault or AWS Secrets Manager to inject secrets into our apps as env vars (or similar).

Component and Live dependency testing - we would need to add more tests for this system to make sure it plays well with 
other systems around it. We can have a QA team or try to automat it.

AI usage - I was using Kimi K2.7-coder, works pretty good once it have good examples, does not know about Spring 4.
File AGENTS.md outlines recommended approach.
