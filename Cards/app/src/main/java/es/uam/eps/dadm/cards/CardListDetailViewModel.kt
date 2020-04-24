package es.uam.eps.dadm.cards

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.cardfire.FirebaseLiveData

private const val  DATABASENAME = "decks"

class CardListDetailViewModel : ViewModel() {
    var decksLiveData = FirebaseLiveData()
    var answered : Boolean = false
    private val deckIdLiveData = MutableLiveData<String>()
    private var currentCard = 0
    private var details : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private var currentDate : MutableLiveData<Int> = MutableLiveData<Int>()

    init {
        currentDate.value = 0
        details.value = false
    }

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

    fun loadDeck(deckId: String) {
        deckIdLiveData.value = deckId
    }

    fun currentCard(deck : Deck) : Card?{
        var tarjetas = deck.cards
        for (card in tarjetas) {
            if (card.nextPracticeDate <= (currentDate.value) ?: 0) {
                currentCard = deck.cards.indexOf(card)
                return card
            }
        }

        return null
    }

    fun changeDetailsState(){
        details.value = details.value!!.not()
    }

    fun getDetails() = details

    fun nextDay() {
        currentDate.value = (currentDate.value ?: 0) + 1
    }

    fun getCurrentDate() = currentDate

    fun setCurrentDate(date: Int){
        currentDate.value = date
    }

    fun nextCardSimulation(decks : MutableList<Deck>,deck : Deck,difficulty : Int) : Card?{
        deck.cards[currentCard].quality = difficulty
        deck.cards[currentCard].currentDate = (currentDate.value ?: 0)
        deck.cards[currentCard].update()
        for (i in 0 until decks.size) {
            if (decks[i].id == deck.id) {
                decks[i] = deck
            }
        }
        saveData(decks)

        return currentCard(deck)
    }

    fun getCurrentDeckId() : String {
        return deckIdLiveData.value ?: ""
    }

    fun setCurrentCard(deck : Deck,cardId: String) {
        for(i in 0 until deck.cards.size){
            if(deck.cards[i].id == cardId) {
                currentCard = i
                return
            }
        }
        currentCard = -1
    }

    fun saveData(decks : List<Deck>){
        val reference = FirebaseDatabase.getInstance().getReference(DATABASENAME)
        reference.setValue(decks)
    }
}