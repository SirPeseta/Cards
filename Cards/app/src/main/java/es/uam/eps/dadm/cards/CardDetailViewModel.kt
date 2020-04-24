package es.uam.eps.dadm.cards

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.cardfire.FirebaseLiveData

private const val  DATABASENAME = "decks"

class CardDetailViewModel : ViewModel() {
    var decksLiveData = FirebaseLiveData()
    private val cardIdLiveData = MutableLiveData<String>()
    private val deckIdLiveData = MutableLiveData<String>()

    var deckLiveData: MutableLiveData<Deck?> = MutableLiveData<Deck?>()
        private set
        get(){
            FirebaseDatabase.getInstance().getReference(DATABASENAME)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        var deck : Deck? = null
                        for (deckk in p0.children) {
                            var newDeck = deckk.getValue(Deck::class.java)
                            if (newDeck?.id == deckIdLiveData.value)
                                deck = newDeck
                        }
                        field.postValue(deck)
                    }
                })
            return field
        }

        //Transformations.switchMap(deckIdLiveData) { deckId -> FirebaseDatabase.getInstance().getReference(DATABASENAME).child(deckId) }

    fun loadCard(cardId: String,deckId: String) {
        cardIdLiveData.value = cardId
        deckIdLiveData.value = deckId
    }

    fun addCard(decks : MutableList<Deck>,deck : Deck,card: Card) {
        deck.addCard(card)
        for (i in 0 until decks.size) {
            if (decks[i].id == deck.id) {
                decks[i] = deck
            }
        }
        saveData(decks)
    }

    fun updateCard(decks : MutableList<Deck>,deck: Deck,card: Card) {
        var index = deck.getCardIndexById(card.id)

        if(index >= 0) {
            deck.cards[index] = card
            for (i in 0 until decks.size) {
                if (decks[i].id == deck.id) {
                    decks[i] = deck
                }
            }
            saveData(decks)
        }

    }

    fun deleteCurrentCard(decks : MutableList<Deck>,deck: Deck,card : Card){
        var index = deck.getCardIndexById(card.id)

        if(index >= 0) {
            deck.cards.removeAt(index)
            for (i in 0 until decks.size) {
                if (decks[i].id == deck.id) {
                    decks[i] = deck
                }
            }
            saveData(decks)
        }
    }

    fun saveData(decks : List<Deck>){
        val reference = FirebaseDatabase.getInstance().getReference(DATABASENAME)
        reference.setValue(decks)
    }
}