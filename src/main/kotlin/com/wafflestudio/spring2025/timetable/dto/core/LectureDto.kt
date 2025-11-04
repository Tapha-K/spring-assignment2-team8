package com.wafflestudio.spring2025.timetable.dto.core

import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.model.Lecture
import com.wafflestudio.spring2025.timetable.model.LectureTime

data class LectureDto (
    var id: Long? = null,
    var year: Int,
    var semester: Semester,
    var classification: String,
    var college: String,
    var department: String,
    var academicCourse: String,
    var academicYear: String,
    var courseNumber: String,
    var lectureNumber: String,
    var courseTitle: String,
    var courseSubtitle: String,
    var credit: Int,
    var lectureTimes: Set<LectureTimeDto>,
    var instructor: String,
    var remark: String,

) {
    constructor(lecture: Lecture): this(
        id = lecture.id,
        year = lecture.year,
        semester = Semester.fromValue(lecture.semester),
        classification = lecture.classification,
        college = lecture.college,
        department = lecture.department,
        academicCourse = lecture.academicCourse,
        academicYear = lecture.academicYear,
        courseNumber = lecture.courseNumber,
        lectureNumber = lecture.lectureNumber,
        courseTitle = lecture.courseTitle,
        courseSubtitle = lecture.courseSubtitle,
        credit = lecture.credit,
        lectureTimes = lecture.lectureTimes.map { LectureTimeDto(it) }.toSet(),
        instructor = lecture.instructor,
        remark = lecture.remark,
    )
}

data class LectureTimeDto (
    var dayOfWeek: String,
    var startTime: Int,
    var endTime: Int,
    var lectureType: String,
    var location: String,
) {
    constructor(lectureTime: LectureTime): this(
        dayOfWeek = lectureTime.dayOfWeek,
        startTime = lectureTime.startTime,
        endTime = lectureTime.endTime,
        lectureType = lectureTime.lectureType,
        location = lectureTime.location,
    )
}