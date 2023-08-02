package com.buisness.bonuscards.card

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.buisness.bonuscards.R
import com.buisness.bonuscards.api.Network
import com.buisness.bonuscards.api.model.CardInfoBonus
import com.buisness.bonuscards.bonus_program.BonusProgramActivity
import com.buisness.bonuscards.contacts.ContactsActivity
import com.buisness.bonuscards.databinding.ActivityCardBinding
import com.buisness.bonuscards.main.MainActivity
import com.buisness.bonuscards.purchase_history.PurchaseHistoryActivity
import com.buisness.bonuscards.service.BottomMenuLinks
import com.buisness.bonuscards.service.Constants.app_id
import com.buisness.bonuscards.service.Constants.secret
import com.buisness.bonuscards.shops.ShopsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: CardViewModel
    private lateinit var viewPager: ViewPager2
    private lateinit var cardsAdapter: CardAdapter
    private var clientCards: List<CardInfoBonus> = listOf()

    private var clientNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Network.createRepository()
        binding.progressBarCyclic.visibility = VISIBLE

        auth = Firebase.auth
        clientNumber = getNormalizedNum(auth.currentUser?.phoneNumber.toString())
        // clientNumber = "79129500356"
        // 79129500356 three cards
        // 79083290650 bonus history

        viewModel = CardViewModel(Network.cardRepository, getNormalizedNum(clientNumber))

        viewPager = binding.viewpager

        configureViewPager()

        setupCarousel()

        initListeners()
        initObservers()
        setBarcode()

        setBottomMenuListeners()

        viewModel.getPartnerContactInfo(app_id, secret)
    }

    private fun getNormalizedNum(num: String): String {
        var realNum = num
        if (realNum.contains("+7")) {
            realNum = realNum.replace("+7", "7")
        }
        return realNum
    }

    private fun setBarcode() {
    }

    private fun initListeners() {
        binding.btnPurchaseHistory.setOnClickListener {
            val intent = Intent(this, PurchaseHistoryActivity::class.java)
            if (clientCards.isNotEmpty()) {
                val cardsId = clientCards.map { it.id.toString() }.toTypedArray()
                val bundle = Bundle()
                bundle.putStringArray("discount_card_id", cardsId)
                intent.putExtras(bundle)
            }
            else
                intent.putExtra("discount_card_id", "-1")
            startActivity(intent)
        }
        binding.btnBonusProgram.setOnClickListener {
            val link = viewModel.uiInfo.value?.bonusProgramLink
            if (!link.isNullOrEmpty()) {
                openMainActivityWithLink(link)
            } else {
                // Открываем форму с текстом
                val intent = Intent(this, BonusProgramActivity::class.java)
                startActivity(intent)
            }
        }
        binding.btnShops.setOnClickListener {
            val link = viewModel.uiInfo.value?.shopsLink
            if (!link.isNullOrEmpty()) {
                openMainActivityWithLink(link)
            } else {
                // Открываем форму
                val intent = Intent(this, ShopsActivity::class.java)
                startActivity(intent)
            }
        }
        binding.btnContacts.setOnClickListener {
            val link = viewModel.uiInfo.value?.contactsLink
            if (!link.isNullOrEmpty()) {
                openMainActivityWithLink(link)
            } else {
                // Открываем форму
                val intent = Intent(this, ContactsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun openMainActivityWithLink(link: String) {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("startLink", Uri.parse(link).toString())
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Ссылка не работает", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initObservers() {
        viewModel.clientCards.observe(this) {
            clientCards = it
            cardsAdapter.apply {
                submitList(clientCards)
            }
            binding.progressBarCyclic.visibility = GONE
            if (clientCards.isEmpty()) {
                binding.txtYouHaveNoCards.visibility = VISIBLE
                binding.btnReleaseCard.visibility = VISIBLE
            } else {
                binding.txtYouHaveNoCards.visibility = GONE
                binding.btnReleaseCard.visibility = GONE
            }
            viewModel.getDiscountCardTypes(app_id, secret)
        }
        viewModel.isNoCardsButtonVisible.observe(this) {
            if (it) {
                binding.txtYouHaveNoCards.visibility = VISIBLE
                binding.btnReleaseCard.visibility = VISIBLE
                binding.progressBarCyclic.visibility = GONE
            } else {
                binding.txtYouHaveNoCards.visibility = GONE
                binding.btnReleaseCard.visibility = GONE
            }
        }
        viewModel.updatePartnerInfo.observe(this) {
            viewModel.getPartnerContactInfo(app_id, secret)
        }

        viewModel.partnerId.observe(this) {
            viewModel.getCardsByPartnerId(app_id, secret)
        }
        viewModel.updateBonuses.observe(this) {
            cardsAdapter.notifyDataSetChanged()
        }
        viewModel.cardTypes.observe(this) { typesList ->
            val types = typesList.associateBy({it.id}, {it})
            for (card in clientCards) {
                val type = types[card.discount_card_type_id]
                card.type = type
            }
            cardsAdapter.apply {
                submitList(clientCards)
            }
            cardsAdapter.notifyDataSetChanged()
        }
        viewModel.uiInfo.observe(this) {
            clientCards.map { card -> card.smallCardLogo = it.smallCardLogo }
            cardsAdapter.apply {
                submitList(clientCards)
            }
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

    private fun setupCarousel() {

        viewPager.offscreenPageLimit = 1

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx =
            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
            // If you want a fading effect uncomment the next line:
            page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }
        viewPager.setPageTransformer(pageTransformer)

        //// The ItemDecoration gives the current (centered) item horizontal margin so that
        //// it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            this,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        viewPager.addItemDecoration(itemDecoration)


    }

    private fun configureViewPager() {
        viewPager = binding.viewpager

        cardsAdapter = CardAdapter(this).apply {
            submitList(clientCards)
        }

        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewpager.adapter = cardsAdapter
        binding.viewpager.offscreenPageLimit = 5
    }
}