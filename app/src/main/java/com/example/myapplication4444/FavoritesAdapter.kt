import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication4444.R
import com.example.myapplication4444.model.Place

class FavoritesAdapter(
    private var favorites: List<Place>,
    private val onFavoriteToggle: (Place) -> Unit,
    private val onSelect: (Place, Boolean) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view, onFavoriteToggle, onSelect)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int = favorites.size

    class FavoriteViewHolder(
        itemView: View,
        private val onFavoriteToggle: (Place) -> Unit,
        private val onSelect: (Place, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(place: Place) {
            itemView.findViewById<TextView>(R.id.place_name).text = place.name
            Glide.with(itemView.context).load(place.imageUrl).into(itemView.findViewById<ImageView>(R.id.place_image))
            itemView.findViewById<CheckBox>(R.id.checkbox_select).apply {
                isChecked = place.isSelected
                setOnCheckedChangeListener { _, isChecked ->
                    onSelect(place, isChecked)
                    place.isSelected = isChecked
                }
            }
            itemView.findViewById<ImageView>(R.id.favorite_icon).setOnClickListener {
                onFavoriteToggle(place)
            }
        }
    }

    fun updateFavorites(newFavorites: List<Place>) {
        this.favorites = newFavorites
        notifyDataSetChanged()
    }
}



