package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.Lecture
import org.springframework.data.repository.ListCrudRepository

interface LectureRepository : ListCrudRepository<Lecture, Long>