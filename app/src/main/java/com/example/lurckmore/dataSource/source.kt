package com.example.lurckmore.dataSource

import android.util.Log
import com.example.lurckmore.MainActivity
import io.reactivex.Observable
import org.jsoup.Jsoup
import java.io.IOException

const val url = "https://lurkmore.to"
private const val TAG = "tag"

data class P (
    val listOfHrefs: List<Pair<String, String>>?,
    val content: String = "",
    val type: String = "",
    var author: String = "",
    val imgUrl: String = "",
    val title: String = "",
    val id: String = ""
)
fun contentSource(path: String, android: Boolean) : Observable<P> = Observable.create {w ->
    Thread.sleep(2000)
    try {
        val marksDoc = Jsoup.connect(path)
            .userAgent("Chrome/4.0.249.0 Safari/532.5")
            .get()
        marksDoc.body().select("#mw-content-text")[0].children().forEach {

            when {

                it.tagName() == "table" && it.className() == "lm-plashka" -> {
                    if (android) Log.e(TAG, "in:obs:plashka")
                    val td = it.select("tbody > tr > td")
                    val imgUrl = url+td[0].select("a > img").attr("src")
                    val title = td[1].select("b").text()
                    val content = td[1].text()
                    val listOfHrefs = td[1].select("a")
                        .map {element ->  element.text() to url+element.attr("href") }
                    w.onNext(P(imgUrl = imgUrl, title = title, content = content, type = "plashka",listOfHrefs = listOfHrefs))
                }

                it.tagName() == "ul" -> {
                    it.children().forEach { e ->
                        if (android) Log.e("ul", "yeah ::li->[${
                        P(content = e.text(), listOfHrefs = e.select("a").map { element ->
                            element.text() to url+element.attr("href")
                        }, type = "li")
                        }]")
                        w.onNext(P(content = e.text(), listOfHrefs = e.select("a").map { element ->
                            element.text() to url+element.attr("href")
                        }, type = "li"))
                    }
                }
                it.tagName() == "p" -> {
                    if (android) Log.e(TAG, "is: p")
                    val listOfHrefs = it.select("a")
                        .map {e -> e.text() to url+e.attr("href") }
                    w.onNext(P(listOfHrefs = listOfHrefs, content = it.text(), type = "p"))
                }
                it.tagName() == "table" && it.className() == "tpl-quote-tiny" -> {
                  it.select("tbody > tr > td")[1]?.let {td ->
                      if (it.select("tbody > tr").size > 1) w.onNext(P(
                          listOfHrefs = td.select("p > a").map { e ->
                              e.attr("href") to e.text()
                          },
                          content = td.text(),
                          author = it.select("tbody > tr")[1].select("td").text(),
                          type = "quote-tiny"
                      )) else w.onNext(P(
                          content = td.text(),
                          listOfHrefs = td.select("a").map { e ->
                              e.attr("href") to e.text()
                          },
                          type = "no-name-quote"
                      ))
                  }
                }
                it.tagName() == "table" && it.className() == "tpl-quote" -> {
                    it.select("tbody > tr > td")[0]?.let { td ->
                        if (it.select("tbody > tr").size > 1) w.onNext(P(
                                content = td.text(),
                                listOfHrefs = td.select("p > a").map { e ->
                                    e.attr("href") to e.text()
                                },
                                author =  it.select("tbody > tr")[1].select("td").text(),
                                type = "quote"
                            ))
                        else w.onNext(P(
                            content = td.text(),
                            listOfHrefs = td.select("a").map { e ->
                                e.attr("href") to e.text()
                            },
                            type = "no-name-quote"
                        ))
                    }
                }
                /*
                 if (android) Log.e(TAG, "is: table quote2")

                        if (it.select("tbody"))

                        if (it.select("tbody > tr")[0].select("td").size > 1) {
                            val text =
                                it.select("tbody > tr")[0].select("td")[1].select("p.quote").text()
                            w.onNext(
                                P(content = if (text == "") {
                                    if (it.select("tbody > tr")[0].select("td").size > 1)
                                        it.select("tbody > tr")[0].select("td")[1].select("p.prequote")
                                            .text()
                                    else text
                                } else text,
                                    type = "quote-tiny",
                                    listOfHrefs =
                                    if (it.select("tbody > tr").size > 1) {
                                        if (it.select("tbody > tr")[0].select("td").size > 1)
                                            it.select("tbody > tr")[1].select("td > a").map { e ->
                                                e.text() to e.attr("href")
                                            } else null
                                    } else null,
                                    author = if (it.select("tbody > tr").size > 1) {
                                        it.select("tbody > tr")[1].select("td").text()
                                    } else {
                                        ""
                                    }
                                )
                            )
                        } else {
                            it.select("tbody > tr > td > p.prequote").text()?.let { text ->
                                w.onNext(P(
                                    type = "no-name-quote",
                                    content = text,
                                    listOfHrefs =
                                    if (it.select("tbody > tr").size > 1) {
                                        if (it.select("tbody > tr")[0].select("td").size > 1)
                                            it.select("tbody > tr")[1].select("td > a").map { e ->
                                                e.text() to e.attr("href")
                                            } else null
                                    } else null
                                ))
                            }
                        }
                 */

                it.tagName() == "div" && it.className() == "thumb tright" -> {
                    if (android) Log.e(TAG, "is: img")
                        if (it.select("div > a > img").hasAttr("src"))
                            if (it.select("div > a > img").attr("src").isNotEmpty())
                                if("Attention32.png" !in it.select("div > a > img").attr("src"))
                                    w.onNext(
                                        P(
                                            type = "img",
                                            imgUrl = url + it.select("div > a > img").attr("src"),
                                            content = it.select("div.thumbcaption").text(),
                                            listOfHrefs = null
                                        )
                                    )


                }

                it.tagName() == "h3" -> {
                    if (android) Log.e(TAG, "is: h3")
                    w.onNext(
                        P(
                            type = "h3",
                            content = it.select("span.mw-headline").text(),
                            id = it.select("span.mw-headline").attr("id"),
                            listOfHrefs = null
                        )
                    )

                }

                it.tagName() == "h2" -> {
                    if (android) Log.e(TAG, "is: h2")
                    w.onNext(
                        P(
                            type = "h2",
                            content = it.select("span.mw-headline").text(),
                            id = it.select("span.mw-headline").attr("id"),
                            listOfHrefs = null
                        )
                    )
                }


            }
        }
    } catch (ioe: IOException) {
        ioe.printStackTrace()
    }
}

