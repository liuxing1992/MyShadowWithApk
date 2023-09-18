package com.fan.test_shadow_case

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun testCase1(view: View){
        startActivity(Intent(this , SecondActivity::class.java).apply {
            putExtra("data" , "testCase1")
        })
    }

    fun testCase2(view: View){
        startActivity(Intent(this , SecondActivity::class.java).apply {
            putExtra("data" , "testCase2")
        })
    }

    fun testCase3(view: View){
        startActivity(Intent(this , SecondActivity::class.java).apply {
            putExtra("data" , "testCase3")
        })
    }

    fun testCase4(view: View){
        startActivity(Intent(this , SecondActivity::class.java).apply {
            putExtra("data" , "testCase4")
        })
    }
}