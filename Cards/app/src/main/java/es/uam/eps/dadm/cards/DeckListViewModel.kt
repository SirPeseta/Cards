/***
 * Fichero que contiene el ViewModel de los mazos de cartas con sus cartas que utiliza
 * el DeckFragment.
 */
package es.uam.eps.dadm.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class DeckListViewModel : ViewModel() {
    private var currentDeckLiveData = MutableLiveData<String>()
    private val cardRepository = CardRepository.get()
    val decks = cardRepository.getDecks()
    var deckLiveData: LiveData<Deck?> =
        Transformations.switchMap(currentDeckLiveData) { deckId -> cardRepository.getDeck(deckId) }

    fun setCurrentDeck(deckId: String) {
        currentDeckLiveData.value = deckId
    }

    fun getCurrentDeck() = currentDeckLiveData.value
}
