package kk.huining.favcats.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kk.huining.favcats.R
import kk.huining.favcats.data.model.Image
import kk.huining.favcats.databinding.GridItemImageBinding
import timber.log.Timber


class ImageAdapter(
    private val context: Context,
    private val imageOnClickListener: ImageOnClickListener,
    private val favIconOnClickListener: FavIconOnClickListener
) : ListAdapter<Image, ImageAdapter.ViewHolder>(ImageDiffCallback()) {

    private var dataSet: List<Image> = emptyList()

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
        viewHolder.binding.gridImage.setOnClickListener {
            val clickedPos = viewHolder.adapterPosition
            Timber.d("clicked on image %d", clickedPos)
            val clickedItem = dataSet[clickedPos]
            imageOnClickListener.onClick(clickedItem, it)
        }
        viewHolder.binding.favoriteIcon.setOnClickListener {
            val clickedPos = viewHolder.adapterPosition
            Timber.d("clicked on favoriteIcon %d", clickedPos)
            val clickedItem = dataSet[clickedPos]
            //toggleFavoriteIcon()
            favIconOnClickListener.onClick(clickedItem, it)
        }
        return viewHolder
    }

    private fun toggleFavoriteIcon() {
        // TODO: save isFav, reload this icon
        // Or save isFav in Fragment (edit dataset), onPause write to DB?
    }

    class ViewHolder private constructor(
        val binding: GridItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Image, context: Context) {
            Glide.with(context)
                .load(item.url)
                .centerCrop()
                .placeholder(R.drawable.ic_pets_24)
                .into(binding.gridImage)
            if (item.isFavorite) {
                binding.favoriteIcon.setImageResource(R.drawable.ic_favorite_24)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GridItemImageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ImageDiffCallback : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}

class ImageOnClickListener(val clickListener: (image: Image, clickedView: View) -> Unit) {
    fun onClick(image: Image, clickedView: View) = clickListener(image, clickedView)
}

class FavIconOnClickListener(val clickListener: (image: Image, clickedView: View) -> Unit) {
    fun onClick(image: Image, clickedView: View) = clickListener(image, clickedView)
}

