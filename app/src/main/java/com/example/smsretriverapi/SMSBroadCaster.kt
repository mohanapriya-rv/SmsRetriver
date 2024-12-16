package com.example.smsretriverapi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {
    var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null
    override fun onReceive(context: Context?, intent: Intent) {
        Log.d("priya","onReceive")
        if (intent.action === SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras: Bundle? = intent.extras
            val smsRetrieverStatus: Status? = extras?.get(SmsRetriever.EXTRA_STATUS) as Status?
            Log.d("priya","smsRetrieverStatus")
            when (smsRetrieverStatus?.statusCode) {

                CommonStatusCodes.SUCCESS -> {
                    Log.d("priya","SUCCESS")
                    val otpMessage = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String?
                    val otpValue = extractOTP(otpMessage)
                    if (otpValue!=null)
                    {
                        smsBroadcastReceiverListener?.onSuccess(otpValue)
                    }
                }
                CommonStatusCodes.TIMEOUT ->{
                    Log.d("priya","TIMEOUT")
                    smsBroadcastReceiverListener?.onFailure()
                }
                else ->{
                    Log.d("priya",smsRetrieverStatus?.statusCode.toString())
                }

            }
        }
    }

    interface SmsBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)
        fun onSuccess(otp:String)
        fun onFailure()
    }
}

fun extractOTP(message: String?): String? {
    // Define a regular expression pattern to match a string of 6 digits as an OTP
    val pattern = "\\b\\d{6}\\b".toRegex()

    // Find the first match of the pattern in the input message
    val matchResult = message?.let { pattern.find(it) }

    // Extract the OTP if a match is found
    return matchResult?.value
}
