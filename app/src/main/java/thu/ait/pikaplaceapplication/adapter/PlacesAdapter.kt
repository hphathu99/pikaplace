package thu.ait.pikaplaceapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_place.view.*
import thu.ait.pikaplaceapplication.R
import thu.ait.pikaplaceapplication.data.PlaceItem

class PlacesAdapter : RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private val context: Context

    val placesItems: MutableList<PlaceItem> = mutableListOf<PlaceItem>()

    constructor(context: Context) : super() {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.card_place, parent, false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = placesItems[position]

        holder.tvName.text = currentItem.name
        holder.tvAddress.text = currentItem.address
        holder.tvType.text = currentItem.type
        holder.tvDistance.text = currentItem.distance
        holder.imgType.setImageResource(
            when(currentItem.type) {
                "Coffee" -> R.drawable.coffee
                "Restaurant" -> R.drawable.restaurant
                "Arts,Entertainment" -> R.drawable.museum
                "Bar" -> R.drawable.pub
                "Outdoors" -> R.drawable.park
                else -> R.drawable.ic_launcher_background
            }
        )
    }

    override fun getItemCount(): Int {
        return placesItems.size
    }


    public fun addItem(places: PlaceItem) {
        placesItems.add(places)
        notifyItemInserted(placesItems.lastIndex)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName = itemView.tvName
        val tvAddress = itemView.tvAddress
        val tvType = itemView.tvType
        val tvDistance = itemView.tvDistance
        val imgType = itemView.imgType
    }

}

