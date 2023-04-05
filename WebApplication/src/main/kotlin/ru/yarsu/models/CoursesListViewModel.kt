package ru.yarsu.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.Course

data class CoursesListViewModel(
        val description: String,
        val courses: List<Course>
): ViewModel
