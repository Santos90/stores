package com.simarro.stores.POJOS

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StoreDAO {

	@Query("SELECT * From StoreEntity")
	fun getAllStores() : MutableList<StoreEntity>

	@Insert
	fun addStore(storeEntity : StoreEntity) : Long

	@Update
	fun updateStore (storeEntity: StoreEntity)

	@Delete
	fun deleteStore (storeEntity: StoreEntity)

	@Query("SELECT * From StoreEntity s WHERE s.id = :id ")
	fun getStore(id : Long) : StoreEntity

}