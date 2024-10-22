package org.forgerock.pebblebank.ui.main

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.forgerock.android.auth.FRListener
import org.forgerock.android.auth.Listener
import org.forgerock.android.auth.Node
import org.forgerock.android.auth.PingOneProtectInitializeCallback

class PingOneProtectInitCallbackKt (val callback1: PingOneProtectInitializeCallback) {

    fun start(context: Context, listener: FRListener<Void>) {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            try {
                callback1.start(context)
                Listener.onSuccess(listener, null);
            } catch (e: Exception) {
                Listener.onException(listener, e)
            }
        }
    }
}