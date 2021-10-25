package wesa.xelar.realgoldencoder.activities

import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class Dashboard : AppCompatActivity() {
    private var mNfcAdapter: NfcAdapter? = null

    private var passportData : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        passportData = this.intent.getStringExtra("data").toString()
        //Toast.makeText(this, NFCUtil.retrieveNFCMessage(this.intent), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter?.let {
            NFCUtil.enableNFCInForeground(it, this, javaClass)
        }
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.let {
            NFCUtil.disableNFCInForeground(it, this)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val messageWrittenSuccessfully = NFCUtil.createNFCMessage(passportData, intent)
        Toast.makeText(this, messageWrittenSuccessfully.ifElse("Successful Written to Tag", "Something When wrong Try Again"), Toast.LENGTH_LONG).show()
        if (messageWrittenSuccessfully) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun <T> Boolean.ifElse(primaryResult: T, secondaryResult: T) = if (this) primaryResult else secondaryResult
}