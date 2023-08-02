package com.buisness.bonuscards.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.databinding.ActivityMainBinding
import com.buisness.bonuscards.service.BottomMenuLinks
import com.buisness.bonuscards.service.Constants.haveBottomMenu
import com.buisness.bonuscards.service.Constants.mainWebLink
import com.buisness.bonuscards.sign_in.SignInActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startLink = intent.getStringExtra("startLink") ?: mainWebLink

        initListeners()
        initWebView(startLink)
        initBottomNavView()

        // init repository to get UI elements
        UserAccountRepository()

        setBottomMenuListeners()
    }

    private fun initBottomNavView() {
        if (!haveBottomMenu)
            binding.bottomMenu.root.visibility = View.GONE
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
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
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

    private fun initWebView(initLink: String) {
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (!url.isNullOrEmpty()) 
                    view?.loadUrl(url)

                return true
            }
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true

        binding.webView.loadUrl(initLink)
    }

    private fun initListeners() {
        binding.fab.setOnClickListener {
            Toast.makeText(this, "Opening Sign In Activity", Toast.LENGTH_LONG)
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}