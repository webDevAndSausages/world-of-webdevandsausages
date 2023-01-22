FROM gradle:5.6.4-jdk8 AS build
USER root
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -i -x jooq-codegen-meta

FROM adoptopenjdk/openjdk8-openj9:jdk8u192-b12-alpine-slim
ARG PUBLIC_WDS_API_KEY
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_KEY
ENV PUBLIC_WDS_API_KEY=$PUBLIC_WDS_API_KEY
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
ENV AWS_SECRET_KEY=$AWS_SECRET_KEY
EXPOSE 5000
WORKDIR /app
COPY --from=build /home/gradle/project/build/distributions/events.tar .
RUN tar -xf ./events.tar
RUN rm ./events.tar
ENTRYPOINT ["./events/bin/events"]
