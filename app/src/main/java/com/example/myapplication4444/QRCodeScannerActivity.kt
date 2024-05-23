package com.example.myapplication4444

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCodeScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)
        startQRCodeScanner()
    }

    private fun startQRCodeScanner() {
        IntentIntegrator(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents != null) {
            // Здесь предполагается, что QR код содержит ссылку или уникальный идентификатор
            handleQRCodeResult(result.contents)
        } else {
            Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleQRCodeResult(contents: String) {
        // Переход к RedOctoberDetailActivity с определенным ID
        val intent = Intent(this, RedOctoberDetailActivity::class.java).apply {
            putExtra("place_id", "-NyWjB1ZRZvCHFPRagbC")
        }
        startActivity(intent)
    }
}
