package com.megix

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import okhttp3.FormBody

class PixelDrain : ExtractorApi() {
    override val name            = "PixelDrain"
    override val mainUrl         = "https://pixeldrain.com"
    override val requiresReferer = true

    override suspend fun getUrl(url: String, referer: String?, subtitleCallback: (SubtitleFile) -> Unit, callback: (ExtractorLink) -> Unit) {
        val mId = Regex("/u/(.*)").find(url)?.groupValues?.get(1)
        if (mId.isNullOrEmpty())
        {
            callback.invoke(
                ExtractorLink(
                    this.name,
                    this.name,
                    url,
                    url,
                    Qualities.Unknown.value,
                )
            )
        }
        else {
            callback.invoke(
                ExtractorLink(
                    this.name,
                    this.name,
                    "$mainUrl/api/file/${mId}?download",
                    url,
                    Qualities.Unknown.value,
                )
            )
        }
    }
}


class VCloud : ExtractorApi() {
    override val name: String = "V-Cloud"
    override val mainUrl: String = "https://vcloud.lol"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        var href=url
        if (href.contains("api/index.php"))
        {
            href=app.get(url).document.selectFirst("div.main h4 a")?.attr("href") ?:""
        }
        val doc = app.get(href).document
        val scriptTag = doc.selectFirst("script:containsData(url)")?.toString() ?:""
        val urlValue = Regex("var url = '([^']*)'").find(scriptTag) ?. groupValues ?. get(1) ?: ""
        if (urlValue.isNotEmpty()) {
            val document = app.get(urlValue).document
            val size = document.selectFirst("i#size")?.text() ?: ""
            val div = document.selectFirst("div.card-body")
            val header = document.selectFirst("div.card-header")?.text() ?: ""
            div?.select("h2 a.btn")?.apmap {
                val link = it.attr("href")

                if (link.contains("technorozen.workers.dev"))
                {
                    val iframe = getGBurl(link)
                    callback.invoke(
                        ExtractorLink(
                            "$name[Download]",
                            "$name[Download] $size",
                            iframe,
                            "",
                            getIndexQuality(header),
                        )
                    )
                }
                else if (link.contains("pixeldra.in")) {
                    callback.invoke(
                        ExtractorLink(
                            "Pixeldrain",
                            "Pixeldrain $size",
                            link,
                            "",
                            getIndexQuality(header),
                        )
                    )
                }
                else if (link.contains(".dev")) {
                    callback.invoke(
                        ExtractorLink(
                            name,
                            "$name $size",
                            link,
                            "",
                            getIndexQuality(header),
                        )
                    )
                }
                else if (link.contains("fastdl.lol"))
                {
                    callback.invoke(
                        ExtractorLink(
                            "$name[Download]",
                            "$name[Download] $size",
                            link,
                            "",
                            getIndexQuality(header),
                        )
                    )
                }
                else if (link.contains("hubcdn.xyz"))
                {
                    callback.invoke(
                        ExtractorLink(
                            name,
                            "$name $size",
                            link,
                            "",
                            getIndexQuality(header),
                        )
                    )
                }
                else if (link.contains("gofile.io"))
                {
                    loadExtractor(link,"",subtitleCallback, callback)
                }
                else if (link.contains("pixeldrain"))
                {
                    loadExtractor(link,"",subtitleCallback, callback)
                }
                else
                {
                    //Nothing
                }
            }
        }
    }


    private fun getIndexQuality(str: String?): Int {
        return Regex("(\\d{3,4})[pP]").find(str ?: "")?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: Qualities.Unknown.value
    }

    private suspend fun getGBurl(url: String): String {
        return app.get(url).document.selectFirst("#vd")?.attr("href") ?:""
    }

}

class HubCloudClub : HubCloud() {
    override val mainUrl: String = "https://hubcloud.club"
}

class HubCloudlol : HubCloud() {
    override var mainUrl = "https://hubcloud.lol"
}

open class HubCloud : ExtractorApi() {
    override val name: String = "Hub-Cloud"
    override val mainUrl: String = "https://hubcloud.art"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val text = app.get(url).text
        val newLink = text.substringAfter("url=").substringBefore("\"")
        val newDoc = app.get(newLink).document
        var gamerLink : String

        if(newLink.contains("drive")) {
            val scriptTag = newDoc.selectFirst("script:containsData(url)")?.toString() ?: ""
            gamerLink = Regex("var url = '([^']*)'").find(scriptTag) ?. groupValues ?. get(1) ?: ""
        }

        else {
            gamerLink = newDoc.selectFirst("div.vd > center > a") ?. attr("href") ?: ""
        }

