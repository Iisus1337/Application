package com.example.myapplication4444.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapplication4444.showToast

class ConnectivityChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val isConnected = intent?.getBooleanExtra("noConnectivity", false) == false
        if (isConnected) {
            context?.showToast("Соединение установлено")
        } else {
            context?.showToast("Соединение потеряно")
        }
    }
}
