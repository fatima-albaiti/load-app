package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_detail)
        setSupportActionBar(toolbar)
        binding.contentDetail.fileName.text = intent.getStringExtra(MainActivity.DOWNLOAD_NAME)?:""
        binding.contentDetail.fileStatus.text = intent.getStringExtra(MainActivity.DOWNLOAD_STATUS)?:""
    }

}
