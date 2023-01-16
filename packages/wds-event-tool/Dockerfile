FROM gradle:5.6.4-jdk8 AS build
USER root
ARG FIREBASE_SERVICEACCOUNT_KEY
ARG SECRETS_YAML
RUN echo "$SSH_KEY" > /root/.ssh/id_rsa
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN echo "$FIREBASE_SERVICEACCOUNT_KEY" > ./src/main/resources/firebaseServiceAccountKey.json
RUN echo "$SECRETS_YAML" > ./src/main/resources/secrets.yaml

RUN gradle build -x jooq-codegen-meta -x test

FROM adoptopenjdk/openjdk8-openj9:jdk8u192-b12-alpine-slim
EXPOSE 5000
WORKDIR /app
COPY --from=build /home/gradle/project/build/distributions/events.tar .
RUN tar -xf ./events.tar
RUN rm ./events.tar
ENTRYPOINT ["./events/bin/events"]
