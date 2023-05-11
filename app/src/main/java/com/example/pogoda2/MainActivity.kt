package com.example.pogoda2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pogoda2.fragments.MainFragment

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            supportFragmentManager.beginTransaction()
                .replace(R.id.placeholder, MainFragment.newInstance()).commit()
//в разметке placeholder показать фрагмент MainFragment(f1)
        }
    }




