# Ark Invest API - take home task

The goal is to create simple but production ready CRUD API using Spring Boot and Java

To run server using Maven:
```shell
./mvnw spring-boot:run
```

To run whole application with DB locally using Docker:
```shell
docker compose up -d # TODO
```

## Design decisions and considerations

On whether to use JDBC vs JPA
We are choosing JDBC here for somplicity and flexability,
if we need to use a fancy index or join, it should be easier to implement.
