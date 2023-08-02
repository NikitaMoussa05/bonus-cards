package com.buisness.bonuscards.sign_in

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.card.CardActivity
import com.buisness.bonuscards.contacts.ContactsActivity
import com.buisness.bonuscards.databinding.ActivitySignInBinding
import com.buisness.bonuscards.main.MainActivity
import com.buisness.bonuscards.service.BottomMenuLinks
import com.buisness.bonuscards.user_agreement.UserAgreementActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var auth: FirebaseAuth
    private val repository = UserAccountRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.editNumber.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Tag", "before $p1 $p2 $p3 ")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Tag", "onTextChanged  $p1 $p2 $p3")
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null && p0.length < 2) {
                    binding.editNumber.setText("+7")
                    binding.editNumber.setSelection(2)
                } else if (p0 != null && !p0.startsWith("+7")) {
                    binding.editNumber.setText("+7$p0")
                }
            }

        })

        initCallbacks()
        initObservers()
        setOnClickListeners()
        setBottomMenuListeners()
    }

    private fun initObservers() {
        repository.uiInfo.observe(this) {
            if (it.mainLogo.isNotEmpty())
                Picasso.get()
                    .load(it.mainLogo)
                    .into(binding.imgLogo)
            binding.txtEnterBold.text = it.signInBoldText
            binding.txtAddition.text = it.signInAdditionalText
            binding.txtCouldNotSignIn.text = it.signInCouldNotConnectText
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if already authorized
        if (auth.currentUser != null) {
            val intent = Intent(this, CardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                Toast.makeText(this@SignInActivity, "Верификация не удалась", Toast.LENGTH_LONG)
                    .show()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {

                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                Toast.makeText(this@SignInActivity, "Код отправлен", Toast.LENGTH_SHORT).show()
                storedVerificationId = verificationId
                resendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this@SignInActivity, CardActivity::class.java)
                    startActivity(intent)

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }

                    Toast.makeText(this@SignInActivity, "Неверный SMS код", Toast.LENGTH_LONG)
                        .show()
                    // Update UI

                }
                //опдтягиватьь данные из фаербейз
            }


    }

    private fun saveNewMessagingToken() {
        Log.w("New token", "Saving...")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.w("New token", token)
            val repository = UserAccountRepository()
            repository.addToken(token)
        }
    }

    private fun setOnClickListeners() {
        binding.btnSendCode.setOnClickListener {
            var number = binding.editNumber.text.toString()
            if (number.length != 12) {
                Toast.makeText(this, "Неправильный номер", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (number.startsWith("8")) {
                number = number.replaceFirst("8", "+7")
            }
            sendVerificationCode(number)
            saveNewMessagingToken()
        }

        binding.btnApplyCode.setOnClickListener {
            val code = binding.editCode.text.toString()
            verifyCode(code)
        }
        binding.txtCouldNotSignIn.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }
        binding.btnOpenUserAgreement.setOnClickListener {
            val link = repository.uiInfo.value?.userAgreementLink
            if (!link.isNullOrEmpty()) {
                openMainActivityWithLink(link)
            } else {
                // Открываем форму
                val intent = Intent(this, UserAgreementActivity::class.java)
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
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Toast.makeText(this@SignInActivity, "Отправляем код", Toast.LENGTH_SHORT).show()
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }
}