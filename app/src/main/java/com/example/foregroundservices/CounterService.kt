package com.example.foregroundservices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CounterService: Service() {

    private val counter = Counter()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?, flags: Int, startId: Int
    ): Int {

        when(intent?.action){
            CounterAction.START.name -> start()
            CounterAction.STOP.name -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    // Creating Foreground Post Notification :->
    private fun createNotification(counterValue: Int){
        val counterNotification = NotificationCompat
            .Builder(this, "counter_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Counter")
            .setContentText("Count : $counterValue")
            .setStyle(NotificationCompat.BigTextStyle())
            .build()

        startForeground(1, counterNotification)
    }

    private fun start(){
        CoroutineScope(Dispatchers.Default).launch {
            counter.start().collect{ counterValue ->
                Log.d("Counter", counterValue.toString())
                createNotification(counterValue)
            }
        }
    }

    private fun stop(){
        counter.stop()
        stopSelf()
    }
}