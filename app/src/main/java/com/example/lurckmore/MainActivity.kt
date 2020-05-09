package com.example.lurckmore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.lurckmore.dataSource.P
import com.example.lurckmore.dataSource.contentSource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_toc.*
import org.jsoup.Jsoup
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val url = "https://lurkmore.to"
    private lateinit var uri : String
    private val TAG = MainActivity::class.java.simpleName
    private var id: String = ""
    private val ps : MutableList<Pair<P, View>> = mutableListOf()
    private var count_li = 0
    var visibility = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pb.visibility = ProgressBar.VISIBLE

        uri = intent.getStringExtra("uri") ?: "$url/КНДР"
        fab_search.visibility = FloatingActionButton.INVISIBLE
        fab_toc.visibility = FloatingActionButton.INVISIBLE
        tv_search.visibility = TextView.INVISIBLE
        tv_toc.visibility = TextView.INVISIBLE
        new_back.visibility = RelativeLayout.INVISIBLE
        fab_search.isClickable = false
        fab_toc.isClickable = false
        visibility = true

        fab_menu.setOnClickListener {
            if (visibility) {
                fab_search.visibility = FloatingActionButton.VISIBLE
                fab_toc.visibility = FloatingActionButton.VISIBLE
                tv_search.visibility = TextView.VISIBLE
                tv_toc.visibility = TextView.VISIBLE
                new_back.visibility = RelativeLayout.VISIBLE
                fab_search.isClickable = true
                fab_toc.isClickable = true
                visibility = false
            } else {
                fab_search.visibility = FloatingActionButton.INVISIBLE
                fab_toc.visibility = FloatingActionButton.INVISIBLE
                tv_search.visibility = TextView.INVISIBLE
                tv_toc.visibility = TextView.INVISIBLE
                new_back.visibility = RelativeLayout.INVISIBLE
                fab_search.isClickable = false
                fab_toc.isClickable = false
                visibility = true
            }
        }

        fab_toc.setOnClickListener {
            val intent = Intent(this@MainActivity, TocActivity::class.java)
            intent.putExtra("uri", uri)
            startActivityForResult(intent, 1)
        }

        fab_search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        val content = contentSource(uri, true)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    pb.visibility = ProgressBar.INVISIBLE
                    if (it.type != "li") count_li = 0
                    when (it.type) {
                        "h2" -> {
                            val view = layoutInflater.inflate(R.layout.h, ll, false)
                            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                            tvTitle.text = it.content
                            ll.addView(view)
                            ps.add(it to view)
                        }

                        "h3" -> {
                            val view = layoutInflater.inflate(R.layout.h, ll, false)
                            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                            tvTitle.text = it.content
                            ll.addView(view)
                            ps.add(it to view)
                        }

                        "img" -> {
                            val view = layoutInflater.inflate(R.layout.img_layout, ll, false)
                            val ivImg = view.findViewById<ImageView>(R.id.img_cvad)
                            val tvDesc = view.findViewById<TextView>(R.id.tv_description)
                            Picasso
                                .get()
                                .load(it.imgUrl).into(ivImg)
                            tvDesc.text = it.content
                            ll.addView(view)
                        }

                        "p" -> {
                            val view = layoutInflater.inflate(R.layout.text_p, ll, false)
                            val tv = view.findViewById<TextView>(R.id.tv_p)
                            tv.text = makeSpannable(it)
                            tv.movementMethod = LinkMovementMethod.getInstance()
                            ll.addView(view)
                        }

                        "quote-tiny" -> {
                            val view = layoutInflater.inflate(R.layout.quote, ll, false)
                            val tvAuthor = view.findViewById<TextView>(R.id.tv_author)
                            val tvContent = view.findViewById<TextView>(R.id.tv_content)
                            tvAuthor.text = it.author
                            tvContent.text = makeSpannable(it)
                            tvContent.movementMethod = LinkMovementMethod.getInstance()
                            ll.addView(view)
                        }

                        "quote" -> {
                            val view = layoutInflater.inflate(R.layout.quote, ll, false)
                            val tvAuthor = view.findViewById<TextView>(R.id.tv_author)
                            val tvContent = view.findViewById<TextView>(R.id.tv_content)
                            tvAuthor.text = it.author
                            tvContent.text = makeSpannable(it)
                            tvContent.movementMethod = LinkMovementMethod.getInstance()
                            ll.addView(view)
                        }

                        "plashka" -> {
                            Log.e("pl", "in::plashka")
                            val view = layoutInflater.inflate(R.layout.plasha, ll, false)
                            val tvContent = view.findViewById<TextView>(R.id.tv_content)
                            val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                            val ivPlashka = view.findViewById<ImageView>(R.id.iv_plasha)
                            tvContent.text = makeSpannable(it)
                            Picasso
                                .get()
                                .load(it.imgUrl)
                                .resize(100, 100)
                                .centerCrop()
                                .into(ivPlashka)
                            tvTitle.text = it.title
                            tvContent.movementMethod = LinkMovementMethod.getInstance()
                            ll . addView (view)
                        }

                        "li" -> {
                            val view = layoutInflater.inflate(R.layout.li_la, ll, false)
                            val tv = view.findViewById<TextView>(R.id.tv_li)
                            val num = view.findViewById<TextView>(R.id.tv_num)
                            count_li++
                            num . text = "$count_li. "
                            tv . text = makeSpannable(it)
                            tv . movementMethod = LinkMovementMethod . getInstance ()
                            ll . addView (view)
                            Log.e("li", view.toString())

                        }
                        "no-name-quote" -> {
                            val view = layoutInflater.inflate(R.layout.no_name_quote, ll, false)
                            val tvContent = view.findViewById<TextView>(R.id.tv_content)
                            tvContent.text = makeSpannable(it)
                            tvContent.movementMethod = LinkMovementMethod.getInstance()
                            ll.addView(view)
                        }
                    }
                },
                {
                    Log.e(TAG, it.toString())
                    Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_LONG).show()

                },
                {

                }
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fab_search.visibility = FloatingActionButton.INVISIBLE
        fab_toc.visibility = FloatingActionButton.INVISIBLE
        tv_search.visibility = TextView.INVISIBLE
        tv_toc.visibility = TextView.INVISIBLE
        new_back.visibility = RelativeLayout.INVISIBLE
        fab_search.isClickable = false
        fab_toc.isClickable = false
        visibility = true

        id = data?.getStringExtra("id") ?: ""
        ps.filter { it.first.type == "h2" || it.first.type == "h3" }.find { it.first.id == id }?.let{
            scroll_view.scrollTo(0, it.second.top)
            Log.e("map-oar", "do it ${it.second.top} text: ${it.first.content}")
        }
        Log.e("map-oar", "kuku")
    }

    private fun makeSpannable(p : P) : SpannableString {
        val spannable = SpannableString(p.content)
        p.listOfHrefs?.forEach { e ->
            if (e.first in p.content && e.first.isNotEmpty()) {
                val indexStart = p.content.indexOf(e.first)
                val indexEnd = indexStart + e.first.length
                spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark)),
                    indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        intent.putExtra("uri", e.second)
                        startActivity(intent)
                    }
                }, indexStart, indexEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannable
    }

}
