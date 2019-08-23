# docker build -t play-slick-streaming_web-app_1 .

FROM openjdk:8

WORKDIR /root

COPY production.conf /root/production.conf
COPY server/target/universal/stage /root

EXPOSE 9000

CMD ["/root/bin/server", "-Dconfig.file=/root/production.conf", "-Dpidfile.path=/dev/null"]
