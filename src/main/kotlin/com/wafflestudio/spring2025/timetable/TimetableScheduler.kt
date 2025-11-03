package com.wafflestudio.spring2025.timetable

import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.service.TimetableFetchService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class TimetableScheduler (
    private val timetableFetchService: TimetableFetchService
) {
    @Scheduled(fixedRate = 600000)
    fun runCrawl() {
        timetableFetchService.fetchLectures(2025, Semester.AUTUMN)
    }
}