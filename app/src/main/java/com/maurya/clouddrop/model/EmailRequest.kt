package com.maurya.clouddrop.model

data class EmailRequest(
    val emailFrom: String,
    val emailTo: String,
    val uuid: String
)