# Ark Invest API - take home task

The goal is to create simple but production-ready CRUD API using Spring Boot and Java

To run whole application with DB locally using Docker:
```shell
./mvnw clean package && docker compose up
```

Alternatively: to run API server using Maven, assuming DB is running separately
adjustments to DB connection settings will be needed to make it fully work
```shell
./mvnw spring-boot:run
```

## Design decisions and considerations

On whether to use JDBC versus JPA
We are choosing JDBC here for simplicity and flexibility,
if we need to use a fancy index or join, it should be easier to implement.

Some parameter validation implemented, mostly for demonstration, there still might be possibilities to break the system.
All functionality covered with unit tests (we are using in-memory SQLite DB for repository testing).
SQLite have certain limitations, if we want to use it - we will have to use simple alternatives in PostgreSQL (or other DB system in Prod).

## Things that still need work to make this truly prod-ready system

Logging - currently we're only relying on Spring existing logging/error handling, for prod system we will need to
setup application logging, preferably by using lightweight logging framework to avoid Log4j surprises.
Observability setup is also highly encouraged especially in this will be part of big microservice application
to add the ability to track request/response among several services.

Monitoring and alerting - outages will happen, we would need to be notified about them and be able to failover or
restore from a backup with another provider (or in another region). We can use systems like Sentry.io to get notified.

Deployment - currently this can be either run in Maven/IDE or in Docker using Docker Compose.
For Prod we would need to deploy it to, say, AWS Fargate with API Gateway or similar technology.
Depending on if this will be external-facing app the requirements will be much stricter.

Parameter validation/Business logic separation
Currently business logic is trivial, we moved it to `business` package just for demo, but we would need to 
think how to best structure it depending on the tasks and needs.
For params validation - rule of thumb - we should be strict with the output that we produce, and permissive with 
the input we get from the outside (as long as we can make sense of it).

Secrets management - currently we have a .env file, but in real app we would need to use something like 
HashiCorp Vault or AWS Secrets Manager to inject secrets into our apps as env vars (or similar).

Component and Live dependency testing - we would need to add more tests for this system to make sure it plays well with 
other systems around it. We can have a QA team or try to automate it.

AI usage - I was using Kimi K2.7-coder, works pretty well once it have good examples, does not know about Spring Boot 4
(it confuses it with version 3).
AGENTS.md outlines recommended approach.
