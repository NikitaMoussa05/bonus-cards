package com.buisness.bonuscards.shops.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buisness.bonuscards.databinding.ItemCityShopsBinding
import com.buisness.bonuscards.databinding.ItemShopBinding

class ShopsAdapter(private val openUrl: (String) -> (Unit)) : RecyclerView.Adapter<ShopsHolder>() {

    var contacts: List<CityWithShops> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemCityShopsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopsHolder(itemBinding, openUrl, inflater)
    }

    override fun onBindViewHolder(holder: ShopsHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size
}

class ShopsHolder(private val itemBinding: ItemCityShopsBinding, val openUrl: (String) -> (Unit), val inflater: LayoutInflater) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(city: CityWithShops) {
        itemBinding.txtCityName.text = city.cityName
        for (shop in city.shops) {
            val shopBinding = ItemShopBinding.inflate(inflater)
            shopBinding.txtStreet.text = shop.address
            shopBinding.txtDescription.text = shop.description
            itemBinding.layoutShops.addView(shopBinding.root)
        }

    }

}

