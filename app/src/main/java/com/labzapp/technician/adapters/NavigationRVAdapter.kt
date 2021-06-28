package com.labzapp.technician.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.labzapp.technician.R
import com.labzapp.technician.databinding.RowNavDrawerBinding
import com.labzapp.technician.model.NavigationItemModel

class NavigationRVAdapter(private var items: ArrayList<NavigationItemModel>, private var currentPos: Int) :RecyclerView.Adapter<NavigationRVAdapter.NavigationItemViewHolder>() {

    private lateinit var context: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationItemViewHolder {
        context = parent.context
        val binding = RowNavDrawerBinding.inflate(LayoutInflater.from(context), parent, false)
        return NavigationItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: NavigationItemViewHolder, position: Int) {

        val itempos = items[position]
        holder.setData(itempos, position)

    }

    inner class NavigationItemViewHolder(private val binding: RowNavDrawerBinding) : RecyclerView.ViewHolder(binding.root){


        fun setData(item: NavigationItemModel?, pos: Int) {

            if (pos == currentPos) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            }
            binding.navigationIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            binding.navigationTitle.setTextColor(Color.WHITE)
            //val font = ResourcesCompat.getFont(context, R.font.mycustomfont)
            //holder.itemView.navigation_text.typeface = font
            //holder.itemView.navigation_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.toFloat())

            binding.navigationTitle.text = items[pos].title

            binding.navigationIcon.setImageResource(items[pos].icon)
        }

    }

}