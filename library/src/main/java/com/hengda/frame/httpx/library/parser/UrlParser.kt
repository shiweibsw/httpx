package com.hengda.frame.httpx.library.parser

import okhttp3.HttpUrl

interface UrlParser {
    abstract fun parseUrl(domainUrl: HttpUrl, url: HttpUrl): HttpUrl
}