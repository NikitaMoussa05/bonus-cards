package com.buisness.bonuscards.purchase_history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.buisness.bonuscards.databinding.ActivityPurchaseHistoryBinding
import com.buisness.bonuscards.main.MainActivity
import com.buisness.bonuscards.purchase_history.adapter.Cheque
import com.buisness.bonuscards.purchase_history.adapter.PurchaseHistoryAdapter
import com.buisness.bonuscards.service.BottomMenuLinks.menuCartLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuCatalogueLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuMainLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuMenuLink
import com.buisness.bonuscards.service.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class PurchaseHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPurchaseHistoryBinding

    private val viewModel: PurchaseHistoryViewModel by viewModel()
    private val purchaseAdapter = PurchaseHistoryAdapter()

    private var cardIdList: MutableList<String> = mutableListOf()
    private var cheques: MutableList<Cheque> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCardIdList()

        initRecycler()
        initObservers()
        initListeners()

        setBottomMenuListeners()
        viewModel.updatePartnerInfo.postValue(true)
    }

    private fun initCardIdList() {
        val bundle = intent.extras
        val myStringArray = bundle!!.getStringArray("discount_card_id")
        cardIdList = myStringArray?.toMutableList() ?: mutableListOf()
        if (cardIdList.isEmpty()) {
            binding.progressBarCyclic.visibility = View.GONE
            binding.txtNoPurchases.visibility = View.VISIBLE
        }
    }

    private fun initListeners() {
        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }

    private fun setBottomMenuListeners() {
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnHome, menuMainLink)
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnCatalogue, menuCatalogueLink)
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnCart, menuCartLink)
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnMenu, menuMenuLink)
        binding.fab.setOnClickListener {
            finish()
        }
    }

    private fun setBottomMenuButtonListenerWithLink(btn: View, link: String) {
        btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (link.isNotEmpty())
                intent.putExtra("startLink", link)
            startActivity(intent)
        }
    }

    private fun initRecycler() = with(binding.recyclerView) {
        layoutManager = LinearLayoutManager(this@PurchaseHistoryActivity)
        adapter = purchaseAdapter
        itemAnimator = null
    }

    private fun initObservers() {
        viewModel.updatePartnerInfo.observe(this) {
            for (id in cardIdList) {
                viewModel.getCheques(id, Constants.secret)
            }
        }
        viewModel.cheques.observe(this) { newCheques ->
            cheques.addAll(newCheques)
            Log.d("TAG", "Новые чеки!!!")
            val listOfCheques = cheques.toMutableList()

            for (cheque in listOfCheques) {
                purchaseAdapter.addCheque(cheque)
                Log.d("TAG", "Added ${cheque.date}")
            }
            binding.progressBarCyclic.visibility = View.GONE
            if (newCheques.isEmpty()) {
                binding.txtNoPurchases.visibility = View.VISIBLE
            } else {
                binding.txtNoPurchases.visibility = View.GONE
            }
        }
    }
}