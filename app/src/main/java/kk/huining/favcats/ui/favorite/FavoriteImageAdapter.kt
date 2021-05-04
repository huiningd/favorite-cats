package kk.huining.favcats.ui.favorite

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kk.huining.favcats.R
import kk.huining.favcats.data.model.Favorite
import kk.huining.favcats.databinding.ListItemFavoriteBinding
import timber.log.Timber


class FavoriteImageAdapter(
    private val context: Context,
    private val imageClickListener: FavImageOnClickListener,
    private val unfavClickListener: UnfavoriteOnClickListener,
): ListAdapter<Favorite, FavoriteImageAdapter.ViewHolder>(FavImageDiffCallback()) {

    private var dataSet: MutableList<Favorite> = mutableListOf()

    override fun submitList(list: MutableList<Favorite>?) {
        super.submitList(list)
        if (list != null) dataSet = list
    }

    fun updateDataset(itemRemovedPos: Int) {
        dataSet.removeAt(itemRemovedPos)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder.from(parent)
        viewHolder.binding.favoriteImage.setOnClickListener { _ ->
            val clickedPos = viewHolder.adapterPosition
            Timber.d("clicked on item %d", clickedPos)
            val clickedItem = dataSet[clickedPos]
            imageClickListener.onClick(clickedItem)
        }
        viewHolder.binding.removeFavoriteButton.setOnClickListener { _ ->
            val clickedPos = viewHolder.adapterPosition
            Timber.d("clicked on item %d", clickedPos)
            val clickedItem = dataSet[clickedPos]
            unfavClickListener.onClick(clickedItem, clickedPos)
        }
        return viewHolder
    }

    class ViewHolder private constructor(val binding: ListItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Favorite, context: Context) {
            // Timber.d("binding ${item.id}")
            item.image?.url?.let {
                Glide.with(context)
                    .load(it)
                    .centerCrop()
                    .placeholder(R.drawable.ic_pets_24)
                    .into(binding.favoriteImage)
            }
            // TODO get sub_id, created date
            //binding.breedName = if (item.breeds.isNotEmpty()) item.breeds[0].name
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemFavoriteBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class FavImageDiffCallback : DiffUtil.ItemCallback<Favorite>() {
    override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem == newItem
    }
}

class FavImageOnClickListener(val clickListener: (fav: Favorite) -> Unit) {
    fun onClick(fav: Favorite) = clickListener(fav)
}

class UnfavoriteOnClickListener(val clickListener: (fav: Favorite, position: Int) -> Unit) {
    fun onClick(fav: Favorite, position: Int) = clickListener(fav, position)
}