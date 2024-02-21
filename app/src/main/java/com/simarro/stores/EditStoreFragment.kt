package com.simarro.stores

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.simarro.stores.POJOS.StoreEntity
import com.simarro.stores.database.StoreApplication
import com.simarro.stores.databinding.FragmentEditStoreBinding
import java.util.concurrent.LinkedBlockingDeque


class EditStoreFragment : Fragment() {

	private  lateinit var mBinding : FragmentEditStoreBinding
	private var mActivity:MainActivity? = null
	var editStore : StoreEntity? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
		return mBinding.root
	}




	@SuppressLint("RestrictedApi")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)



		mActivity = activity as? MainActivity

		mActivity?.supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)

		setHasOptionsMenu(true)

		mBinding.etFotoUrl.addTextChangedListener {
			Glide.with(this)
				.load(mBinding.etFotoUrl.text.toString().trim())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.centerCrop()
				.into(mBinding.imgPhoto)
		}

		editStore = (arguments?.getSerializable("store") as StoreEntity?)


		if (editStore != null) {
			mBinding.etName.setText(editStore!!.name)
			mBinding.etWebsite.setText(editStore!!.website)
			mBinding.etPhone.setText(editStore!!.phone)
			mBinding.etFotoUrl.setText(editStore!!.photoUrl)
			mActivity?.supportActionBar?.title = getString(R.string.modificar_tienda)
			/*
			// Utiliza una coroutine para realizar la operación de base de datos en segundo plano
			CoroutineScope(Dispatchers.IO).launch {
				val store: StoreEntity = StoreApplication.database.storeDao().getStore(editStore)

				// Actualiza la interfaz de usuario en el hilo principal
				withContext(Dispatchers.Main) {
					mBinding.etName.setText(store.name)
					mBinding.etWebsite.setText(store.website)
					mBinding.etPhone.setText(store.phone)
					mBinding.etFotoUrl.setText(store.photoUrl)
				}
			}

			 */
		} else mActivity?.supportActionBar?.title = getString(R.string.edit_store_tittle)

	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.menu_save, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {

		return when (item.itemId) {
			android.R.id.home -> {

				true
			}
			android.R.id.home -> {

				mActivity?.onBackPressedDispatcher?.onBackPressed()
				mActivity?.setVisibilityFAB(true)
				true
			}

			R.id.action_save -> {
				val store = StoreEntity(
					name = mBinding.etName.text.toString().trim(),
					phone= mBinding.etPhone.text.toString().trim(),
					website= mBinding.etWebsite.text.toString().trim(),
					photoUrl = mBinding.etFotoUrl.text.toString().trim()
				)

				val cola = LinkedBlockingDeque<Long>()
				Thread {
					if (editStore == null) {
						val id = StoreApplication.database.storeDao().addStore(store)
						store.id = id
						cola.add(id)
					}
					else {
						store.id = editStore!!.id
						StoreApplication.database.storeDao().updateStore(store)
						cola.add(0L)
					}


				}.start()

				with (cola.take()) {
					if (editStore == null) {
						Snackbar.make(
							mBinding.root,
							"Tienda agregada correctamente",
							Snackbar.LENGTH_SHORT
						).show()
						mActivity?.addStore(store)
					} else {
						Snackbar.make(
							mBinding.root,
							"Tienda modificada correctamente",
							Snackbar.LENGTH_SHORT
						).show()
						mActivity?.updateStore(store)
					}
				}
				mActivity?.setVisibilityFAB(true)
				mActivity?.onBackPressedDispatcher?.onBackPressed()

				true
			}
			else -> super.onOptionsItemSelected(item)
		}

	}

	override fun onDestroy() {
		mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false) //Flecha hacia atras
		mActivity?.supportActionBar?.title = getString(R.string.app_name)
		setHasOptionsMenu(false)

		super.onDestroy()
	}




}