FROM debian:stretch as builder
MAINTAINER jarroyes

# Environment vars
ENV GRADLE_VERSION 4.9
ENV JAVA_VERSION 8

ENV GRADLE_HOME=/opt/gradle/gradle-$GRADLE_VERSION
ENV PATH=$PATH:$GRADLE_HOME/bin

# Basic Update
RUN apt-get update && apt-get upgrade -y

# Install wget and unzip
RUN apt-get install wget unzip -y

# Install JRE
RUN apt-get install openjdk-${JAVA_VERSION}-jdk -y

# Install Gradle from WEB
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /tmp

RUN mkdir /opt/gradle \
    && unzip -d /opt/gradle /tmp/gradle-${GRADLE_VERSION}-bin.zip \
    && export PATH=$PATH:/opt/gradle/gradle-${GRADLE_VERSION}/bin

COPY . /appsrc
WORKDIR /appsrc
RUN ls -la
RUN gradle build

FROM debian:stretch
RUN apt-get update && apt-get upgrade -y && apt install openjdk-8-jre -y
EXPOSE 8080
COPY --from=builder /appsrc/release/api-space-invaders-1.0.jar /app/api-space-invaders-1.0.jar
WORKDIR /app
CMD ["java", "-jar", "api-space-invaders-1.0.jar"]
