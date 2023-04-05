package ru.yarsu

import org.http4k.core.*
import ru.yarsu.models.PebbleViewModel
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.BiDiBodyLens
import org.http4k.lens.LensFailure
import org.http4k.routing.*
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.http4k.template.PebbleTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import ru.yarsu.domain.ScheduleInfo
import ru.yarsu.domain.ScheduleItem
import ru.yarsu.domain.formSchedule
import ru.yarsu.filters.showErrorMessageFilter
import ru.yarsu.models.CourseViewModel
import ru.yarsu.models.CoursesListViewModel
import ru.yarsu.models.MainPageViewModel

private fun showPebbleTemplate(bodyLens: BiDiBodyLens<ViewModel>,): HttpHandler = {
    val viewModel = PebbleViewModel("Hello there!")
    Response(OK).with(bodyLens of viewModel)
}

private fun showMainPage(bodyLens: BiDiBodyLens<ViewModel>,): HttpHandler = {
    val viewModel = MainPageViewModel("Main Page")
    Response(OK).with(bodyLens of viewModel)
}

private fun showListCourses(bodyLens: BiDiBodyLens<ViewModel>, scheduleInfo: ScheduleInfo): HttpHandler = {
    val viewModel = CoursesListViewModel("Courses list", scheduleInfo.courses.sortedBy { it.courseName })
    Response(OK).with(bodyLens of viewModel)
}

private fun showCourse(bodyLens: BiDiBodyLens<ViewModel>, scheduleInfo: ScheduleInfo): HttpHandler = handler@{request ->
    val idString = request.path("id").orEmpty()
    try {
        val course = scheduleInfo.courses.first { it.id == idString }
        val viewModel = CourseViewModel("Course", course, scheduleInfo.scheduleItems.sortedBy { it.startTime })
        Response(OK).with(bodyLens of viewModel)
    } catch (ex: NoSuchElementException) {
        ex.message
        return@handler Response(Status.BAD_REQUEST)
    }
}

private fun respondWithPong(): HttpHandler = {
    Response(OK).body("pong")
}

fun router(
    scheduleInfo: ScheduleInfo,
    bodyLens: BiDiBodyLens<ViewModel>,
): HttpHandler = routes(
    "/ping" bind GET to respondWithPong(),
    "/" bind GET to showMainPage(bodyLens),
    "/templates/pebble" bind GET to showPebbleTemplate(bodyLens),
    "/courses/" bind GET to showListCourses(bodyLens, scheduleInfo),
    "/courses/{id}" bind GET to showCourse(bodyLens, scheduleInfo),
    static(ResourceLoader.Classpath("/ru/yarsu/public/")),
)

fun main() {
    val scheduleInfo = formSchedule()
    val renderer = Body.viewModel(
        PebbleTemplates().HotReload("src/main/resources"),
        ContentType.TEXT_HTML,
    ).toLens()
    val router = router(scheduleInfo, renderer)

    val app: HttpHandler = Filter.NoOp
            .then(showErrorMessageFilter(PebbleTemplates().HotReload("src/main/resources")).then(router))
    val server = app.asServer(Netty(9000)).start()
    println("Server started on http://localhost:" + server.port())
}
