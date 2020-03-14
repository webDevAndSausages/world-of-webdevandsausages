# Multi-stage build instructions: https://docs.docker.com/develop/develop-images/multistage-build/
# Use dev.Dockerfile when in development or CI mode
FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine AS build
ENV APP_HOME=/opt/app/
WORKDIR $APP_HOME

# Get gradle distribution
COPY gradlew $APP_HOME
COPY gradle/ $APP_HOME/gradle
RUN ./gradlew --version

# Copy source and build fat jar
COPY . $APP_HOME/
RUN ./gradlew --no-daemon build -x jooq-codegen-meta

#
# Create minimal image
#
FROM adoptopenjdk/openjdk11:jdk-11.0.6_10-alpine
ENV APP_HOME=/opt/app/
WORKDIR $APP_HOME

# User
RUN addgroup -S wds && adduser -H -D -u 1000 -S wds -G wds
USER wds

COPY --from=build --chown=wds:wds $APP_HOME/build/libs/events-all.jar events.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar events.jar"]
EXPOSE 5000
