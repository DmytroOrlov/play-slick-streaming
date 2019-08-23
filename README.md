### HTTP Streaming of _json_ from `postgres` (with _compile-time_ SQL checking)

Run on localhost:
```sh
$ docker run --rm -d -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres postgres && \
  sbt 'runMain ApplyEvolutions' && \
  sbt server/stage && \
  server/target/universal/stage/bin/server
```

Run with docker-compose:
```sh
$ sbt server/stage && \
  docker-compose up --build
```
