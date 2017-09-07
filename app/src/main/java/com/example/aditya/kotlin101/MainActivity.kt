package com.example.aditya.kotlin101

import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import com.beust.klaxon.Parser
import com.github.salomonbrys.kotson.string
import com.github.salomonbrys.kotson.toJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.moshi.Moshi
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    internal var json=""
    internal lateinit var list : ArrayList<Product>
    internal lateinit var listview : ListView
    internal lateinit var mylist : ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener() {
            val obj = "http://ymca.dreamhosters.com/wp-json/wp/v2/posts?filter[posts_per_page]=10&fields=id,title&filter[category_name]=all"
            fetchJson(obj)
            //Toast.makeText(this, obj, Toast.LENGTH_LONG).show()
        }
    }

    fun fetchJson(str:String){
        FetchTask().execute(str)
    }

    private inner class FetchTask : AsyncTask<String,Void,String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            ProgressDialog(this@MainActivity).setMessage("Loading...")
            ProgressDialog(this@MainActivity).setProgressStyle(ProgressDialog.STYLE_SPINNER)
            ProgressDialog(this@MainActivity).show()
        }

        override fun doInBackground(vararg urlString: String): String {
            var response =""
            for(url in urlString){
                val client=DefaultHttpClient()
                val httpget=HttpGet(url)

                try{
                    val execute=client.execute(httpget)
                    val cont=execute.entity.content

                    val buffer=BufferedReader(InputStreamReader(cont))
                    var s=buffer.readLine()
                    response+=s

                }catch(e:Exception){
                    e.printStackTrace()
                }
            }
            //response=urlString[0]
            println(response)
            return response
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            json=result
            parseJson(json)
        }
    }

    fun parseJson(json: String){

        println("LINE 88 "+json)

        val gson= Gson()
        /**/
        val j=gson.toJson(json)
        println(j)
        val parser: Parser = Parser()
        val stringBuilder: StringBuilder = StringBuilder(j)
        val js: JsonObject = parser.parse(stringBuilder) as JsonObject
        println("ID: ${js.get("id")}")

        //Moshi library usage
        /*val moshi=Moshi.Builder().build()
        val jsonAdapter=moshi.adapter<Array<Product>>(Array<Product>::class.java)
        var products:Array<Product>?= null
        mylist=ArrayList<String>()

        try{
            products=jsonAdapter.fromJson(j)
        }catch(e:Exception){
            e.printStackTrace()
        }

        for(p in products!!){
            println(p.id +" "+ p.title)
            mylist.add("ID: "+p.id+"\n"+"Title: "+p.title)
        }*/
        list= ArrayList(Arrays.asList(*products))
        setupListView()
    }

    fun setupListView(){
        listview=findViewById<ListView>(R.id.list_view) as ListView
        listview.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mylist)
    }
}
