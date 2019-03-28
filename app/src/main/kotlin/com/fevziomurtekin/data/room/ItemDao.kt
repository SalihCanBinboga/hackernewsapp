package com.fevziomurtekin.com.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fevziomurtekin.com.data.model.ItemModel

@Dao
interface ItemDao{


    /**
     * Save all items
     * */
    @Insert
    fun saveAll(entities:MutableList<ItemEntity>?)

    /**
     * Find ItemEntity for text
     * @return MutableList<ItemEntity>
     * */
    @Query("SELECT * FROM item WHERE text LIKE :text OR title LIKE :text")
    fun findAllBy(text:String):MutableList<ItemEntity>

    /**
     * Find ItemEntity for id
     * @return ItemEntity
     * */
    @Query("SELECT * FROM item WHERE id= :id")
    fun findAllById(id: String):ItemEntity

}