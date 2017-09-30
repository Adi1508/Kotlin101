package com.example.aditya.kotlin101

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


@Suppress("DEPRECATION")
public open class MainActivity : AppCompatActivity() {

    internal var json = ""
    internal lateinit var listview: ListView
    lateinit var postContent:Array<String?>
    var postTitle:Array<String?> = arrayOf("One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen")

    lateinit var mapPost:Map<String, Any>
    lateinit var mapContent:Map<String, Any>
    lateinit var mapTitle:Map<String, Any>
    internal var doubleBackToExitPressedOnce = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener() {
            val obj = "http://ymca.dreamhosters.com/wp-json/wp/v2/posts?filter[posts_per_page]=10&fields=id,title&filter[category_name]=all"
            fetchJson(obj)
        }
    }

    fun fetchJson(str: String) {
        FetchTask().execute(str)
    }

    private inner class FetchTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            ProgressDialog(this@MainActivity).setProgressStyle(ProgressDialog.STYLE_SPINNER)
            ProgressDialog(this@MainActivity).setMessage("Loading...")
            ProgressDialog(this@MainActivity).show()
        }

        override fun doInBackground(vararg urlString: String): String {
            var response = ""
            for (url in urlString) {
                val client = DefaultHttpClient()
                val httpget = HttpGet(url)

                try {
                    val execute = client.execute(httpget)
                    val cont = execute.entity.content

                    val buffer = BufferedReader(InputStreamReader(cont))
                    var s = buffer.readLine()
                    response += s

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            println(response)
            return response
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            json = result
            parseJson(json)
        }
    }

    fun parseJson(json: String) {

        println("LINE 88 " + json)

        var gson= Gson()
        var list=gson.fromJson(json,List::class.java) as List
        postTitle= arrayOfNulls<String>(list.size)
        postContent= arrayOfNulls<String>(list.size)
        for(i in 0..list.size-1){
            mapPost=list.get(i) as Map<String,Any>
            mapTitle=mapPost.get("title") as Map<String, Any>
            postTitle[i]= mapTitle.get("rendered") as String
            mapContent=mapPost.get("content") as Map<String, Any>
            postContent[i]= mapContent.get("rendered") as String
            println(postTitle[i])
        }
        val lv=findViewById<ListView>(R.id.list_view)
        lv.adapter= MainActivity.ListExampleAdapter(this, postTitle)
        ProgressDialog(this@MainActivity).dismiss()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce)
        {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            System.exit(0)
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
    }

    private class ListExampleAdapter(context: Context, data: Array<String?>) : BaseAdapter() {
        internal var sList = data

        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return sList.size
        }

        override fun getItem(position: Int): String? {
            return sList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.list_row, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }

            vh.label.text = sList[position]
            return view
        }
    }

    private class ListRowHolder(row: View?) {
        public val label: TextView

        init {
            this.label = row?.findViewById<TextView>(R.id.label) as TextView
        }
    }
}
