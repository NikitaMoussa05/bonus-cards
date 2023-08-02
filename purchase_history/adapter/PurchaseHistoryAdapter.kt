package com.buisness.bonuscards.purchase_history.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buisness.bonuscards.databinding.ItemChequeBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PurchaseHistoryAdapter() : RecyclerView.Adapter<CheckHolder>() {

    private var cheques: MutableList<Cheque> = mutableListOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckHolder {
        val itemBinding = ItemChequeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: CheckHolder, position: Int) {
        val cheque = cheques[position]
        holder.bind(cheque)
    }

    override fun getItemCount(): Int = cheques.size

    fun addCheque(cheque: Cheque) {
        if (!cheques.contains(cheque)) {
            cheques.add(cheque)
            cheques.sortBy { curr ->
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                val datetime = LocalDateTime.parse(curr.date, formatter)
                datetime
            }
            notifyDataSetChanged()
        }
    }
}

class CheckHolder(private val itemBinding: ItemChequeBinding) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(cheque: Cheque) {
        itemBinding.txtBonusesAmount.text = cheque.bonuses.toString()
        itemBinding.txtChequeSumAmount.text = cheque.purchase_sum.toString()
        val chequeDate = cheque.date ?: ""
        itemBinding.txtDate.text = chequeDate.toString()
    }

}