FROM openjdk:17
LABEL maintainer=xht


#docker run -e PARAMS="--server.port 9090"
ENV PARAMS="--server.port=8501 --spring.profiles.active=dev"
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY target/*.jar /app.jar
EXPOSE 8501

ENV JAVA_OPTS=""
ENTRYPOINT ["/bin/sh","-c","java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar app.jar ${PARAMS}"]