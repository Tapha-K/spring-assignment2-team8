package com.wafflestudio.spring2025.timetable.service

import com.wafflestudio.spring2025.timetable.dto.LectureSearchRequest
import com.wafflestudio.spring2025.timetable.dto.core.LectureDto
import com.wafflestudio.spring2025.timetable.repository.LectureRepository
import org.springframework.stereotype.Service

@Service
class LectureService(
    private val lectureRepository: LectureRepository,
) {
    fun searchLectures(request: LectureSearchRequest): List<LectureDto> {

        val allLectures = lectureRepository.findAllByYearAndSemester(
            request.year,
            request.semester
        )

        val filtered = if (request.keyword.isBlank()) {
            allLectures
        } else {
            allLectures.filter {
                it.courseTitle.contains(request.keyword, ignoreCase = true) ||
                        it.instructor.contains(request.keyword, ignoreCase = true)
            }
        }

        val fromIndex = request.page * request.size
        val toIndex = minOf(fromIndex + request.size, filtered.size)
        if (fromIndex >= filtered.size) return emptyList()

        val paged = filtered.subList(fromIndex, toIndex)

        return paged.map { LectureDto(it) }
    }
}