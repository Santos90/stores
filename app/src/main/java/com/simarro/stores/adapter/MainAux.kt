package com.simarro.stores.adapter

import com.simarro.stores.POJOS.StoreEntity

interface MainAux {

	fun setVisibilityFAB (isVisible : Boolean)

	fun addStore (storeEntity: StoreEntity)
	abstract fun updateStore(store: StoreEntity)
}