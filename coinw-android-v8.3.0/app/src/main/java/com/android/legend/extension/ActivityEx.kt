package com.android.legend.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import huolongluo.byw.byw.base.BaseApp

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.appViewModels(
        noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }

    return ViewModelLazy(VM::class, { BaseApp.getSelf().viewModelStore }, factoryPromise)
}


fun Activity.launchLoginActivity() {
    //LoginActivity.launch(this)
}

//通过浏览器打开url
fun Activity.openUrlByBrowser(url: String) {
    intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}