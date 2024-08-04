package com.example.hackathon_guru

data class Schedule(
    var title: String,
    var date: String,
    var location: String = "",
    var comment: String = "",
    var isExpanded: Boolean = false
)
