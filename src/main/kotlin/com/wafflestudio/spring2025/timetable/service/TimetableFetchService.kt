package com.wafflestudio.spring2025.timetable.service

import com.wafflestudio.spring2025.timetable.TimetableFetchException
import com.wafflestudio.spring2025.timetable.enum.Semester
import com.wafflestudio.spring2025.timetable.model.Lecture
import com.wafflestudio.spring2025.timetable.model.LectureTime
import com.wafflestudio.spring2025.timetable.repository.LectureRepository
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayInputStream

@Component
class TimetableFetchService(
    private val lectureRepository: LectureRepository,
) {
    companion object {
        private const val TIMETABLE_SNU_BASEURL = "https://sugang.snu.ac.kr"
        private const val TIMETABLE_SNU_REFERER = "https://sugang.snu.ac.kr/sugang/cc/cc100InterfaceSrch.action"
        private const val DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0"
        private const val TIMETABLE_SNU_EXCEL_URI = "/sugang/cc/cc100InterfaceExcel.action"
    }

    private val webClient =
        WebClient
            .builder()
            .baseUrl(TIMETABLE_SNU_BASEURL)
            .defaultHeader(HttpHeaders.REFERER, TIMETABLE_SNU_REFERER)
            .defaultHeader(HttpHeaders.USER_AGENT, DEFAULT_USER_AGENT)
            .build()

    private fun parseLectureXls(
        year: Int,
        semester: Semester,
        bytes: ByteArray,
    ): ArrayList<Lecture> {
        val result = ArrayList<Lecture>()

        ByteArrayInputStream(bytes).use { input ->
            val workbook = HSSFWorkbook(input)
            val sheet = workbook.getSheetAt(0)

            for (rowIndex in 3 until sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue
                val lecture =
                    Lecture(
                        year = year,
                        semester = semester.value,
                        classification = row.getCell(0).toString(),
                        college = row.getCell(1).toString(),
                        department = row.getCell(2).toString(),
                        academicCourse = row.getCell(3).toString(),
                        academicYear = row.getCell(4).toString(),
                        courseNumber = row.getCell(5).toString(),
                        lectureNumber = row.getCell(6).toString(),
                        courseTitle = row.getCell(7).toString(),
                        courseSubtitle = row.getCell(8).toString(),
                        credit = row.getCell(9).toString().toInt(),
                        classTimeText = row.getCell(12).toString(),
                        classTypeText = row.getCell(13).toString(),
                        location = row.getCell(14).toString(),
                        instructor = row.getCell(15).toString(),
                        remark = row.getCell(21).toString(),
                    )
                result.add(lecture)
            }
            workbook.close()
        }

        return result
    }

    private fun getSemesterCode(semester: Semester): String =
        when (semester) {
            Semester.SPRING -> "U000200001U000300001"
            Semester.SUMMER -> "U000200001U000300002"
            Semester.AUTUMN -> "U000200002U000300001"
            Semester.WINTER -> "U000200002U000300002"
        }

    private fun getLectureTimes(lecture: Lecture) {
        if (lecture.classTimeText.isEmpty()) {
            return
        }

        val result: MutableSet<LectureTime> = HashSet()
        val classTimes = lecture.classTimeText.split("/")
        val classTypes = lecture.classTypeText.split("/")
        val classLocations = lecture.location.split("/")

        if (classTimes.size != classTypes.size || classTimes.size != classLocations.size) {
            throw TimetableFetchException()
        }

        for (i in classTimes.indices) {
            result.add(
                LectureTime(
                    dayOfWeek = classTimes[i].substring(0, 1),
                    startTime = classTimes[i].substring(2, 4).toInt() * 60 + classTimes[i].substring(5, 7).toInt(),
                    endTime = classTimes[i].substring(8, 10).toInt() * 60 + classTimes[i].substring(11, 13).toInt(),
                    lectureType = classTypes[i],
                    location = classLocations[i],
                ),
            )
        }

        lecture.lectureTimes = result
    }

    fun fetchLectures(
        year: Int,
        semester: Semester,
    ) {
        val variableList: List<String> =
            listOf(
                "workType",
                "pageNo",
                "srchOpenSchyy",
                "srchOpenShtm",
                "srchSbjtNm",
                "srchSbjtCd",
                "seeMore",
                "srchCptnCorsFg",
                "srchOpenShyr",
                "srchOpenUpSbjtFldCd",
                "srchOpenSbjtFldCd",
                "srchOpenUpDeptCd",
                "srchOpenDeptCd",
                "srchOpenMjCd",
                "srchOpenSubmattCorsFg",
                "srchOpenSubmattFgCd1",
                "srchOpenSubmattFgCd2",
                "srchOpenSubmattFgCd3",
                "srchOpenSubmattFgCd4",
                "srchOpenSubmattFgCd5",
                "srchOpenSubmattFgCd6",
                "srchOpenSubmattFgCd7",
                "srchOpenSubmattFgCd8",
                "srchOpenSubmattFgCd9",
                "srchExcept",
                "srchOpenPntMin",
                "srchOpenPntMax",
                "srchCamp",
                "srchBdNo",
                "srchProfNm",
                "srchOpenSbjtTmNm",
                "srchOpenSbjtDayNm",
                "srchOpenSbjtTm",
                "srchOpenSbjtNm",
                "srchTlsnAplyCapaCntMin",
                "srchTlsnAplyCapaCntMax",
                "srchLsnProgType",
                "srchTlsnRcntMin",
                "srchTlsnRcntMax",
                "srchMrksGvMthd",
                "srchIsEngSbjt",
                "srchMrksApprMthdChgPosbYn",
                "srchIsPendingCourse",
                "srchGenrlRemoteLtYn",
                "srchLanguage",
                "srchCurrPage",
                "srchPageSize",
            )

        val formData =
            LinkedMultiValueMap<String, String>().apply {
                variableList.forEach { add(it, "") }
                setAll(
                    mapOf(
                        "workType" to "EX",
                        "pageNo" to "1",
                        "srchOpenSchyy" to year.toString(),
                        "srchOpenShtm" to getSemesterCode(semester),
                        "srchLanguage" to "ko",
                        "srchCurrPage" to "1",
                        "srchPageSize" to "9999",
                    ),
                )
            }

        val flux =
            webClient
                .post()
                .uri(TIMETABLE_SNU_EXCEL_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToFlux(DataBuffer::class.java)
                .map { buffer ->
                    val bytes = ByteArray(buffer.readableByteCount())
                    buffer.read(bytes)
                    bytes
                }

        val fullBytes =
            flux
                .reduce(ByteArray(0)) { acc, chunk ->
                    acc + chunk
                }.block()

        val oldLectures =
            lectureRepository.findAllByYearAndSemester(year, semester.value).associate { lecture ->
                (lecture.courseNumber + "##" + lecture.lectureNumber) to lecture.id
            }

        val newLectureList = parseLectureXls(year, semester, fullBytes!!)

        // 있던 강의가 없어질 수도 있나..?
        newLectureList.forEach { lecture ->
            getLectureTimes(lecture)

            val oldLectureId = oldLectures.get(lecture.courseNumber + "##" + lecture.lectureNumber)
            if (oldLectureId != null) {
                lecture.id = oldLectureId
            }
        }

        lectureRepository.saveAll(newLectureList)
    }
}
