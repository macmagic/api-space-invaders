FROM debian:stretch
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

EXPOSE 8080

#Add entrypoint
ADD ./docker/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod 777 /usr/local/bin/entrypoint.sh
ENTRYPOINT [ "/usr/local/bin/entrypoint.sh" ]
