package com.simarro.stores

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.simarro.stores.POJOS.StoreEntity
import com.simarro.stores.adapter.MainAux
import com.simarro.stores.adapter.OnClickListener
import com.simarro.stores.adapter.StoreAdapter
import com.simarro.stores.database.StoreApplication
import com.simarro.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingDeque

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {
	private lateinit var binding: ActivityMainBinding
	private lateinit var mAdapter: StoreAdapter
	private lateinit var mGridLayout: GridLayoutManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setupRecyclerView()


		binding.fab.setOnClickListener {
			launchEditFragment()
		}
	}

	private fun launchEditFragment(args : Bundle? = null) {
		val fragment = EditStoreFragment()

		if (args != null) fragment.arguments = args

		supportFragmentManager.beginTransaction()
			.add(R.id.containerMain, fragment)
			.addToBackStack(null)
			.commit()

		setVisibilityFAB(false)
	}

	private fun setupRecyclerView() {
		mAdapter = StoreAdapter(mutableListOf(), this)
		mGridLayout = GridLayoutManager(this, 2)
		getStores()
		binding.recycledView.apply {
			setHasFixedSize(true)
			layoutManager = mGridLayout
			adapter = mAdapter
		}
	}

	private fun getStores() {
		val cola = LinkedBlockingDeque<MutableList<StoreEntity>>()
		Thread {
			val stores = StoreApplication.database.storeDao().getAllStores()
			cola.add(stores)

		}.start()
		mAdapter.setStores(cola.take())
	}

	override fun onClick(obj: Any?) {
		var args = Bundle()
		args.putSerializable("store", obj as StoreEntity )
		launchEditFragment(args)
	}

	override fun onFavouriteStore(obj: Any?) {
		obj as StoreEntity
		obj.isFavorite = ! obj.isFavorite

		val cola = LinkedBlockingDeque<StoreEntity>()
		Thread {
			StoreApplication.database.storeDao().updateStore(obj)
			cola.add(obj)
		}.start()
		mAdapter.update(cola.take())
	}

	override fun onDEleteStore(obj: Any?) {
		obj as StoreEntity

		val cola = LinkedBlockingDeque<StoreEntity>()
		Thread {
			StoreApplication.database.storeDao().deleteStore(obj)
			cola.add(obj)
		}.start()
		mAdapter.delete(cola.take())
	}

	override fun setVisibilityFAB(isVisible: Boolean) {
		if (isVisible)
			binding.fab.show()
		else
			binding.fab.hide()
	}

	override fun addStore(storeEntity: StoreEntity) {
		mAdapter.add(storeEntity)
	}

	override fun updateStore(store: StoreEntity) {
		mAdapter.update(store)
	}

}
