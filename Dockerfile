# docker build -t rdbtoelasticstreaming_web-app .

FROM dmytroorlov/jdk

WORKDIR /root

ADD target/universal/stage /root
ADD production.conf /root/production.conf

EXPOSE 9000

CMD ["/root/bin/rdb-to-elastic", "-Dconfig.file=/root/production.conf"]
