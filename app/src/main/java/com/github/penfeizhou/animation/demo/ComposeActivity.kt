package com.github.penfeizhou.animation.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage


/**
 *
 * @Description:    ComposeActivity
 * @Author:         pengfei.zhou
 * @CreateDate:     2023/9/6
 */
class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimationDemo()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AnimationDemo() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp)
    ) {
        GlideImage(
            model = "file:///android_asset/test.avif",
            contentDescription = "Test",
        )
        GlideImage(
            model = "file:///android_asset/1.webp",
            contentDescription = "Test",
        )
        GlideImage(
            model = "file:///android_asset/test1.png",
            contentDescription = "Test",
        )
        GlideImage(
            model = "file:///android_asset/2.gif",
            contentDescription = "Test",
        )
    }
}