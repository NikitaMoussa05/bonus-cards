package com.buisness.bonuscards.bonus_program

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.databinding.ActivityBonusProgramBinding
import com.buisness.bonuscards.databinding.ActivityContactsBinding
import com.buisness.bonuscards.main.MainActivity
import com.buisness.bonuscards.service.BottomMenuLinks

class BonusProgramActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBonusProgramBinding

    private val repository = UserAccountRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBonusProgramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        initObservers()
        setBottomMenuListeners()
    }

    private fun initObservers() {
        repository.uiInfo.observe(this) {
            if (it.bonusProgramText.isNotEmpty()) {
                val text = System.getProperty("line.separator")
                    ?.let { it1 -> it.bonusProgramText.replace("\\n", it1) }
                binding.txtMainText.text = text
                Log.d("Tag", it.bonusProgramText)
            }
        }
    }

    private fun initListeners() {
        binding.btnGoBack.setOnClickListener {
            finish()
        }
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
}