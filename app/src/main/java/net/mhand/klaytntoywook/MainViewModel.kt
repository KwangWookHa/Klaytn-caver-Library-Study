package net.mhand.klaytntoywook

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klaytn.caver.Caver
import com.klaytn.caver.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val caver: Caver
): ViewModel() {

    private val _balanceLiveData by lazy { MutableLiveData<String>() }
    val balanceLiveData : LiveData<String> = _balanceLiveData

    fun getBalance(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val quantity = caver.rpc.klay.getBalance(address).send().value.toString()
            val convertedValue = caver.utils.convertFromPeb(quantity, Utils.KlayUnit.KLAY)
            _balanceLiveData.postValue(convertedValue)
        }
    }

    fun loadWallet() {
        val inputStream = app.assets.open("kaikas-hgo0930.json")

    }





}