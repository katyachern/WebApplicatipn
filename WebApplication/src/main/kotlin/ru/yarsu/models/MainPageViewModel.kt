package ru.yarsu.models

import org.http4k.template.ViewModel

data class MainPageViewModel(
        val description: String,
) : ViewModel