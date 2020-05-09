package com.example.lurckmore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import com.example.lurckmore.dataSource.searchSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import org.jsoup.Jsoup


class SearchActivity : AppCompatActivity() {
    private val url = "https://lurkmore.to"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                scroll_la.removeAllViews()
                val res = searchSource(newText)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        val view = layoutInflater.inflate(R.layout.text_p, scroll_la, false)

                        val tvTitle = view.findViewById<TextView>(R.id.tv_p)
                        val spannable = SpannableString(it.first)
                        val indexStart = 0
                        val indexEnd = it.first.length
                        spannable.setSpan(
                            object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    val intent = Intent(this@SearchActivity, MainActivity::class.java)
                                    intent.putExtra("uri", url+it.second)
                                    startActivity(intent)
                                }
                            }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        tvTitle.text = spannable
                        tvTitle.movementMethod = LinkMovementMethod.getInstance()
                        scroll_la.addView(view)
                    }, {}, {})
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                scroll_la.removeAllViews()
                val res = searchSource(query)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        val view = layoutInflater.inflate(R.layout.text_p, scroll_la, false)

                        val tvTitle = view.findViewById<TextView>(R.id.tv_p)
                        val spannable = SpannableString(it.first)
                        val indexStart = 0
                        val indexEnd = it.first.length
                        spannable.setSpan(
                            object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    val intent = Intent(this@SearchActivity, MainActivity::class.java)
                                    intent.putExtra("uri", url+it.second)
                                    startActivity(intent)
                                }
                            }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        tvTitle.text = spannable
                        tvTitle.movementMethod = LinkMovementMethod.getInstance()
                        scroll_la.addView(view)
                    }, {}, {})

                return true
            }
        })
    }



}
