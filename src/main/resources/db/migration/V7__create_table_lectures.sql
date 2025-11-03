CREATE TABLE IF NOT EXISTS lectures
(
    id              BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    `year`          INT          NOT NULL,
    semester        INT          NOT NULL,
    classification  VARCHAR(255) NOT NULL,
    college         VARCHAR(255) NOT NULL,
    department      VARCHAR(255) NOT NULL,
    academic_course VARCHAR(255) NOT NULL,
    academic_year   VARCHAR(255),
    course_number   VARCHAR(255) NOT NULL,
    lecture_number  VARCHAR(255) NOT NULL,
    course_title    VARCHAR(255) NOT NULL,
    course_subtitle VARCHAR(255),
    credit          INT          NOT NULL,
    class_time_text VARCHAR(255) NOT NULL,
    location        VARCHAR(255),
    instructor      VARCHAR(255),
    remark          TEXT
);