fun searchSource(searchText: String) : Observable<Pair<String, String>> = Observable.create{ w ->
    val searchDoc = Jsoup
        .connect(
            "https://lurkmore.to/index.php?" +
                    "title=%D0%A1%D0%BB%D1%83%D0%B6%D0%B5%D0%B1%D0%BD%D0%B0%D1%8F%3ASearch" +
                    "&profile=default" +
                    "&search=$searchText" +
                    "&fulltext=Search")

        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()
    searchDoc.select("ul.mw-search-results")[0].children().forEach { e ->
        val text = e.select("div > a")[0].text()
        val href = e.select("div > a")[0].attr("href")
        w.onNext(text to href)
    }
}

fun tocDataSource(uri: String) : Observable<P> = Observable.create{
    val doc = Jsoup.connect(uri)
        .userAgent("Chrome/4.0.249.0 Safari/532.5")
        .get()
    it.onNext(P(title = doc.select("#toctitle")[0].select("h2").text(), type = "toc_title", listOfHrefs = null))
    doc.select("table.toc")[0].select("tbody > tr > td > ul > li").forEach { li ->
        if(li.children().size == 1) it.onNext(
            P(listOfHrefs = listOf(li.select("a > span.toctext").text() to li.select("a").attr("href").removePrefix("#")),
                id = li.select("a > span.tocnumber").text()
                , type = "toc_li", content = li.select("a > span.toctext").text())
        ) else  {
            it.onNext(
                P(listOfHrefs = listOf(
                    li.select("a")[0].child(1).text() to li.select("a").attr("href").removePrefix("#")),
                    id = li.select("a > span.tocnumber").text()
                    , type = "toc_li", content = li.select("a")[0].child(1).text())
            )
            li.select("ul > li").forEach { e ->
                it.onNext(
                    P(listOfHrefs = listOf(
                        e.select("a > span.toctext").text() to e.select("a").attr("href").removePrefix("#")),
                        id = e.select("a > span.tocnumber").text()
                        , type = "mini_li", content = e.select("a > span.toctext").text())
                )
            }
        }

    }
}