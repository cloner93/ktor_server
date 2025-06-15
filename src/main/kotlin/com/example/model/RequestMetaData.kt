package com.example.com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestMetaData (
    val initialVec: String,
    val imei: String,
    val osType: Short ,
    val osVersion: Short ,
    val deviceName: String
)