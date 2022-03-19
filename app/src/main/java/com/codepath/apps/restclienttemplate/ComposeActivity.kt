package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.lang.Exception

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient
    lateinit var etCounter: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        etCounter = findViewById(R.id.etCounter)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
                etCounter.setText((280-s.length).toString() + "/280")
            }
        })
        client = TwitterApplication.getRestClient(this);

        // Handle tweet button click
        btnTweet.setOnClickListener{
            // Grab ET content
            val tweetContent = etCompose.text.toString()
            // Make sure tweet isn't empty
            if (tweetContent.isEmpty())
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
            // Make sure tweet isn't too long
            else if (tweetContent.length > 280)
                Toast.makeText(this, "This tweet is too long! Limit is 140 characters", Toast.LENGTH_SHORT).show()
            // Make api call to publish Tweet
            else{
                client.publishTweet(tweetContent, object:JsonHttpResponseHandler(){
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "ERROR publishing tweet: $throwable")
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        Log.i(TAG, "Tweet successfully published!")
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK,intent)
                        finish()
                    }

                })
            }
        }
    }
    companion object {
        val TAG = "ComposeActivity"
    }
}