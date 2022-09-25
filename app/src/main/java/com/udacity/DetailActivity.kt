package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity.Companion.DOWNLOAD_STATUS_EXTRA
import com.udacity.MainActivity.Companion.FILE_NAME_EXTRA
import com.udacity.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        intent?.let {
            binding.contentDetail.fileNameTextView.text =
                intent.getStringExtra(FILE_NAME_EXTRA)

            binding.contentDetail.statusTextView.text =
                intent.getStringExtra(DOWNLOAD_STATUS_EXTRA)
        }

        binding.contentDetail.okButton.setOnClickListener {
            finish()
        }
    }

}
