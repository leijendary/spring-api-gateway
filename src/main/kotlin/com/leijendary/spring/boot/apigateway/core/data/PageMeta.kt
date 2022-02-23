package com.leijendary.spring.boot.apigateway.core.data

class PageMeta(page: PageResponse<*>) {

    var numberOfElements: Int = page.numberOfElements
    var totalPages: Int = page.totalPages
    var totalElements: Long = page.totalElements
    var size: Int = page.size
    var number: Int = page.number
}