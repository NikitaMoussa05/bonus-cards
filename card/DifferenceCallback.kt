package com.buisness.bonuscards.card

import androidx.recyclerview.widget.DiffUtil
import com.buisness.bonuscards.api.model.CardInfoBonus

/**
 * This is a DiffUtil.ItemCallback for our adapters, nothing special :)
 * */
class DifferenceCallback : DiffUtil.ItemCallback<CardInfoBonus>() {
  override fun areItemsTheSame(oldItem: CardInfoBonus, newItem: CardInfoBonus): Boolean = oldItem.id == newItem.id
  override fun areContentsTheSame(oldItem: CardInfoBonus, newItem: CardInfoBonus): Boolean = oldItem.id  == newItem.id
}
