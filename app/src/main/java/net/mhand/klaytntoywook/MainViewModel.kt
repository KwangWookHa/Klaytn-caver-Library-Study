package net.mhand.klaytntoywook

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klaytn.caver.Caver
import com.klaytn.caver.methods.response.TransactionReceipt
import com.klaytn.caver.transaction.TxPropertyBuilder
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor
import com.klaytn.caver.utils.Utils
import com.klaytn.caver.wallet.keyring.KeyStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.protocol.ObjectMapperFactory
import java.io.File
import java.lang.RuntimeException
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val caver: Caver
) : ViewModel() {

    private val _fromBalanceLiveData by lazy { MutableLiveData<String>() }
    val fromBalanceLiveData: LiveData<String> = _fromBalanceLiveData

    private val _toBalanceLiveData by lazy { MutableLiveData<String>() }
    val toBalanceLiveData: LiveData<String> = _toBalanceLiveData

    private val _receiptLiveData by lazy { MutableLiveData<TransactionReceipt.TransactionReceiptData>() }
    val receiptLiveData: LiveData<TransactionReceipt.TransactionReceiptData> = _receiptLiveData

    fun getFromBalance(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val quantity = caver.rpc.klay.getBalance(address).send().value.toString().also {
                Log.i("#####", "##### getFromBalance quantity -> $it")
            }
            val convertedValue = caver.utils.convertFromPeb(quantity, Utils.KlayUnit.KLAY)
            _fromBalanceLiveData.postValue(convertedValue)
        }
    }

    fun getToBalance(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val quantity = caver.rpc.klay.getBalance(address).send().value.toString().also {
                Log.i("#####", "##### getToBalance quantity -> $it")
            }
            val convertedValue = caver.utils.convertFromPeb(quantity, Utils.KlayUnit.KLAY)
            _toBalanceLiveData.postValue(convertedValue)
        }
    }

    fun sendTx(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val keyStoreFile = createFileFromKeyStoreJson()
            val objectMapper = ObjectMapperFactory.getObjectMapper()
            val keyStore = objectMapper.readValue(keyStoreFile, KeyStore::class.java)
            val keyring = caver.wallet.keyring.decrypt(keyStore, "Gkqhfl@014")
            if (caver.wallet.length() == 0) {
                caver.wallet.add(keyring)
            }

            val value = BigInteger(caver.utils.convertToPeb(BigDecimal.ONE, "KLAY"))

            val valueTransfer = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                    .setFrom(keyring.address)
                    .setTo(address)
                    .setValue(value)
                    .setGas(BigInteger.valueOf(25000))
            )

            valueTransfer.sign(keyring)

            val result = caver.rpc.klay.sendRawTransaction(valueTransfer.rawTransaction).send()
            if (result.hasError()) {
                throw RuntimeException(result.error.message)
            }

            val transactionReceiptProcessor = PollingTransactionReceiptProcessor(caver, 1000, 15)
            val receiptData = transactionReceiptProcessor.waitForTransactionReceipt(result.result)
            _receiptLiveData.postValue(receiptData)
        }
    }

    private fun createFileFromKeyStoreJson(): File? =
        try {
            val inputStream = app.resources.openRawResource(R.raw.hgo0930)
            val file = File(app.cacheDir, Config.KEYSTORE_FILE_NAME).apply {
                outputStream().use {
                    inputStream.copyTo(it)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
}