        val document = app.get(gamerLink).document
        val size = document.selectFirst("i#size") ?. text() ?: ""
        val div = document.selectFirst("div.card-body")
        val header = document.selectFirst("div.card-header") ?. text() ?: ""
        div?.select("h2 a.btn")?.apmap {
            val link = it.attr("href")

            if (link.contains("technorozen.workers.dev"))
            {
                val iframe = getGBurl(link)
                callback.invoke(
                    ExtractorLink(
                        "$name[Download]",
                        "$name[Download] $size",
                        iframe,
                        "",
                        getIndexQuality(header),
                    )
                )
            }
            else if (link.contains("pixeldra.in")) {
                callback.invoke(
                    ExtractorLink(
                        "Pixeldrain",
                        "Pixeldrain $size",
                        link,
                        "",
                        getIndexQuality(header),
                    )
                )
            }
            else if (link.contains(".dev")) {
                callback.invoke(
                    ExtractorLink(
                        name,
                        "$name $size",
                        link,
                        "",
                        getIndexQuality(header),
                    )
                )
            }
            else if (link.contains("fastdl.lol"))
            {
                callback.invoke(
                    ExtractorLink(
                        "$name[Download]",
                        "$name[Download] $size",
                        link,
                        "",
                        getIndexQuality(header),
                    )
                )
            }
            else if (link.contains("hubcdn.xyz"))
            {
                callback.invoke(
                    ExtractorLink(
                        name,
                        "$name $size",
                        link,
                        "",
                        getIndexQuality(header),
                    )
                )
            }
            else if (link.contains("gofile.io"))
            {
                loadExtractor(link,"",subtitleCallback, callback)
            }
            else if (link.contains("pixeldrain"))
            {
                loadExtractor(link,"",subtitleCallback, callback)
            }
            else
            {
                //Nothing
            }
        }
    }

    private fun getIndexQuality(str: String?): Int {
        return Regex("(\\d{3,4})[pP]").find(str ?: "") ?. groupValues ?. getOrNull(1) ?. toIntOrNull()
            ?: Qualities.Unknown.value
    }

    private suspend fun getGBurl(url: String): String {
        return app.get(url).document.selectFirst("#vd")?.attr("href") ?:""
    }

}

class fastdlserver : GDFlix() {
    override var mainUrl = "https://fastdlserver.online"
}

class GDFlix1 : GDFlix() {
    override val mainUrl: String = "https://new3.gdflix.cfd"
}

class GDFlix2 : GDFlix() {
    override val mainUrl: String = "https://new2.gdflix.cfd"
}

open class GDFlix : ExtractorApi() {
    override val name: String = "GDFlix"
    override val mainUrl: String = "https://new4.gdflix.cfd"
    override val requiresReferer = false

    private suspend fun extractbollytag(url:String): String {
        val tagdoc= app.get(url).text
        val tags ="""\b\d{3,4}p\b""".toRegex().find(tagdoc) ?. value ?. trim() ?:""
        return tags
    }

    private suspend fun extractbollytag2(url:String): String {
        val tagdoc= app.get(url).text
        val tags ="""\b\d{3,4}p\b\s(.*?)\[""".toRegex().find(tagdoc) ?. groupValues ?. get(1) ?. trim() ?:""
        return tags
    }

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        var originalUrl = url
        val tags = extractbollytag(originalUrl)
        val tagquality = extractbollytag2(originalUrl)

        if (originalUrl.startsWith("$mainUrl/goto/token/")) {
            val partialurl = app.get(originalUrl).text.substringAfter("replace(\"").substringBefore("\")")
            originalUrl = mainUrl + partialurl
        }
        app.get(originalUrl).document.select("div.text-center a").map {
            if (it.select("a").text().contains("FAST CLOUD DL"))
            {
                val link=it.attr("href")
                val trueurl=app.get("$mainUrl$link", timeout = 30L).document.selectFirst("a.btn-success")?.attr("href") ?:""
                callback.invoke(
                    ExtractorLink(
                        "GDFlix[Fast Cloud]",
                        "GDFLix[Fast Cloud] $tagquality",
                        trueurl,
                        "",
                        getQualityFromName(tags)
                    )
                )
            }
            else if (it.select("a").text().contains("DRIVEBOT LINK"))
            {
                val driveLink = it.attr("href")
                val id = driveLink.substringAfter("id=").substringBefore("&")
                val doId = driveLink.substringAfter("do=").substringBefore("==")
                val indexbotlink = "https://indexbot.lol/download?id=${id}&do=${doId}"
                val indexbotresponse = app.get(indexbotlink, timeout = 30L)
                if(indexbotresponse.isSuccessful) {
                    val cookiesSSID = indexbotresponse.cookies["PHPSESSID"]
                    val indexbotDoc = indexbotresponse.document
                    val token = Regex("""formData\.append\('token', '([a-f0-9]+)'\)""").find(indexbotDoc.toString()) ?. groupValues ?. get(1) ?: ""
                    val postId = Regex("""fetch\('/download\?id=([a-zA-Z0-9/+]+)'""").find(indexbotDoc.toString()) ?. groupValues ?. get(1) ?: ""

                    val requestBody = FormBody.Builder()
                        .add("token", token)
                        .build()

                    val headers = mapOf(
                        "Referer" to indexbotlink
                    )

                    val cookies = mapOf(
                        "PHPSESSID" to "$cookiesSSID",
                    )

                    val response = app.post(
                        "https://indexbot.lol/download?id=${postId}",
                        requestBody = requestBody,
                        headers = headers,
                        cookies = cookies,
                        timeout = 30L
                    ).toString()

                    var downloadlink = Regex("url\":\"(.*?)\"").find(response) ?. groupValues ?. get(1) ?: ""

                    downloadlink = downloadlink.replace("\\", "")

                    callback.invoke(
                        ExtractorLink(
                            "GDFlix[IndexBot](VLC)",
                            "GDFlix[IndexBot](VLC) $tagquality",
                            downloadlink,
                            "https://indexbot.lol/",
                            getQualityFromName(tags)
                        )
                    )
                }
            }
            else if (it.select("a").text().contains("Instant DL"))
            {
                val instantLink = it.attr("href")
                val link = app.get(instantLink, timeout = 30L, allowRedirects = false).headers["Location"]?.split("url=") ?. getOrNull(1) ?: ""
                callback.invoke(
                    ExtractorLink(
                        "GDFlix[Instant Download]",
                        "GDFlix[Instant Download] $tagquality",
                        link,
                        "",
                        getQualityFromName(tags)
                    )
                )
            }
        }
    }
}

