package com.example.aditya.kotlin101

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
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
    open lateinit var postTitle:Array<String?>
    lateinit var mapPost:Map<String, Any>
    lateinit var mapContent:Map<String, Any>
    lateinit var mapTitle:Map<String, Any>
    internal var doubleBackToExitPressedOnce = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //listview=findViewById<ListView>(R.id.list_view) as ListView

        findViewById<Button>(R.id.button).setOnClickListener() {
            val obj = "http://ymca.dreamhosters.com/wp-json/wp/v2/posts?filter[posts_per_page]=10&fields=id,title&filter[category_name]=all"
            fetchJson(obj)

            //Toast.makeText(this, obj, Toast.LENGTH_LONG).show()
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
            //response=urlString[0]
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

        //listview.adapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,postTitle)
        ProgressDialog(this).dismiss()
        val intent = Intent(this,ListViewActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce)
        {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            //super.onBackPressed();
            System.exit(0)
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
    }
}
