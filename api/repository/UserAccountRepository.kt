package com.buisness.bonuscards.api.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.buisness.bonuscards.api.model.Contact
import com.buisness.bonuscards.api.model.TextContact
import com.buisness.bonuscards.api.model.UiInfoModel
import com.buisness.bonuscards.service.BottomMenuLinks.menuCartLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuCatalogueLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuMainLink
import com.buisness.bonuscards.service.BottomMenuLinks.menuMenuLink
import com.buisness.bonuscards.service.Constants.account_name
import com.buisness.bonuscards.service.Constants.app_id
import com.buisness.bonuscards.service.Constants.secret
import com.buisness.bonuscards.shops.adapter.CityToShop
import com.buisness.bonuscards.shops.adapter.CityWithShops
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserAccountRepository {
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>>
        get() = _contacts

    private val _textContacts = MutableLiveData<List<TextContact>>()
    val textContacts: LiveData<List<TextContact>>
        get() = _textContacts

    private val _shops = MutableLiveData<List<CityWithShops>>()
    val shops: LiveData<List<CityWithShops>>
        get() = _shops

    private val _uiInfo = MutableLiveData<UiInfoModel>()
    val uiInfo : LiveData<UiInfoModel>
        get() = _uiInfo

    private val database = Firebase.database("https://bonuscards-5bb1c-default-rtdb.europe-west1.firebasedatabase.app")
    private val myRef: DatabaseReference = database.getReference("account").child(account_name)
    private val uiRef: DatabaseReference = database.getReference("account").child(account_name).child("ui_info")
    private val contactsRef = myRef.child("contacts")
    private val textContactsRef = myRef.child("text_contacts")
    private val shopsRef = myRef.child("shops")

    init {
        getAccountInfo()
        getContacts()
        getTextContacts()
        getShops()
    }

    private fun getShops() {
        if (myRef != null) {
            shopsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val cityList = mutableListOf<CityWithShops>()
                    for (postSnapshot in dataSnapshot.children) {

                        val city = postSnapshot.getValue(CityToShop::class.java)
                        if (city != null) {
                            //cityList.add(city)
                            val shops = city.shops.values.toList()
                            cityList.add(CityWithShops(city.cityName, shops))
                        }
                    }
                    Log.d("Tag", cityList.toString())
                    _shops.value = cityList

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    //Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        } else {
            Log.d("Tag", "Ref is null")
        }
    }

    private fun getContacts() {
        // Read from the database
        if (myRef != null) {

            contactsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val contactList = mutableListOf<Contact>()
                    for (postSnapshot in dataSnapshot.children) {

                        val contact = postSnapshot.getValue(Contact::class.java)
                        if (contact != null) {
                            contactList.add(contact)
                        }
                    }
                    Log.d("Tag", contactList.toString())
                    _contacts.value = contactList

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    //Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        } else {
            Log.d("Tag", "Ref is null")
        }
    }

    private fun getTextContacts() {
        if (myRef != null) {
            textContactsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val contactList = mutableListOf<TextContact>()
                    for (postSnapshot in dataSnapshot.children) {

                        val contact = postSnapshot.getValue(TextContact::class.java)
                        if (contact != null) {
                            contactList.add(contact)
                        }
                    }
                    Log.d("Tag", contactList.toString())
                    _textContacts.value = contactList
                }
                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
        } else {
            Log.d("Tag", "Ref is null")
        }
    }

    private fun getAccountInfo() {
        // Read from the database
        if (uiRef != null) {
            uiRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.d("Tak", "Got chane")
                    val newUiInfo = dataSnapshot.getValue(UiInfoModel::class.java)
                    if (newUiInfo != null) {
                        _uiInfo.postValue(newUiInfo)
                        menuMainLink = newUiInfo.menuMainLink
                        menuCatalogueLink = newUiInfo.menuCatalogueLink
                        menuCartLink = newUiInfo.menuCartLink
                        menuMenuLink = newUiInfo.menuMenuLink
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    //Log.w("TAG", "Failed to read value.", error.toException())
                }
            })
        } else {
            Log.d("Tag", "Ui Ref is null")
        }
    }


    fun addToken(new_token: String) {
        myRef.child("tokens").push().setValue(new_token)
        myRef.child("app_id").setValue(app_id)
        myRef.child("secret").setValue(secret)
    }

    //private fun getCurrentUserId(): String = auth.currentUser?.uid.toString()
}