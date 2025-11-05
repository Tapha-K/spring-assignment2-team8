CREATE TABLE IF NOT EXISTS lecture_time
(
    id              BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    lecture_id      BIGINT       NOT NULL,
    start_time      INT          NOT NULL,
    end_time        INT          NOT NULL,
    day_of_week     VARCHAR(255) NOT NULL,
    location        VARCHAR(255),
    lecture_type    VARCHAR(255),
    CONSTRAINT lecture_time__fk__lecture_id
        FOREIGN KEY (lecture_id) REFERENCES lectures (id)
        ON DELETE CASCADE
);