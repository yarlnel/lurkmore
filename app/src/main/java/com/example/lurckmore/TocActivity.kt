package com.example.lurckmore

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.example.lurckmore.dataSource.P
import com.example.lurckmore.dataSource.tocDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_toc.*
import org.jsoup.Jsoup

class TocActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toc)
        intent.getStringExtra("uri")?.let {uri ->
            tocDataSource(uri)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {
                        when(it.type) {
                               "toc_li" -> {
                                    val view = layoutInflater.inflate(R.layout.text_p, list_la, false)
                                    val tv = view.findViewById<TextView>(R.id.tv_p)
                                    tv . text = makeSpannable(it)
                                    tv . movementMethod = LinkMovementMethod . getInstance ()
                                    list_la . addView (view)
                               }
                                "mini_li" -> {
                                    val view = layoutInflater.inflate(R.layout.li_la, list_la, false)
                                    val tv = view.findViewById<TextView>(R.id.tv_li)
                                    val num = view.findViewById<TextView>(R.id.tv_num)
                                    num . text = it.id.split(".")[1]
                                    tv . text = makeSpannable(it)
                                    tv . movementMethod = LinkMovementMethod . getInstance ()
                                    list_la . addView (view)
                                }
                                "toc_title" -> {
                                    val view = layoutInflater.inflate(R.layout.h, list_la, false)
                                    val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                                    tvTitle.text = it.title
                                    list_la.addView(view)
                                }
                        }
                    }
                }, {
                    it.printStackTrace()
                },
                    {

                })
        }
    }




    private fun makeSpannable(p : P) : SpannableString {
        val spannable = SpannableString(p.content)
        p.listOfHrefs?.forEach { e ->
            if (e.first in p.content && e.first.isNotEmpty()) {
                val indexStart = p.content.indexOf(e.first)
                val indexEnd = indexStart + e.first.length
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent()
                        intent.putExtra("id", e.second)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannable
    }
}
