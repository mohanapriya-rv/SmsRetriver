package com.example.smsretriverapi

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.smsretriverapi.databinding.MainactivityBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private var binding: MainactivityBinding? = null
    private val smsBroadCastReceiver = SmsBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = MainactivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        Log.d("priya",getAppSignatureHash(packageName))
        val signature =AppSignatureHelper(this)
        Log.d("priyasigned",signature.appSignatures.toString() )

        binding!!.reteriveOtp.setOnClickListener {
            initSmsRetriever(this)
            intialiseSMSRestriver()
        }
        super.onCreate(savedInstanceState)
    }

    private fun intialiseSMSRestriver() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.d("priya","register if")
                this.registerReceiver(smsBroadCastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), Context.RECEIVER_EXPORTED)
            } else {
                Log.d("priya","register else")
                ContextCompat.registerReceiver(
                   this,
                    smsBroadCastReceiver,
                    IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
                    ContextCompat.RECEIVER_EXPORTED
                )
            }
            autoFillOTP()
    }
    fun initSmsRetriever(context: Context) {
        val client = SmsRetriever.getClient(context).startSmsRetriever()
        client.addOnSuccessListener {
            Log.d("SmsRetriever", "SmsRetriever exception: addOnSuccessListener")

        }.addOnFailureListener {
            Log.d("SmsRetriever", "SmsRetriever exception: ${it.localizedMessage}")
        }

    }

    private fun autoFillOTP() {
        Log.d("priya","autoFillOTP")
        smsBroadCastReceiver.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {

            override fun onSuccess(intent: Intent?) {
                Log.d("priya","intent")
            }

            override fun onSuccess(otp: String) {
                Log.d("priya","MFOTP onSuccess")
            }

            override fun onFailure() {
                Log.d("priya","MFOTP BottomsheetTIMEOUT")
            }
        }
    }

    fun getAppSignatureHash(packageName: String): String {
        try {
            val signature = MessageDigest.getInstance("SHA").digest(packageName.toByteArray())
            return Base64.encodeToString(signature, Base64.NO_WRAP).substring(0, 11)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}