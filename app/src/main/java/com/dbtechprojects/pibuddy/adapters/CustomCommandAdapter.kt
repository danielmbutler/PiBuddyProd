package com.dbtechprojects.pibuddy.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dbtechprojects.pibuddy.databinding.ItemCustomButtonBinding
import com.dbtechprojects.pibuddy.models.CustomButton


class CustomCommandAdapter(private val onClickListener: OnCustomCommandClick, private val commands: List<CustomButton>) : RecyclerView.Adapter<CustomCommandAdapter.CustomCommandViewHolder>() {


    class CustomCommandViewHolder(private val binding: ItemCustomButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomButton, onClickListener: OnCustomCommandClick?) {

            binding.itemName.text = item.name

            binding.root.setOnClickListener {
                onClickListener?.onClick(item)
            }

        }



        companion object {
            fun inflateLayout(parent: ViewGroup): CustomCommandViewHolder {
                parent.apply {
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = ItemCustomButtonBinding.inflate(inflater, parent, false)
                    return CustomCommandAdapter.CustomCommandViewHolder(binding)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCommandViewHolder {
        return CustomCommandViewHolder.inflateLayout(parent)
    }

    override fun onBindViewHolder(holder: CustomCommandViewHolder, position: Int) {
        holder.bind(commands[position], onClickListener)
    }

    override fun getItemCount(): Int = commands.size

    interface OnCustomCommandClick {
        fun onClick(item: CustomButton)
    }




}