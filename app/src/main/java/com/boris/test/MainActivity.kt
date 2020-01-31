package com.boris.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var timer : Timer

    private val data = arrayOf(
        50,
        70,
        -1,
        100,
        90,
        30,
        50,
        10,
        120,
        100,
        0,
        10,
        30,
        90,
        0
    )

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        timer = timer("data", false, 0L, 1500L) {
            setNextProgress()
        }
    }

    override fun onPause() {
        timer.cancel()
        super.onPause()
    }

    private fun setNextProgress() {
        val progress = data[index]
        custom_progress_bar.setProgress(progress)
        runOnUiThread {
            tv_progress.text = "pregress = $progress"
        }
        index = (index+1) % data.size
    }
}
