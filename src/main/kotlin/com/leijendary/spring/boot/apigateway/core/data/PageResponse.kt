package com.leijendary.spring.boot.apigateway.core.data

data class PageResponse<T>(
    var content: List<T>,
    var number: Int,
    var size: Int,
    var totalPages: Int,
    var numberOfElements: Int,
    var totalElements: Long,
    var previous: Boolean,
    var first: Boolean,
    var next: Boolean,
    var last: Boolean
)