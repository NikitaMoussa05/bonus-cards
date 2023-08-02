package com.buisness.bonuscards.contacts

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.buisness.bonuscards.R
import com.buisness.bonuscards.api.model.Contact
import com.buisness.bonuscards.api.model.TextContact
import com.buisness.bonuscards.api.model.UiInfoModel
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.contacts.adapter.ContactsAdapter
import com.buisness.bonuscards.databinding.ActivityContactsBinding
import com.buisness.bonuscards.databinding.ItemTextContactBinding
import com.buisness.bonuscards.main.MainActivity
import com.buisness.bonuscards.service.BottomMenuLinks

class ContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactsBinding

    private val viewModel = ContactsViewModel(UserAccountRepository())

    private lateinit var adapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ContactsAdapter(::callUriIntent)

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
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnHome,
            BottomMenuLinks.menuMainLink
        )
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnCatalogue,
            BottomMenuLinks.menuCatalogueLink
        )
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnCart,
            BottomMenuLinks.menuCartLink
        )
        setBottomMenuButtonListenerWithLink(binding.bottomMenu.btnMenu,
            BottomMenuLinks.menuMenuLink
        )
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

    private fun initObservers() {
        viewModel.contacts.observe(this) {
            adapter.contacts = it
        }
        viewModel.text_contacts.observe(this) {
            loadTextContacts(it)
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

    private fun loadTextContacts(contacts: List<TextContact>) {
        for (contact in contacts) {
            val textInfoView = ItemTextContactBinding.inflate(layoutInflater)
            textInfoView.txtTitle.text = contact.name
            textInfoView.txtText.text =contact.data
            binding.textListLayout.addView(textInfoView.root)
        }
    }


    private fun callUriIntent(intentType: String, uri: String) {
        val intent = Intent(intentType, Uri.parse(uri))
        startActivity(intent)
    }


}