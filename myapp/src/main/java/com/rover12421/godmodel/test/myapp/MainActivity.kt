package com.rover12421.godmodel.test.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.Nullable
import com.rover12421.android.godmodel.myapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CanProguard.test001()
        CanProguard.test002()
    }
}