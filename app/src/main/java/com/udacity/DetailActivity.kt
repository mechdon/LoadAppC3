package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        file_name.text = filename
        status_tv.text = status

        if (status == getString(R.string.success)){
            status_tv.setTextColor(getColor(R.color.colorPrimaryDark))
        } else {
            status_tv.setTextColor(Color.RED)
        }

        ok_button.setOnClickListener {
            finish()
        }

    }

}
