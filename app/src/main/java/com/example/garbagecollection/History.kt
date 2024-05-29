package com.example.garbagecollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton


class History : AppCompatActivity() {

    private lateinit var Home_btn: ImageButton
    private lateinit var Upload_btn: ImageButton
    private lateinit var History_btn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        Home_btn=findViewById(R.id.Home_btn)

        Home_btn.setOnClickListener {
            val intent =  Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }
        Upload_btn=findViewById(R.id.Upload_btn)

        Upload_btn.setOnClickListener {
            val intent =  Intent(this,Upload::class.java)
            startActivity(intent)
            finish()
        }
        History_btn=findViewById(R.id.History_btn)

        History_btn.setOnClickListener {
            val intent =  Intent(this,History::class.java)
            startActivity(intent)
            finish()
        }
    }
}
