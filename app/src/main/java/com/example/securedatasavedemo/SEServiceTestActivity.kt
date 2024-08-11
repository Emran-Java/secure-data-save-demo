package com.example.securedatasavedemo

import android.os.Bundle
import android.se.omapi.Reader
import android.se.omapi.SEService
import android.se.omapi.Session
import android.se.omapi.Channel
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executors

class SEServiceTestActivity : AppCompatActivity() , SEService.OnConnectedListener{


    private lateinit var seService: SEService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_seservice_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SEService
        val executor = Executors.newSingleThreadExecutor()
        seService = SEService(this, executor, this)


    }

    //use for SEService
    override fun onConnected() {
        // Called when the SEService is connected and ready to use

        // Get the available readers
        val readers: Array<Reader> = seService.readers

        // Example: Accessing the first reader (e.g., embedded SE)
        if (readers.isNotEmpty()) {
            val reader = readers[0]

            try {
                // Check if the reader is SE
                if (reader.isSecureElementPresent) {
                    // Open a session
                    val session: Session = reader.openSession()

                    // Select an applet (for example, by sending a SELECT command)
                    // The AID (Application Identifier) must be known for the applet
                    val aid = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x01) // Replace with actual AID
                    val channel: Channel? = session.openBasicChannel(aid)

                    // Send APDU command to the applet
                    val commandApdu = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, aid.size.toByte()) + aid
                    val responseApdu: ByteArray? = channel?.transmit(commandApdu)

                    // Process the response APDU
                    // Example: Extract status word from the response
                    val sw1 = responseApdu!![responseApdu.size - 2].toInt() and 0xFF
                    val sw2 = responseApdu[responseApdu.size - 1].toInt() and 0xFF
                    if (sw1 == 0x90 && sw2 == 0x00) {
                        // Success, process the response data
                        val responseData = responseApdu.sliceArray(0 until (responseApdu.size - 2))
                        Toast.makeText(this, "Response Data: ${responseData.toHex()}", Toast.LENGTH_LONG).show()

                    } else {
                        // Error, handle accordingly
                        Toast.makeText(this, "Error: $sw1 $sw2", Toast.LENGTH_LONG).show()
                    }

                    // Close the channel and session
                    channel.close()
                    session.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error accessing SE: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "No Secure Element reader available", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure to disconnect the SEService when done
        seService.shutdown()
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { byte -> "%02x".format(byte) }

}
