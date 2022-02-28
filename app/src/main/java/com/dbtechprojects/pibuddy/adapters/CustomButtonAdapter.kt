package com.dbtechprojects.pibuddy.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dbtechprojects.pibuddy.databinding.ItemCustomButtonResultBinding
import com.dbtechprojects.pibuddy.models.CustomButton


class CustomButtonAdapter(private val onClickListener: OnCustomButtonClick, private val deleteListener: OnCustomButtonDelete,private val commands: MutableList<CustomButton>?) : RecyclerView.Adapter<CustomButtonAdapter.CustomCommandViewHolder>() {


    class CustomCommandViewHolder(private val binding: ItemCustomButtonResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomButton, onClickListener: OnCustomButtonClick?, deleteListener: OnCustomButtonDelete) {

            binding.itemName.text = item.name

            binding.root.setOnClickListener {
                onClickListener?.onClick(item)
            }
            binding.root.setOnLongClickListener {
                val builder = AlertDialog.Builder(binding.root.context)
                builder.setMessage("Are you sure you want to delete this custom button")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        deleteListener.onDelete(item)
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
                true
            }

        }



        companion object {
            fun inflateLayout(parent: ViewGroup): CustomCommandViewHolder {
                parent.apply {
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = ItemCustomButtonResultBinding.inflate(inflater, parent, false)
                    return CustomButtonAdapter.CustomCommandViewHolder(binding)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCommandViewHolder {
        return CustomCommandViewHolder.inflateLayout(parent)
    }

    override fun onBindViewHolder(holder: CustomCommandViewHolder, position: Int) {
        holder.bind(commands?.get(position)!!, onClickListener, deleteListener)
    }

    override fun getItemCount(): Int = commands!!.size

    interface OnCustomButtonClick {
        fun onClick(item: CustomButton)
    }

    interface OnCustomButtonDelete {
        fun onDelete(item: CustomButton)
    }





}