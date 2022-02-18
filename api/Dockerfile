ARG BUILD_HOME=/build-dir

FROM gradle:jdk11 as build-image

ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

COPY --chown=gradle:gradle build.gradle settings.gradle $APP_HOME/
COPY --chown=gradle:gradle src $APP_HOME/src

RUN gradle --no-daemon build

FROM alpine:3.14
RUN apk update && apk upgrade && apk add --no-cache openjdk11
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
COPY --from=build-image $APP_HOME/build/libs/api-0.0.2.jar app.jar

ENTRYPOINT java -jar app.jar