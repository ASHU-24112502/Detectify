package com.example.detectify.model



data class VisionResponse(
    val responses: List<VisionSingleResponse>?
)

data class VisionSingleResponse(
    val labelAnnotations: List<VisionLabel>?
)

data class VisionLabel(
    val mid: String?,
    val description: String?,
    val score: Double?
)
