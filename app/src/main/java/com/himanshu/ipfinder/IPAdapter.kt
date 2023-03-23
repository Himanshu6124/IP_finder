package com.himanshu.ipfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IPAdapter(private val mList: List<Model>) : RecyclerView.Adapter<IPAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.revlayout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        holder.ipAdd.text = ItemsViewModel.ipAddress
        holder.ipTime.text = ItemsViewModel.time


        // sets the image to the imageview from our itemHolder class
//        holder.te.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
//        holder.textView.text = ItemsViewModel.text

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ipAdd: TextView = itemView.findViewById(R.id.textView)
        val ipTime: TextView = itemView.findViewById(R.id.textView2)
    }
}