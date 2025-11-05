ALTER TABLE lectures
    ADD CONSTRAINT lecture__uk__course_number_lecture_number UNIQUE (year, semester, course_number, lecture_number);