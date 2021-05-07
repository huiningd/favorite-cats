package kk.huining.favcats.ui.upload

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kk.huining.favcats.R
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.databinding.ListItemMyUploadBinding
import kk.huining.favcats.utils.getFormattedDateTimeInLocal
import timber.log.Timber


class MyUploadedImageAdapter(
    private val context: Context
): ListAdapter<Image, MyUploadedImageAdapter.ViewHolder>(MyUploadedImageDiffCallback()) {

    private var dataSet: MutableList<Image> = mutableListOf()

    override fun submitList(list: MutableList<Image>?) {
        super.submitList(list)
        if (list != null) dataSet = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder.from(parent)
        // TODO add on click listener
        return viewHolder
    }

    class ViewHolder private constructor(val binding: ListItemMyUploadBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Image, context: Context) {
            // Timber.d("binding ${item.id}")
            item.url?.let {
                Glide.with(context)
                    .load(it)
                    .centerCrop()
                    .placeholder(R.drawable.ic_pets_24)
                    .into(binding.myUploadImage)
            }
            binding.createdTime = "created at ${getFormattedDateTimeInLocal(item.created_at)}"
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMyUploadBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class MyUploadedImageDiffCallback : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}
