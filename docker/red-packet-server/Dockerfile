FROM openjdk:17
LABEL maintainer=xht

ENV PARAMS="--server.port=8888 --spring.profiles.active=prod"
RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY target/*.jar /redpacket-server.jar
EXPOSE 8888
ENTRYPOINT ["/bin/sh","-c","java -jar redpacket-server.jar ${PARAMS}"]