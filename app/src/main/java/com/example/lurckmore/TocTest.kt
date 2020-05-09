package com.example.lurckmore

import com.example.lurckmore.dataSource.tocDataSource
import com.example.lurckmore.dataSource.url

fun main() {
    val result = tocDataSource("$url/КНДР")
        .subscribe({
            println(it)
        },{
            it.printStackTrace()
        },{

        })
}