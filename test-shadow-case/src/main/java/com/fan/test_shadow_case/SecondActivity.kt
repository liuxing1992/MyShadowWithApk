package com.fan.test_shadow_case

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class SecondActivity : AppCompatActivity() {

    private lateinit var text : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        text = findViewById(R.id.tv)
        var texts = intent.getStringExtra("data")
        text.text = texts
    }
}