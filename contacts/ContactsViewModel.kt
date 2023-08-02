package com.buisness.bonuscards.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.buisness.bonuscards.api.model.Contact
import com.buisness.bonuscards.api.model.TextContact
import com.buisness.bonuscards.api.model.UiInfoModel
import com.buisness.bonuscards.api.repository.UserAccountRepository

class ContactsViewModel(
    private val repository: UserAccountRepository
): ViewModel() {

    val contacts: LiveData<List<Contact>> = repository.contacts

    val text_contacts: LiveData<List<TextContact>> = repository.textContacts

    private val userInfoRepository = UserAccountRepository()

    val uiInfo: LiveData<UiInfoModel> = userInfoRepository.uiInfo

}