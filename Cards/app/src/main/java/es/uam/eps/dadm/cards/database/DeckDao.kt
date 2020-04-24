package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.*
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.Deck
import es.uam.eps.dadm.cards.DeckTypeConverters

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck")
    fun getDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM deck WHERE id == (:idReq)")
    fun getDeck(idReq: String): LiveData<Deck?>

    @Insert
    fun addDeck(deck: Deck)

    @Update
    fun updateDeck(deck: Deck)

    @Query("UPDATE deck SET cards = (:cardsReq) WHERE id == (:idReq)")
    fun updateCards(idReq: String,@TypeConverters(DeckTypeConverters::class)cardsReq : MutableList<Card>)
}