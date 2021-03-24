FROM adoptopenjdk/openjdk11:jdk-11.0.7_10-debianslim-slim as build

ENV APP_HOME=/opt/app
WORKDIR $APP_HOME
COPY ./ .

USER root
RUN --mount=type=cache,id=gradle,sharing=locked,target=/root/.gradle chmod +x ./gradlew && ./gradlew --no-daemon build -x jooq-codegen-meta

FROM adoptopenjdk/openjdk11-openj9:jre-11.0.10_9_openj9-0.24.0-debianslim
ENV APP_HOME=/opt/app
WORKDIR $APP_HOME

# User
RUN addgroup --system wds && adduser --system --no-create-home --disabled-password --disabled-login --uid 1000 --ingroup wds wds
USER 1000

COPY --from=build --chown=1000:1000 /opt/app/build/libs/events-all.jar .
ENTRYPOINT ["sh", "-c", "java -cp events-all.jar org.webdevandsausages.events.AppKt"]