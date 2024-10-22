package org.forgerock.pebblebank.controller

import android.net.Uri
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.StatusLine
import org.forgerock.android.auth.InitProvider
import org.forgerock.android.auth.PolicyAdvice
import org.forgerock.android.auth.interceptor.AdviceHandler
import java.io.IOException
import java.net.HttpURLConnection

private const val ADVICES = "advices"
private const val AUTH_INDEX_VALUE = "authIndexValue"
private const val TXID = "_txid"
private const val WWW_AUTHENTICATE = "WWW-Authenticate"
private const val LOCATION = "location"

abstract class IdentityGatewayAdviceInterceptor :
    org.forgerock.android.auth.interceptor.IdentityGatewayAdviceInterceptor() {

    @Throws(IOException::class)
    fun interceptResponse(chain: Interceptor.Chain, response: Response): Response {
        //val response: Response = chain.proceed(chain.request())

        val advice: PolicyAdvice
        try {
            advice = getAdvice(response)
            runBlocking {
                getAdviceHandler(advice)
                    .onAdviceReceived(InitProvider.getCurrentActivity(), advice)
            }
        } catch (e: Exception) {
            return response;
        }
        //Discard the existing response
        try {
            response.close()
        } catch (e: Exception) {
            //ignore
        }
        //Retry the request
        return chain.proceed(decorateRequest(chain.request(), advice))
    }

    private fun getAdvice(response: Response): PolicyAdvice {
        if (!response.isSuccessful && response.code == StatusLine.HTTP_TEMP_REDIRECT) {
            val location = response.header(LOCATION)
            return getAdvice(location)
        } else if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED &&
            response.header(WWW_AUTHENTICATE, null) != null) {
            val authenticate = response.header(WWW_AUTHENTICATE)
            val adviceStr = authenticate!!.split(",".toRegex()).associate {
                val (left, right) = it.split("=")
                left to right.replace("^\"|\"$".toRegex(), "")
            }[ADVICES]
            return PolicyAdvice.parseAsBase64(adviceStr)
        }
        throw IllegalArgumentException("Advice Not Found")

    }

    /**
     * Extract the Advice from the location redirect url
     *
     * @param location The redirect location
     * @return Policy Advice or null if advice not found.
     */
    private fun getAdvice(location: String?): PolicyAdvice {
        val redirect = Uri.parse(location)
        val advice = redirect.getQueryParameter(AUTH_INDEX_VALUE)
        if (advice != null) {
            return try {
                PolicyAdvice.parse(advice)
            } catch (e: Exception) {
                PolicyAdvice.parseAsBase64XML(advice)
            }
        }
        throw IllegalArgumentException("Advice Not Found: $location")
    }

    /**
     * Decorate the request with additional parameter which required for Policy Advice
     *
     * @param original The original Request
     * @param advice   The Advice
     * @return The decorated Request.
     */
    private fun decorateRequest(original: Request, advice: PolicyAdvice): Request {
        return if (advice.getType() == PolicyAdvice.TRANSACTION_CONDITION_ADVICE) {
            //Add _txid to the original query parameter
            val originalHttpUrl = original.url
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter(TXID, advice.value)
                .build()
            original.newBuilder().url(url).build()
        } else {
            original
        }
    }

    override fun getAdviceHandler(advice: PolicyAdvice): AdviceHandler {
        TODO("Not yet implemented")
    }
}