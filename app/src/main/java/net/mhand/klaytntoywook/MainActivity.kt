package net.mhand.klaytntoywook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import net.mhand.klaytntoywook.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), View.OnClickListener {

    var binding: ActivityMainBinding? = null

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@MainActivity
            viewModel = viewModel
            listener = this@MainActivity
        }
        initWalletAddress()
        initBalanceObserver()
        initTxObserver()
    }

    private fun initWalletAddress() {
        binding?.fromAddress = Config.fromAddress
        binding?.toAddress = Config.toAddress
    }

    private fun initBalanceObserver() {
        mainViewModel.fromBalanceLiveData.observe(this) {
            binding?.fromBalance = it
        }
        mainViewModel.toBalanceLiveData.observe(this) {
            binding?.toBalance = it
        }
    }

    private fun initTxObserver() {
        mainViewModel.receiptLiveData.observe(this) {
            mainViewModel.getFromBalance(binding?.editFromAddress?.text.toString())
            mainViewModel.getToBalance(binding?.editToAddress?.text.toString())
        }
    }

    override fun onClick(v: View?) {
        with(binding) {
            when(v) {
                this?.btnBalance -> {
                    this?.editFromAddress?.let {
                        mainViewModel.getFromBalance(it.text.toString())
                    }
                }
                this?.btnSendTx -> {
                    this?.editToAddress?.let {
                        mainViewModel.sendTx(it.text.toString())
                    }
                }
                else -> {

                }
            }
        }
    }
}