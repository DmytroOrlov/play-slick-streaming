# --- !Ups

CREATE TABLE stream_data (
  id INTEGER PRIMARY KEY
);

INSERT INTO stream_data (id) SELECT id FROM generate_series(0, 1000000) AS id;
# --- !Downs

DROP TABLE IF EXISTS stream_data;
