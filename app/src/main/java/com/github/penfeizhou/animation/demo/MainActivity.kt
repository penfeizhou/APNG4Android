package com.github.penfeizhou.animation.demo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.penfeizhou.animation.demo.databinding.ActivityMainBinding

/**
 *
 * @Description:    MainActivity
 * @Author:         pengfei.zhou
 * @CreateDate:     2023/9/6
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            100
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tv0.setOnClickListener(this)
        binding.tvAvif.setOnClickListener(this)
        binding.tv1.setOnClickListener(this)
        binding.tv2.setOnClickListener(this)
        binding.tv3.setOnClickListener(this)
        binding.tv4.setOnClickListener(this)
        binding.tv5.setOnClickListener(this)
        binding.tv6.setOnClickListener(this)
        binding.tv7.setOnClickListener(this)
        binding.tv8.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_0 -> {
                val intent = Intent(this, ComposeActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_avif -> {
                val intent = Intent(this, AnimationTestActivity::class.java)
                intent.putExtra(
                    "files", arrayOf(
                        "test.avif",
                        "world-cup.avif",
                        "wheel.avif",
                    )
                )
                startActivity(intent)
            }

            R.id.tv_1 -> {
                val intent = Intent(this, AnimationTestActivity::class.java)
                intent.putExtra(
                    "files", arrayOf(
                        "apng_detail_guide.png"
                    )
                )
                startActivity(intent)
            }

            R.id.tv_2 -> {
                val intent = Intent(this, AnimationTestActivity::class.java)
                intent.putExtra(
                    "files", arrayOf(
                        "test.png"
                    )
                )
                startActivity(intent)
            }

            R.id.tv_3 -> {
                val intent = Intent(this, AnimationTestActivity::class.java)
                intent.putExtra(
                    "files", arrayOf(
                        "world-cup.webp"
                    )
                )
                startActivity(intent)
            }

            R.id.tv_4 -> {
                val intent = Intent(this, AnimationTestActivity::class.java)
                intent.putExtra(
                    "files", arrayOf(
                        "lossless.webp"
                    )
                )
                startActivity(intent)
            }

            R.id.tv_5 -> {
                val intent = Intent(this, APNGTestActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_6 -> {
                val intent = Intent(this, AnimationTestActivity::class.java)
                intent.putExtra(
                    "files", arrayOf(
                        "world-cup.gif",
                        "1.gif",
                        "2.gif",
                        "3.gif",
                        "4.gif",
                        "5.gif",
                        "6.gif"
                    )
                )
                startActivity(intent)
            }

            R.id.tv_7 -> {
                val intent = Intent(this, EncoderTestActivity::class.java)
                startActivity(intent)
            }

            R.id.tv_8 -> {
                val intent = Intent(
                    this,
                    APNGRecyclerViewTestActivity::class.java
                )
                startActivity(intent)
            }
        }
    }
}
