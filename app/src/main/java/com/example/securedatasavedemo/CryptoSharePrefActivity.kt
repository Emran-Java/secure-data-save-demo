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
//        editor.putString("encrypted_data", encryptedData.joinToString("") { "%02x".format(it) })
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

    //try for test
    fun storeEncryptedData(context: Context, encryptedData: ByteArray, iv: ByteArray) {
//        val masterKey = MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_SIV)
//            .build()

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)


        val sharedPreferences = EncryptedSharedPreferences.create(
            "encrypted_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.putString("encrypted_data", encryptedData.joinToString("") { "%02x".format(it) })
        editor.putString("iv", iv.joinToString("") { "%02x".format(it) })
        editor.apply()
    }
}

/*
code sample
import android.content.Context;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurePrefsManager {

    private static final String SHARED_PREFS_FILE = "secure_shared_prefs";
    private SharedPreferences encryptedSharedPreferences;

    public SecurePrefsManager(Context context) throws GeneralSecurityException, IOException {
        // Generate or retrieve the Master Key for encryption
        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        // Initialize EncryptedSharedPreferences
        encryptedSharedPreferences = EncryptedSharedPreferences.create(
                SHARED_PREFS_FILE,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public void saveData(String key, String value) {
        SharedPreferences.Editor editor = encryptedSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // Or editor.commit() if you prefer synchronous
    }

    public String getData(String key) {
        return encryptedSharedPreferences.getString(key, null);
    }

    public static void main(String[] args) {
        // This method is for demonstration purposes. In a real Android app, you would call these methods from an Activity or Service.
        Context context = null; // You would get the actual context in an Android app

        try {
            SecurePrefsManager securePrefsManager = new SecurePrefsManager(context);

            // Save encrypted data
            securePrefsManager.saveData("username", "mySecureUsername");

            // Retrieve encrypted data
            String retrievedData = securePrefsManager.getData("username");
            System.out.println("Decrypted Data: " + retrievedData);

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}

*
*
* */