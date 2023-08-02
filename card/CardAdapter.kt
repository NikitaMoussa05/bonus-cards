package com.buisness.bonuscards.card

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.icu.lang.UCharacter.IndicPositionalCategory.RIGHT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.buisness.bonuscards.R
import com.buisness.bonuscards.api.model.CardInfo
import com.buisness.bonuscards.api.model.CardInfoBonus
import com.buisness.bonuscards.api.repository.UserAccountRepository
import com.buisness.bonuscards.databinding.ItemCardLayoutBinding
import com.buisness.bonuscards.purchase_history.adapter.Cheque
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*
import kotlin.math.log


class CardAdapter(private val context: Context) : ListAdapter<CardInfoBonus, CardAdapter.MyViewHolder>(DifferenceCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //val view: View = LayoutInflater.from(context).inflate(R.layout.item_card_layout, parent, false)
        val binding = ItemCardLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bind(getItem(position))
       // holder.tvName.text = String.format("Row number  %d ", position)

        //holder.imgBanner.setImageResource(R.drawable.ic_chest)



    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class MyViewHolder(private val binding: ItemCardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: CardInfoBonus) {
            Log.d("tag", "cards rebinded")
            initLogo(binding.imgMoney, card.smallCardLogo)
            when(card.type?.type) {
                1 -> {
                    binding.txtBonuses.text = card.type?.name.toString()
                    binding.txtBonusAmount.text = card.current_discount_value.toInt().toString() + "%"
                    binding.imgMoney.visibility = View.GONE
                }
                2 -> {
                    binding.txtBonuses.text = card.type?.name.toString()
                    binding.txtBonusAmount.text = card.current_discount_value.toInt().toString() + "%"
                    binding.imgMoney.visibility = View.GONE
                }
                3 -> {
                    binding.txtBonuses.text = card.type?.name.toString()
                    binding.txtBonusAmount.text = card.bonus_sum.toString()
                    if (card.bonuses.isNotEmpty() && card.bonuses.last().value > 0) {
                        val lastBonus = card.bonuses.last()
                        if (lastBonus.expire_date != "null" && lastBonus.expire_date != null)
                            binding.txtBonusExpiredAt.text = lastBonus.value.toString() + " бонусов сгорят " + lastBonus.expire_date
                    }
                }
                else -> {
                    binding.txtBonuses.text = "Карта"
                }
            }

            val bitmap = encodeAsBitmap(card.num, BarcodeFormat.CODE_128, img_width = 500, img_height = 250)
            binding.imgBarcode.setImageBitmap(bitmap)
            binding.btnShowBarcode.setOnClickListener {
                showDialog(card.num)
            }
            binding.imgBarcode.setOnClickListener {
                showDialog(card.num)
            }
        }


        private fun initLogo(imageView: ImageView, logo: String) {
            if (logo.isNotEmpty()) {
                Picasso.get()
                    .load(logo)
                    .into(imageView)
            }
        }
    }

    fun showDialog(msg: String) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_barcode)

        val img = dialog.findViewById(R.id.imgBarcode) as ImageView
        val bitmap = encodeAsBitmap(msg, BarcodeFormat.CODE_128, img_width = 500, img_height = 250)
        img.setImageBitmap(bitmap)

        val text = dialog.findViewById(R.id.txtDialog) as TextView
        text.text = msg

        val dialogButton1 = dialog.findViewById(R.id.btnOk) as Button
        dialogButton1.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun encodeAsBitmap(
        contents: String,
        format: BarcodeFormat,
        img_width: Int,
        img_height: Int
    ): Bitmap? {
        var hints: MutableMap<EncodeHintType?, Any?>?
        val encoding= guessAppropriateEncoding(contents)
        hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = encoding
        val writer = MultiFormatWriter()
        val result: BitMatrix
        result = try {
            writer.encode(contents, format, img_width, img_height, hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    private fun guessAppropriateEncoding(contents: CharSequence): String? {
        // Very crude at the moment
        for (i in 0 until contents.length) {
            if (contents[i].code > 0xFF) {
                return "UTF-8"
            }
        }
        return null
    }



     fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        close_image.setOnClickListener { finish() }
        save_image.setOnClickListener { updateProfile() }
        change_photo_text.setOnClickListener { takeCameraPicture() }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance().reference

        mDatabase.child("users").child(mAuth.currentUser!!.uid)
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                mUser = it.getValue(com.example.homeactivity.models.User::class.java)!!
                name_input.setText(mUser.name, TextView.BufferType.EDITABLE)
                username_input.setText(mUser.username, TextView.BufferType.EDITABLE)
                website_input.setText(mUser.website, TextView.BufferType.EDITABLE)
                bio_input.setText(mUser.bio, TextView.BufferType.EDITABLE)
                email_input.setText(mUser.email, TextView.BufferType.EDITABLE)
                phone_input.setText(mUser.phone?.toString(), TextView.BufferType.EDITABLE)
                profile_image.loadUserPhoto(mUser.photo)
            })
    }

        private fun takeCameraPicture() {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                val imageFile = createImageFile()
                mImageUri = FileProvider.getUriForFile(
                    this,
                    "com.example.homeactivity.fileprovider",
                    imageFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
            }
        }

        private fun createImageFile(): File {
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${simpleDateFormat.format(Date())}_",
                ".jpg",
                storageDir
            )
        }

        @SuppressLint("MissingSuperCall")
         fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
                val uid = mAuth.currentUser!!.uid
                mStorage.uploadUserPhoto(uid, mImageUri) {
                    val photoUrl = it.task.toString()
                    mDatabase.updateUserPhoto(uid, photoUrl) {
                        mUser = mUser.copy(photo = photoUrl)
                        profile_image.loadUserPhoto(mUser.photo)
                    }
                }
            }
        }


        private fun updateProfile() {
            mPendingUser = readInputs()
            val error = validate(mPendingUser)
            if  (error == null) {
                if (mPendingUser.email == mUser.email) {
                    updateUser(mPendingUser)
                } else {
                    PasswordDialog().show(supportFragmentManager, "password_dialog")
                }
            } else {
                showToast(error)
            }
        }


        private fun readInputs(): User {
            return User(
                name = name_input.text.toString(),
                username = username_input.text.toString(),
                email = email_input.text.toString(),
                website = website_input.text.toStringOrNull(),
                bio = bio_input.text.toStringOrNull(),
                phone = phone_input.text.toStringOrNull()
            )
        }

        fun onPasswordConfirm(password: String) {
            if (password.isNotEmpty()) {
                val credential = EmailAuthProvider.getCredential(mUser.email, password)
                mAuth.currentUser!!.reauthenticate(credential) {
                    mAuth.currentUser!!.updateEmail(mPendingUser.email) {
                        updateUser(mPendingUser)
                    }
                }
            } else {
                showToast("You must enter your password")
            }
        }

        private fun updateUser(user: com.example.homeactivity.models.User) {
            val updatesMap = mutableMapOf<String, Any?>()
            if (user.name != mUser.name) updatesMap["name"] = user.name
            if (user.username != mUser.username) updatesMap["username"] = user.username
            if (user.website != mUser.website) updatesMap["website"] = user.website
            if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
            if (user.email != mUser.email) updatesMap["email"] = user.email
            if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

            mDatabase.updateUser(mAuth.currentUser!!.uid, updatesMap) {
                showToast("Profile saved")
                finish()
            }
        }

        private fun validate(user: com.example.homeactivity.models.User): String? =
            when {
                user.name.isEmpty() -> "Please enter name"
                user.username.isEmpty() -> "Please enter username"
                user.email.isEmpty() -> "Please enter email"
                else -> null
            }

        private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit) {
            updateEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
        }

        private fun StorageReference.uploadUserPhoto(uid: String, photo: Uri,
                                                     onSuccess: (UploadTask.TaskSnapshot) -> Unit) {
            child("users/$uid/photo").putFile(mImageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    mStorage.child("users/$uid/photo").downloadUrl.addOnCompleteListener { task ->
                        if (it.isSuccessful) {
                            onSuccess(it.result!!)
                        } else {
                            showToast(it.exception!!.message!!)
                        }
                    }
                }
            }
        }


        private fun DatabaseReference.updateUserPhoto(uid: String, photoUrl: String,
                                                      onSuccess: () -> Unit){
            child("users/$uid/photo").setValue(photoUrl)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
        }

        private fun DatabaseReference.updateUser(uid: String, updates: Map<String, Any?>, onSuccess: () -> Unit) {
            child("users").child(uid).updateChildren(updates)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onSuccess()
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
        }

        private fun showToast(message: String) {
            println("the foto is changing")
        }

        private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
            reauthenticate(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
        }
    }

}
