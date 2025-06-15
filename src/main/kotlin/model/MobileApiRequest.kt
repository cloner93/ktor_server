package com.example.model

import com.example.com.example.model.RequestMetaData
import kotlinx.serialization.Serializable

@Serializable
data class MobileApiRequest<T>(
    val metaData: RequestMetaData,
    val data: T
)