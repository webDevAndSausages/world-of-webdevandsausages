FROM gradle:5.1.0-jdk8
USER root
WORKDIR /home/gradle/project
ENTRYPOINT ["./gradlew", "execute", "-x", "jooq-codegen-meta"]

