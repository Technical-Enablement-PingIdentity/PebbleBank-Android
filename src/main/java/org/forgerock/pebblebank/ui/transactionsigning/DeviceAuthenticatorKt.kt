package org.forgerock.pebblebank.ui.transactionsigning

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.forgerock.android.auth.FRListener
import org.forgerock.android.auth.Listener
import org.forgerock.android.auth.devicebind.DeviceAuthenticator
import org.forgerock.android.auth.devicebind.DeviceBindingStatus
import org.forgerock.android.auth.devicebind.initialize

class DeviceAuthenticatorKt(val deviceAuthenticator: DeviceAuthenticator) {

    fun initialize(userId: String) {
        deviceAuthenticator.initialize(userId)
    }

    fun authenticate(context: Context, listener: FRListener<DeviceBindingStatus>) {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            try {
                val status = deviceAuthenticator.authenticate(context)
                Listener.onSuccess(listener, status)
            } catch (e: Exception) {
                Listener.onException(listener, e)
            }
        }
    }
}