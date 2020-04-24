package es.uam.eps.dadm.cards.database

import androidx.room.Database
import androidx.room.RoomDatabase
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.Deck

@Database (entities = [ Deck::class,Card::class ], version = 1,exportSchema = false)
abstract class CardDatabase: RoomDatabase() {
    abstract fun cardDao() : CardDao
    abstract fun deckDao() : DeckDao
}