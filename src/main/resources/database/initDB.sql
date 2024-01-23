CREATE TABLE IF NOT EXISTS ingredients
(
    id    BIGSERIAL PRIMARY KEY ,
    name  VARCHAR(200) NOT NULL ,
    property VARCHAR(254) NOT NULL
);

CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255),
    user_id BIGINT,
    status VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES usr(id)
);