# Ark Invest API - take home task

The goal is to create a simple but production-ready CRUD API using Spring Boot and Java

To run the whole application with the DB locally using Docker:
```shell
./mvnw clean package && docker compose up
```

Alternatively, to run the API server using Maven, assuming the DB is running separately,
adjustments to DB connection settings will be needed to make it fully work
```shell
./mvnw spring-boot:run
```

## Design decisions and considerations

Choosing between JDBC and JPA

We are choosing JDBC here for simplicity and flexibility;
if we need to use a fancy index or join, it should be easier to implement.

Some parameter validation is implemented, mostly for demonstration; there may still be ways to break the system.
All functionality is covered with unit tests (we are using an in-memory SQLite DB for repository testing).
SQLite has certain limitations; if we want to use it, we will have to use simple alternatives in PostgreSQL (or another DB system in production).

## Things that still need work to make this truly prod-ready system

Logging: currently we're only relying on Spring's existing logging/error handling; for a prod system, we will need to
set up application logging, preferably using a lightweight logging framework to avoid Log4j surprises.
Observability setup is also highly encouraged, especially if this will be part of a big microservice application,
to add the ability to track requests/responses between services.

Monitoring and alerting: outages will happen; we would need to be notified about them and be able to failover or
restore from a backup with another provider (or in another region). We can use systems like Sentry.io to get notified.

Deployment: currently this can be run either in Maven/IDE or in Docker using Docker Compose.
For production, we would need to deploy it to, say, AWS Fargate with API Gateway or similar technology.
Depending on whether this will be an external-facing app, the requirements will be much stricter.

Parameter validation/Business logic separation

Currently the business logic is trivial. We moved it to the `business` package just for demo, but we would need to 
think about how to best structure it depending on the tasks and needs.
For parameter validation: as a rule of thumb, we should be strict with the output we produce and permissive with 
the input we get from the outside (as long as we can make sense of it).

Secrets management: currently we have a `.env` file, but in a real app we would need to use something like 
HashiCorp Vault or AWS Secrets Manager to inject secrets into our apps as env vars (or similar).

Component and live dependency testing: we would need to add more tests for this system to make sure it plays well with 
other systems around it. We can have a QA team or try to automate it.

AI usage: I was using Kimi K2.7-coder. It works pretty well once it has good examples, but it does not know about Spring Boot 4
(it confuses it with version 3).
`AGENTS.md` outlines the recommended approach.
