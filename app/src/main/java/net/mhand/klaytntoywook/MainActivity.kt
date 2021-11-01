package net.mhand.klaytntoywook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
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
    }

    private fun initWalletAddress() {
        binding?.initAddress = "0x87A8557e7912AA12CEc0Dab3b92A90184e609B4c"
    }

    private fun initBalanceObserver() {
        mainViewModel.balanceLiveData.observe(this) {
            binding?.balance = it.toString()
        }
    }

    override fun onClick(v: View?) {
        with(binding) {
            when(v) {
                this?.btnBalance -> {
                    this?.textWalletAddress?.let {
                        mainViewModel.getBalance(it.text.toString())
                    }
                }
                else -> {

                }
            }
        }
    }
}