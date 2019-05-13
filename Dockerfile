# docker build -t rdbtoelasticstreaming_web-app .

FROM openjdk:8

WORKDIR /root

COPY production.conf /root/production.conf
COPY server/target/universal/stage /root

EXPOSE 9000

CMD ["/root/bin/rdb-to-elastic", "-Dconfig.file=/root/production.conf", "-Dpidfile.path=/dev/null"]
