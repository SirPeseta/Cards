package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import es.uam.eps.dadm.cards.Card

@Dao
interface CardDao {
    @Query("SELECT * FROM card")
    fun getCards(): LiveData<List<Card>>

    @Query("SELECT * FROM card WHERE id == (:idReq)")
    fun getCard(idReq: String): LiveData<Card?>
}