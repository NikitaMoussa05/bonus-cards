package com.buisness.bonuscards.user_agreement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.databinding.ActivityBonusProgramBinding
import com.buisness.bonuscards.databinding.ActivityUserAgreementBinding
import com.buisness.bonuscards.main.MainActivity
import com.buisness.bonuscards.service.BottomMenuLinks

class UserAgreementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserAgreementBinding

    private val repository = UserAccountRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        initObservers()
        setBottomMenuListeners()
    }

    private fun initObservers() {
        repository.uiInfo.observe(this) {
            if (it.userAgreementText.isNotEmpty()) {
                val text = it.userAgreementText.replace("\\n", System.getProperty("line.separator"))
                binding.txtMainText.text = text
            }
        }
    }

    private fun initListeners() {
        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }

    private fun setBottomMenuListeners() {
        binding.bottomMenu.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (BottomMenuLinks.menuMainLink.isNotEmpty())
                intent.putExtra("startLink", BottomMenuLinks.menuMainLink)
            startActivity(intent)
        }
        binding.bottomMenu.btnCatalogue.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (BottomMenuLinks.menuCatalogueLink.isNotEmpty())
                intent.putExtra("startLink", BottomMenuLinks.menuCatalogueLink)
            startActivity(intent)
        }
        binding.bottomMenu.btnCart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (BottomMenuLinks.menuCartLink.isNotEmpty())
                intent.putExtra("startLink", BottomMenuLinks.menuCartLink)
            startActivity(intent)
        }
        binding.bottomMenu.btnMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            if (BottomMenuLinks.menuMenuLink.isNotEmpty())
                intent.putExtra("startLink", BottomMenuLinks.menuMenuLink)
            startActivity(intent)
        }
        binding.fab.setOnClickListener {
            finish()
        }
    }
}