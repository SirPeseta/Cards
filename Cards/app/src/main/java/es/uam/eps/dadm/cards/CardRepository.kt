package es.uam.eps.dadm.cards

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import es.uam.eps.dadm.cards.database.CardDatabase
import java.util.concurrent.Executors

private const val DATABASE_NAME: String = "card-database"

class CardRepository private constructor(context: Context){
    private val database: CardDatabase = Room.databaseBuilder(
        context.applicationContext,
        CardDatabase::class.java,
        DATABASE_NAME
    ).build()
    private val deckDao = database.deckDao()
    private val cardDao = database.cardDao()

    private val executor = Executors.newSingleThreadExecutor()

    fun getDecks(): LiveData<List<Deck>> = deckDao.getDecks()

    fun getDeck(id: String): LiveData<Deck?>  = deckDao.getDeck(id)


    fun addDeck(deck: Deck){
        executor.execute {
            deckDao.addDeck(deck)
        }
    }

    fun addCard(id: String,card: Card){
        executor.execute {
            var deck = deckDao.getDeck(id).value

            if (deck != null) {
                deck.addCard(card)
                deckDao.updateDeck(deck)
                Log.d("Hola","Añadida")
            }else{
                Log.d("Hola","No añadida")
            }

        }
    }

    fun getCard(cardId: String, deckId: String): LiveData<Card?> {
        var deckLive : LiveData<Deck?>
        var deck: Deck
        var retorno = MutableLiveData<Card>()
            deckLive = deckDao.getDeck(deckId)
                deck = deckLive.value!!
                retorno.value = deck.getCardById(cardId)
                Log.d("Hola","Adios")


        return retorno
    }

    fun updateDeck(deck: Deck){
        executor.execute {
            deckDao.updateDeck(deck)
        }
    }

    companion object {
        private var INSTANCE: CardRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null)
                INSTANCE = CardRepository(context)
        }

        fun get(): CardRepository {
            return INSTANCE ?: throw IllegalStateException("CardRepository isn't initialized")
        }
    }
}