CREATE TABLE IF NOT EXISTS ingredients
(
    id    BIGSERIAL PRIMARY KEY ,
    name  VARCHAR(200) NOT NULL ,
    property VARCHAR(254) NOT NULL
);

CREATE TABLE task (
    id BIGINT PRIMARY KEY,
    description VARCHAR(255),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES usr(id)
);