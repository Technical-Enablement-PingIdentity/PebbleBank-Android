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
import org.forgerock.android.auth.PingOneProtectEvaluationCallback
import org.forgerock.android.auth.PingOneProtectEvaluationException
import org.forgerock.android.auth.ui.callback.CallbackFragment

/**
 * A simple [Fragment] subclass.
 */
class PingOneProtectEvalCallbackFragment : CallbackFragment<PingOneProtectEvaluationCallback>() {

    override fun onStart() {
        super.onStart()
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            try {
                callback.getData(requireContext())
                next()
            } catch (e: PingOneProtectEvaluationException) {
                Logger.error("PingOneRiskEvaluationCallback", e, e.message)
                next()
            } catch (e: Exception) {
                Logger.error("PingOneRiskEvaluationCallback", e, e.message)
                callback?.setClientError(e.message.toString());
                next()
            }
        }
    }

}