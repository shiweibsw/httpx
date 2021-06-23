package com.hengda.frame.httpx.library.parser

import okhttp3.HttpUrl

interface UrlParser {
    fun parseUrl(domainUrl: HttpUrl, url: HttpUrl): HttpUrl
}