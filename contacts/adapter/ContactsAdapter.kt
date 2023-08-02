package com.buisness.bonuscards.contacts.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.buisness.bonuscards.R
import com.buisness.bonuscards.api.model.Contact
import com.buisness.bonuscards.databinding.ItemChequeBinding
import com.buisness.bonuscards.databinding.ItemContactBinding
import com.buisness.bonuscards.purchase_history.adapter.Cheque
import com.squareup.picasso.Picasso

class ContactsAdapter(private val callUriIntent: (String, String) -> (Unit)) : RecyclerView.Adapter<CheckHolder>() {

    var contacts: List<Contact> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckHolder {
        val itemBinding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckHolder(itemBinding, callUriIntent)
    }

    override fun onBindViewHolder(holder: CheckHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size
}

class CheckHolder(private val itemBinding: ItemContactBinding, val callUriIntent: (String, String) -> (Unit)) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(contact: Contact) {

        if (contact.type == "WhatsApp") {
            itemBinding.imgType.setImageResource(R.drawable.ic_whatsapp)
            itemBinding.txtContact.text = "WhatsApp"
            if (contact.data.isNotEmpty())
                setLinkListener(itemBinding.root, contact.data)
        }
        else if (contact.type == "Telegram") {
            itemBinding.imgType.setImageResource(R.drawable.ic_telegram)
            itemBinding.txtContact.text = "Telegram"
            if (contact.data.isNotEmpty())
                setLinkListener(itemBinding.root, contact.data)
        } else if (contact.type == "Phone") {
            itemBinding.imgType.setImageResource(R.drawable.ic_phone2)
            itemBinding.txtContact.text  = contact.data
            if (contact.data.isNotEmpty())
                setSpecificListener(itemBinding.root, "tel:" + contact.data, Intent.ACTION_DIAL)
        } else if (contact.type == "Email") {
            itemBinding.imgType.setImageResource(R.drawable.ic_phone2)
            itemBinding.txtContact.text  = contact.data
            if (contact.data.isNotEmpty())
                setSpecificListener(itemBinding.root, "mailto:" + contact.data, Intent.ACTION_SENDTO)

        }else {
            itemBinding.imgType.setImageResource(R.drawable.ic_phone2)
            itemBinding.txtContact.text = contact.data

        }
        if (contact.imageUrl.isNotEmpty())
            Picasso.get()
                .load(contact.imageUrl)
                .into(itemBinding.imgType)


    }

    private fun setLinkListener(view: View, link: String) {
        view.setOnClickListener {
            callUriIntent(Intent.ACTION_VIEW,link)
        }
    }

    private fun setSpecificListener(view: View, uri: String, intentType: String) {
        view.setOnClickListener {
            callUriIntent(intentType,uri)
        }
    }


}

