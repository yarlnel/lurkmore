package com.example.lurckmore

import com.example.lurckmore.dataSource.contentSource
import com.example.lurckmore.dataSource.url
import io.reactivex.Observable
import org.jsoup.Jsoup
import java.io.IOException



fun main() {
    val res = contentSource("$url/КНДР", false)
        .subscribe(
            {
                when (it.type) {
                 "tpl-quote", "quote" -> print(
                    "\n" + """
type:         ${it.type}
id:           ${it.id}
content:      ${it.content}
author:       ${it.author}
title:        ${it.title}
imgUrl:       ${it.imgUrl}
listOfHrefs:  ${it.listOfHrefs?.joinToString("\n") { e -> "${e.first} >>> ${e.second}" }}

                        """.trimIndent().trimMargin().trim() + "\n"
                )
                  /*  else -> print(
                        "\n" + """
type:         ${it.type}
id:           ${it.id}
content:      
${it.content}

author:       ${it.author}
title:        ${it.title}
imgUrl:       ${it.imgUrl}
listOfHrefs: 
${it.listOfHrefs?.joinToString("\n") { e -> "${e.first} >>> ${e.second}" }}

                        """.trimIndent().trimMargin().trim() + "\n\n"
                    )*/
                }
            },
            {
                it.printStackTrace()
            },
            {
                println("that is all")
            }
        )
}
/*
 """
                            ${"\n"}
                            type:         ${it.type}
                            content:      ${it.content}
                            author:       ${it.author}
                            title:        ${it.title}
                            imgUrl:       ${it.imgUrl}
                            listOfHrefs:  ${it.listOfHrefs?.joinToString("\n") { e -> "${e.first} >>> ${e.second}" }}
                            ${"\n"}
                        """.trimIndent().trimMargin().trim()
*/