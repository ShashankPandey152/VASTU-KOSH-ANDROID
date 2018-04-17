package com.vastukosh.client.android

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class AdsAdapter(val context: Context, val ad: List<Ads>, val itemClick: (Ads) -> Unit): RecyclerView.Adapter<AdsAdapter.Holder>() {

    override fun onBindViewHolder(holder: Holder?, position: Int) {
        holder?.bindAd(ad[position], context)
        val advert = ad[position]
        Glide.with(context).load(advert.image).into(holder?.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_ad, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return ad.count()
    }

    inner class Holder(itemView: View?, val itemClick: (Ads) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val productName = itemView?.findViewById<TextView>(R.id.productName)
        val productPrice = itemView?.findViewById<TextView>(R.id.productPrice)
        val productImage = itemView?.findViewById<ImageView>(R.id.productImage)

        fun bindAd(ads: Ads, context: Context) {
            productName?.text = ads.name
            productPrice?.text = ads.price
            itemView.setOnClickListener { itemClick(ads) }
        }
    }
}