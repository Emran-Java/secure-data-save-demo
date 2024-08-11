package com.example.securedatasavedemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

import java.security.KeyStore
import javax.crypto.Cipher

class CryptoSharePrefActivity : AppCompatActivity() {

    var mEtInputVal : EditText? = null
    var mTvDisplay : TextView? = null
    var mBtnStore : Button? = null
    var mBtnGet : Button? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crypto_share_pref)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mEtInputVal = findViewById<EditText>(R.id.etInputVal)
        mTvDisplay = findViewById<TextView>(R.id.tvDisplay)
        mBtnStore = findViewById<Button>(R.id.btnStore)
        mBtnGet = findViewById<Button>(R.id.btnGet)


        /* val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        val parameterSpec = KeyGenParameterSpec.Builder(
            "keyOne",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()
        keyPairGenerator.initialize(parameterSpec)
        keyPairGenerator.generateKeyPair()
        */
        mBtnStore?.setOnClickListener {
            //mTvDisplay.text = encryptData("keyOne", "Dmoney".toByteArray(Charsets.UTF_8)).toString()
            storeSecrateData(mEtInputVal?.text.toString())
        }

        mBtnGet?.setOnClickListener {
            //mTvDisplay.text = decryptData("keyOne","Dmoney".toByteArray(Charsets.UTF_8)).toString()
            mTvDisplay?.text = retriveVal()
        }

        cryptoSharePref(this)

    }

    //val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    var sharedPreferences : SharedPreferences? = null
    private fun cryptoSharePref(context: Context) {

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    }

    fun storeSecrateData(saveData:String){
        // Store a key
        sharedPreferences?.edit()?.putString("keyAlias", saveData)?.apply()

    }

    fun retriveVal():String{
        // Retrieve a key
        val keyValue = sharedPreferences?.getString("keyAlias", null)

        return keyValue ?: ""
    }

    // Encrypt data
    fun encryptData(alias: String, data: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val publicKey = keyStore.getCertificate(alias).publicKey
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data)
    }

    // Decrypt data
    fun decryptData(alias: String, encryptedData: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val privateKey = keyStore.getKey(alias, null)
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(encryptedData)
    }

}