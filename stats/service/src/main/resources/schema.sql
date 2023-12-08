CREATE TABLE IF NOT EXISTS statentries(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app VARCHAR(50),
    uri VARCHAR(80),
    ip VARCHAR(32),
    query_date TIMESTAMP without time zone
);