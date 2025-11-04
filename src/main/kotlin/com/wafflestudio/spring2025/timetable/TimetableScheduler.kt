package com.wafflestudio.spring2025.timetable

import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.service.TimetableFetchService
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TimetableScheduler(
    private val timetableFetchService: TimetableFetchService,
) {
    @PostConstruct
    fun runOnStartup() {
        runCrawl()
    }

    @Scheduled(cron = "\${snusugang.refresh}")
    fun runCrawl() {
        for (semester in Semester.entries) {
            timetableFetchService.fetchLectures(2025, semester)
        }
    }
}
