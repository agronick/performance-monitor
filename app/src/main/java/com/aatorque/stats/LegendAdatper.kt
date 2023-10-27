package com.aatorque.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.aatorque.stats.databinding.ChartLegendBinding
import com.google.common.collect.ImmutableList

class LegendAdapter(var items: ImmutableList<TorqueChart.LegendBinding>): RecyclerView.Adapter<LegendAdapter.ViewHolder>() {

    class ViewHolder(var binding: ChartLegendBinding, itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: TorqueChart.LegendBinding) {
            binding.binding = data
            // make sure to include this so your view will be updated
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ChartLegendBinding.inflate(layoutInflater, parent, false)
                binding.lifecycleOwner = parent.findViewTreeLifecycleOwner()
                return ViewHolder(binding, binding.root)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = items[position] // this will be the list object you created
        holder.bind(todo)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}