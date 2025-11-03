CREATE TABLE IF NOT EXISTS timetables
(
    id              BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    `year`          INT          NOT NULL,
    semester        INT          NOT NULL,
    title           VARCHAR(255) NOT NULL,
    CONSTRAINT timetables__fk__user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
);