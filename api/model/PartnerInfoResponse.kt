package com.buisness.bonuscards.api.model

data class PartnerInfoResponse(
    val status: String,
    val result: List<PartnerContactInfo>,
    val request_count: String
)