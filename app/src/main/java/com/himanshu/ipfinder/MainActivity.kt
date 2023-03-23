package com.himanshu.ipfinder

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private val ipList = arrayListOf<Model>()
    val firestore = Firebase.firestore

    lateinit var tvIP : TextView
    private lateinit var ipAddress : String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

//        val tvISP = findViewById<TextView>(R.id.tv_isp)
         tvIP = findViewById<TextView>(R.id.tv_ip)
//        val tvAddress = findViewById<TextView>(R.id.tv_address)
        ipAddress=""

        CoroutineScope(Dispatchers.Main).launch {
            val myPublicIp = getMyPublicIpAsync().await()
            ipAddress = myPublicIp
            if(myPublicIp!="You are not connected to internet"){
                tvIP.text = "Your public IP address is $myPublicIp"
                storeData(ipAddress)
            }else{
                tvIP.text = "$myPublicIp"

            }



        }

        val uniqueID: String = getDeviceId(this)!!
        var list: ArrayList<Model> = ArrayList()
        var rv= findViewById<RecyclerView>(R.id.rv)
        Firebase.firestore.collection("users")
            .whereEqualTo(
                "id", uniqueID

            )
            .get().addOnSuccessListener {
                list.clear()
                for (doc in it){
                    val data = doc.toObject(Model::class.java)
                    list.add(data)

                }
                rv.adapter= IPAdapter(list)
//                Log.i("hp",list[1].ipAddress!!)

            }



    }

    private fun storeData(ipAddress: String) {
        val timeStamp = SimpleDateFormat("dd/M/yyyy HH:mm:ss").format(Date())
        val uniqueID: String = getDeviceId(this)!!
        val item =  Model(uniqueID,ipAddress,timeStamp)
//        var data= hashMapOf<String, Any>()
//        ipList.add(item)
//        data["ip"]=ipAddress
//        data["time"]=timeStamp
//        data["id"]=uniqueID


        firestore.collection("users")
                .add(item)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        }







    private suspend fun getMyPublicIpAsync() : Deferred<String> =
        coroutineScope {
            async(Dispatchers.IO) {
                var result = ""
                result = try {
                    val url = URL("https://api.ipify.org")
                    val httpsURLConnection = url.openConnection()
                    val iStream = httpsURLConnection.getInputStream()
                    val buff = ByteArray(1024)
                    val read = iStream.read(buff)
                    String(buff,0, read)
                } catch (e: Exception) {
                    "You are not connected to internet"
//                    "error : $e"
                }
                return@async result
            }
        }

    fun getDeviceId(context: Context): String? {
        val deviceId: String
        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
        } else {
            val mTelephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (mTelephony.deviceId != null) {
                mTelephony.deviceId
            } else {
                Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
            }
        }
        return deviceId
    }

}