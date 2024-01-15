package com.simarro.stores.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.simarro.stores.R
import com.simarro.stores.POJOS.StoreEntity
import com.simarro.stores.databinding.ItemStoreBinding


class StoreAdapter(private var lista: MutableList<StoreEntity>,
                   private val listener: OnClickListener)
                        : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view) //Vinculamos la vista a nuestro adapter

        fun setListener(store : StoreEntity){
            binding.root.setOnClickListener {
                listener.onClick(store)
            }
            binding.root.setOnLongClickListener {
                listener.onDEleteStore(store)
                true
            }

            binding.cbFavorite.setOnClickListener {
                listener.onFavouriteStore(store)
            }
        }


    }

    private lateinit var mContext: Context

    // El layout manager invoca este método para renderizar cada elemento del RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //Inflar la vista item_tarea en el Recycler
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { //Asignamos el contenido a cada item del Layout Item.xml
        val store = lista[position] as StoreEntity

        with(holder){
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite

            Glide.with(mContext)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)

        }


    }

    // Este método devolverá el tamaño de la lista
    override fun getItemCount(): Int = lista.size
    fun add(store: StoreEntity) {
        lista.add(store)
        notifyItemInserted(lista.size-1) //Refresca la vista del adaptador
    }

    fun setStores(stores: MutableList<StoreEntity>) {
        lista = stores
        notifyDataSetChanged()
    }

    fun update(obj: StoreEntity) {
        val store = obj as StoreEntity
        val index = lista.indexOf(store)

        if (index != -1) {
            lista.set(index, store)
            notifyItemChanged(index)

        }
    }

    fun delete(obj: StoreEntity) {

        val index = lista.indexOf(obj)

        if (index != -1) {
            lista.removeAt(index)
            notifyItemRemoved(index)
        }
    }


}