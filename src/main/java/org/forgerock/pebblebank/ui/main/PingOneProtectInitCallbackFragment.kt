/*
 * Copyright (c) 2022 ForgeRock. All rights reserved.
 *
 *  This software may be modified and distributed under the terms
 *  of the MIT license. See the LICENSE file for details.
 */
package org.forgerock.pebblebank.ui.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.forgerock.android.auth.Logger
import org.forgerock.android.auth.PingOneProtectInitException
import org.forgerock.android.auth.PingOneProtectInitializeCallback
import org.forgerock.android.auth.ui.callback.CallbackFragment

/**
 * A simple [Fragment] subclass.
 */
class PingOneProtectInitCallbackFragment : CallbackFragment<PingOneProtectInitializeCallback>() {

    override fun onStart() {
        super.onStart()
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            try {
                callback?.start(requireContext())
                next()
            } catch (e: PingOneProtectInitException) {
                Logger.error("PingOneInitException", e, e.message)
                next()
            } catch (e: Exception) {
                Logger.error("PingOneInitException", e, e.message)
                callback?.setClientError(e.message.toString());
                next()
            }
        }
    }

}