Start env in docker:
```sh
sbt stage && docker-compose up --build
```

or run on localhost:
```sh
docker run --rm -d -p 9200:9200 --name elasticsearch docker.elastic.co/elasticsearch/elasticsearch-oss:6.2.1 && \
  docker run --rm -d -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres postgres && \
  sbt stage && \
  target/universal/stage/bin/rdb-to-elastic
```
