/***
 * Fichero que contiene la clase mazo (Deck) necesaria para la
 * ejecución del programa (back-end).
 */
package es.uam.eps.dadm.cards

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable
import java.util.*

@Entity
@TypeConverters(DeckTypeConverters::class)
class Deck(@PrimaryKey var id: String = UUID.randomUUID().toString(),var name : String = "", @TypeConverters(DeckTypeConverters::class) var cards : MutableList<Card> = mutableListOf<Card>()) : Serializable {
    companion object {
        private const val serialVersionUID = 20180617104400L
    }
    fun addCard(card: Card){
        cards.add(card)
    }
    fun getCardById(idCard: String) : Card?{
        for(i in 0 until cards.size){
            if(idCard == cards[i].id){
                return cards[i]
            }
        }

        return null
    }

    fun getCardIndexById(id : String) : Int{
        for(i in 0..cards.size){
            if(id == cards[i].id){
                return i
            }
        }
        return -1
    }

    fun getCardIndexByIdLiveData(id : String) : Card?{
        var data = MutableLiveData<Card>()
        for(i in 0..cards.size){
            if(id == cards[i].id){
                return cards[i]
            }
        }
        return null
    }
}