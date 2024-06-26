package com.optic.deliveryapp.utils

import android.util.Log
import com.github.nkzawa.socketio.client.Socket
import com.github.nkzawa.socketio.client.IO
import java.net.URISyntaxException

object SocketHandler {

    lateinit var msocket: Socket

    @Synchronized
    fun setSocket(){
        try{
            msocket = IO.socket("https://tad-mauve.vercel.app/orders/delivery")
        } catch (e: URISyntaxException){
            Log.d("Error","No se pudo conectar al socket ${e.message}")
        }
    }

    @Synchronized
    fun getSocket(): Socket{
        return msocket
    }

    @Synchronized
    fun establishConnection(){
        msocket.connect()
    }

    @Synchronized
    fun closeConnection(){
        msocket.disconnect()
    }
}