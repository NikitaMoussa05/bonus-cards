package com.buisness.bonuscards.shops

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.buisness.bonuscards.api.model.TextContact
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.contacts.ContactsViewModel
import com.buisness.bonuscards.contacts.adapter.ContactsAdapter
import com.buisness.bonuscards.databinding.ActivityContactsBinding
import com.buisness.bonuscards.databinding.ActivityShopsBinding
import com.buisness.bonuscards.databinding.ItemTextContactBinding
import com.buisness.bonuscards.main.MainActivity
import com.buisness.bonuscards.service.BottomMenuLinks
import com.buisness.bonuscards.service.BottomMenuLinks.menuCartLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuCatalogueLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuMainLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuMenuLink
import com.buisness.bonuscards.shops.adapter.ShopsAdapter

class ShopsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShopsBinding

    private val viewModel = ShopsViewModel(UserAccountRepository())

    private lateinit var adapter: ShopsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ShopsAdapter(::openUrl)

        initObservers()
        initRecycler()
        initListeners()
        setBottomMenuListeners()

    }

    private fun initRecycler() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
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
            if (menuMenuLink.isNotEmpty())
                intent.putExtra("startLink", link)
            startActivity(intent)
        }
    }

    private fun initObservers() {
        viewModel.cityWithShops.observe(this) {
            adapter.contacts = it
        }
        viewModel.uiInfo.observe(this) {
            setBottomMenuListeners()
        }
    }

    private fun initListeners() {
        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }

    private fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent)
    }
}