package io.kaendagger.bamquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fabNext.setOnClickListener {
            if (etServerUrl.text?.isNotEmpty() == true){
                val serverUrl = etServerUrl.text.toString()
                val intent = Intent(this,QuizActivity::class.java).apply {
                    putExtra("ServerURL",serverUrl)
                }
                startActivity(intent)
            }else{
                etServerUrl.error = "Server Url Cannot be empty"
            }
        }

        btnDefault.setOnClickListener {
            val intent = Intent(this,QuizActivity::class.java)
            startActivity(intent)
        }
    }
}
