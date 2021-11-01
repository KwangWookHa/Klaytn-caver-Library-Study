package net.mhand.klaytntoywook

import com.klaytn.caver.Caver
import javax.inject.Inject

class KlaytnRepository @Inject constructor(
    private val caver: Caver,
){

    fun getBalance(address: String) = caver.rpc.klay.getBalance(address)



}