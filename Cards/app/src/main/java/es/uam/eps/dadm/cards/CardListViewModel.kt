/***
 * Fichero que contiene la clase del ViewModel de la lista de tarjetas.
 */
package es.uam.eps.dadm.cards

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardListViewModel: ViewModel() {

    var cards = MutableLiveData<MutableList<Card>>()
    var answered : Boolean = false
    var editData : Boolean = false
    private var indexCardFragment = 0
    private var currentDate : MutableLiveData<Int> = MutableLiveData<Int>()
    private var currentCard = 0
    private val cardRepository = CardRepository.get()
    private var details : MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        currentDate.value = 0
        details.value = false
        cards.value = mutableListOf<Card>()
    }

    fun currentCard() : Card?{
        for(card in cards.value!!){
            if(card.nextPracticeDate <= (currentDate.value) ?: 0){
                currentCard = cards.value!!.indexOf(card)
                return card
            }
        }

        return null
    }

    fun currentCardEdit() : Card{
        return if(indexCardFragment >= cards.value!!.size || indexCardFragment < 0){
            Card()
        }else {
            cards.value!![indexCardFragment]
        }
    }

    fun changeDetailsState(){
        details.value = details.value!!.not()
    }

    fun getDetails() = details

    fun setEdit(){
        editData = true
    }

    fun setCreate(){
        editData = false
    }

    fun nextDay() {
        currentDate.value = (currentDate.value ?: 0) + 1
    }

    fun getCurrentDate() = currentDate

    fun setCurrentDate(date: Int){
        currentDate.value = date
    }

    fun nextCardSimulation(difficulty : Int) : Card?{
        cards.value!![currentCard].quality = difficulty
        cards.value!![currentCard].currentDate = (currentDate.value ?: 0)
        cards.value!![currentCard].update()
        return currentCard()
    }

    fun setCurrentCard(cardId: String) {
        for(i in 0..(cards.value?.size ?: 0)){
            if(cards.value!![i].id == cardId) {
                currentCard = i
                return
            }
        }
        currentCard = -1
    }

    fun saveCurrentCard(card : Card,newCard : Card){
        val indice = cards.value!!.indexOf(card)
        if(indice >= 0 && indice < cards.value!!.size){
            cards.value!![indice].answer = newCard.answer
            cards.value!![indice].question = newCard.question
        }
    }

    fun deleteCurrentCard(card: Card){
        cards.value!!.remove(card)
    }

    fun addCard(card : Card){
        cards.value!!.add(card)
    }
}