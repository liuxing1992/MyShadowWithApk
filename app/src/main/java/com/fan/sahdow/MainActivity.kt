package com.fan.sahdow

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fan.sahdow.HostApplication.PART_MAIN

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startPlugin(view: View){
        val application = application as HostApplication
        application.loadPlugin(PART_MAIN) {
            val pluginIntent = Intent()
            pluginIntent.setClassName(packageName, "com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestListActivity")
            pluginIntent.putStringArrayListExtra("activities", SampleComponentManager.sActivities)
            val intent: Intent = application.pluginLoader.mComponentManager.convertPluginActivityIntent(pluginIntent)
            startActivity(intent)
        }
    }

    fun startPlugin2(view: View){
        val application = application as HostApplication
        application.loadPlugin(PART_MAIN) {
            val pluginIntent = Intent()
            pluginIntent.setClassName(packageName, "com.fan.test_shadow_case.MainActivity")
            pluginIntent.putStringArrayListExtra("activities", SampleComponentManager.sActivities)
            val intent: Intent = application.pluginLoader.mComponentManager.convertPluginActivityIntent(pluginIntent)
            startActivity(intent)
        }
    }
}