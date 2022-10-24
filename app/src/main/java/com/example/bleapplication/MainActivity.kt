package com.example.bleapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bleapplication.client.ClientActivity
import com.example.bleapplication.client.ClientActivity1
import com.example.bleapplication.client.ClientActivity2
import com.example.bleapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(
            DataBindingUtil.setContentView(this, R.layout.activity_main)
                    as ActivityMainBinding
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    ClientActivity1::class.java
                )
            )
            finish()
            /*launchServerButton.setOnClickListener {
                startActivity(Intent(this@MainActivity,
                    ServerActivity::class.java))
            }
            launchClientButton.setOnClickListener {
                startActivity(Intent(this@MainActivity,
                    ClientActivity::class.java))
            }*/
        }
    }
}