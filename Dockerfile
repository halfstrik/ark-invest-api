FROM amazoncorretto:21-alpine as final
RUN adduser -S user
WORKDIR /app

COPY target/ark*.jar /app

# Run under non-privileged user with minimal write permissions
USER user

ENV JAVA_TOOL_OPTIONS="-Xss256K -XX:MaxRAMPercentage=60 -XX:+ExitOnOutOfMemoryError"
CMD java -jar *.jar

ENV PORT=8080
EXPOSE $PORT
