package com.hengda.frame.httpx.library.parser

import okhttp3.HttpUrl

class DefaultUrlParser : UrlParser {
    override fun parseUrl(domainUrl: HttpUrl, url: HttpUrl): HttpUrl {
        if (null == domainUrl) return url
        return url.newBuilder()
            .scheme(domainUrl.scheme())
            .host(domainUrl.host())
            .port(domainUrl.port())
            .build()
    }
}