package com.wafflestudio.spring2025.timetable

import com.wafflestudio.spring2025.DomainException
import com.wafflestudio.spring2025.post.PostException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class TimetableException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class TimetableFetchException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
        msg = "Timetable Fetch Failed",
    )

class TimetableBlankTitleException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "Title must not be blank",
    )

class TimetableDuplicateTitleException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "Timetable title already exists.",
    )

class TimetableNotFoundException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Timetable not found",
    )

class TimetableUpdateForbiddenException :
    TimetableException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to update this timetable",
    )