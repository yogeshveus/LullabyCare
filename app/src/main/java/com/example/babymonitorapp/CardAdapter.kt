package com.example.babymonitorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val originalItems: List<CardItem>,
    private val onClick: (CardItem) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var filteredItems: List<CardItem> = originalItems
    private var numTasks: Int = 0

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textViewTitle)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val progressPercent: TextView = itemView.findViewById(R.id.progressPercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = filteredItems[position]

        holder.titleText.text = item.title
        holder.cardView.setOnClickListener {
            onClick(item)
        }

        if (item.title == "Daily tasks") {
            holder.progressBar.visibility = View.VISIBLE
            holder.progressPercent.visibility = View.VISIBLE
            holder.progressBar.progress = numTasks
            holder.progressPercent.text = "${holder.progressBar.progress / 10}/10"
        } else {
            holder.progressBar.visibility = View.GONE
            holder.progressPercent.visibility = View.GONE
        }
    }

    override fun getItemCount() = filteredItems.size

    fun updateTasks(num: Int) {
        numTasks = num
        notifyItemChanged(0)
    }

    fun filter(query: String) {
        filteredItems = if (query.isEmpty()) {
            originalItems
        } else {
            originalItems.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
