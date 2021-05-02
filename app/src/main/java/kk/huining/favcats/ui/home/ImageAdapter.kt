package kk.huining.favcats.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
        viewHolder.binding.favoriteButton.setOnClickListener {
            val clickedPos = viewHolder.adapterPosition
            Timber.d("clicked on favoriteIcon %d", clickedPos)
            val clickedItem = dataSet[clickedPos]
            favIconOnClickListener.onClick(clickedItem, clickedPos)
        }
        return viewHolder
    }

    class ViewHolder private constructor(
        val binding: GridItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Image, context: Context) {
            Timber.e("binding ${item.id} isFavorite ${item.isFavorite}")
            Glide.with(context)
                .load(item.url)
                .centerCrop()
                .placeholder(R.drawable.ic_pets_24)
                .into(binding.gridImage)
            if (item.breeds.isNotEmpty()) {
                binding.breedName = item.breeds[0].name
            }
            if (item.isFavorite) {
                binding.favoriteButton.setBackgroundResource(R.drawable.ic_favorite_24)
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
        //Timber.e("oldItem $oldItem, newItem $newItem")
        //Timber.e("areContentsTheSame ${oldItem == newItem}")
        return oldItem == newItem
    }
}

class ImageOnClickListener(val clickListener: (image: Image, clickedView: View) -> Unit) {
    fun onClick(image: Image, clickedView: View) = clickListener(image, clickedView)
}

class FavIconOnClickListener(val clickListener: (image: Image, clickedPosition: Int) -> Unit) {
    fun onClick(image: Image, clickedPosition: Int) = clickListener(image, clickedPosition)
}

