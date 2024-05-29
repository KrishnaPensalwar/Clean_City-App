package com.example.garbagecollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Get_Started : AppCompatActivity() {

    private lateinit var start :Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        start=findViewById(R.id.get_started)

        start.setOnClickListener {
            val intent = Intent(this,login::class.java)
            startActivity(intent)
            finish()
        }
    }
}