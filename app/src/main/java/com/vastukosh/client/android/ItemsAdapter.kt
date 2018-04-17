package com.vastukosh.client.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ItemsAdapter(val context: Context, val item: List<Items>, val itemClick: (Items) -> Unit): RecyclerView.Adapter<ItemsAdapter.ItemHolder>() {

    override fun onBindViewHolder(holder: ItemHolder?, position: Int) {
        holder?.bindAd(item[position], context)
    }

    override fun getItemCount(): Int {
        return item.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false)
        return ItemHolder(view, itemClick)
    }

    inner class ItemHolder(itemView: View?, val itemClick: (Items) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val iidIname = itemView?.findViewById<TextView>(R.id.iidIname)

        @SuppressLint("SetTextI18n")
        fun bindAd(items: Items, context: Context) {
            iidIname?.text = items.iname + " : " + items.iid
            iidIname?.paintFlags = iidIname?.paintFlags!! or Paint.UNDERLINE_TEXT_FLAG
            itemView.setOnClickListener { itemClick(items) }
        }
    }
}