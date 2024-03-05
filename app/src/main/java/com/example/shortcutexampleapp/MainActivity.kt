package com.example.shortcutexampleapp

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.shortcutexampleapp.ui.theme.ShortcutExampleAppTheme

class MainActivity : ComponentActivity() {
    
    private val vModel:AppViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //get shortcut data from intent : if app is boot launched.
        getIntentData(intent)
        setContent {
            ShortcutExampleAppTheme {
                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        20.dp,Alignment.Bottom
                    )

                    ) {
                    when(vModel.appShortcutType){
                        ShortCutType.STATIC -> Text(text = "Static Shortcut Called")
                        ShortCutType.DYNAMIC ->Text(text = "Dynamic Shortcut Called")
                        ShortCutType.PINNED -> Text(text = "Pinned Shortcut Called")
                        null -> Unit
                    }

                    when(vModel.appShortCutData){
                        null -> Text(text = "No Data with shortcut.")
                        is String -> Text(text = "Data :  ${vModel.appShortCutData}")
                        else -> Text(text = "No Data found")
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(onClick = ::addDynamicShortcut) {
                            Text(text = "Dynamic Shortcut")
                        }
                        Text(
                            text = "Click on Dynamic Shortcut button to enable it.\nLong press on app icon to check.",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center

                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Button(onClick = ::addPinnedShortcut) {
                            Text(text = "Pinned Shortcut")
                        }
                    }
                }
            }
        }
    }

    //get intent data if app is in background.
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getIntentData(intent)
    }

    private fun addPinnedShortcut(){
        //On Android 8.0 (API level 26) and higher, you can create pinned shortcuts.
        val shortcutManager = getSystemService<ShortcutManager>()!!
        if(shortcutManager.isRequestPinShortcutSupported){
            val pinShortcutInfo = ShortcutInfo.Builder(applicationContext, "id3")
                .setShortLabel("Create Post")
                .setLongLabel("Create Post")
                .setIcon(Icon.createWithResource(applicationContext,R.drawable.baseline_image_24))
                .setIntent(Intent(applicationContext,MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcutData","Navigate to Create-Post Screen")
                    putExtra("shortcutId","pinned")
                })
                .build()
            val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)
            val successCallback = PendingIntent.getBroadcast(applicationContext, /* request code */ 0,
                pinnedShortcutCallbackIntent, /* flags */ PendingIntent.FLAG_IMMUTABLE)

            shortcutManager.requestPinShortcut(pinShortcutInfo,
                successCallback.intentSender)
        }
    }

    private fun addDynamicShortcut(){
        val shortcut = ShortcutInfoCompat.Builder(applicationContext,"id1")
            .setShortLabel("Website")
            .setLongLabel("Open website")
            .setIcon(IconCompat.createWithResource(applicationContext,R.drawable.baseline_web_24))
            .setIntent(Intent(applicationContext,MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra("shortcutData","https://www.mysite.example.com/")
                putExtra("shortcutId","dynamic")
            }).build()
        ShortcutManagerCompat.pushDynamicShortcut(applicationContext,shortcut)
    }

    private fun getIntentData(i:Intent?){
        i?.let {
            when(i.getStringExtra("shortcutId")){
                "static" -> vModel.onShortCutClicked(ShortCutType.STATIC)
                "dynamic" -> vModel.onShortCutClicked(ShortCutType.DYNAMIC)
                "pinned" -> vModel.onShortCutClicked(ShortCutType.PINNED)
            }

            val shortcutData = i.getStringExtra("shortcutData")
            if (shortcutData != null) {
                vModel.onShortCutDataPresent(shortcutData)
            }

        }
    }
}
