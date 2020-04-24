package es.uam.eps.dadm.cards.cardfire

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.Deck

private const val DATABASENAME = "decks"

class FirebaseViewModel : ViewModel() {
    var decks: MutableLiveData<List<Deck>> = MutableLiveData()
        private set
        get() {
            //if (field.value == null) {
                FirebaseDatabase.getInstance().getReference(DATABASENAME)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(p0: DataSnapshot) {
                            var listOfDecks: MutableList<Deck> = mutableListOf<Deck>()
                            Log.d("Hola","${p0.children.toString()}")
                            for (deck in p0.children) {
                                Log.d("Hola","${deck}")
                                var newDeck = deck.getValue(Deck::class.java)
                                if (newDeck != null)
                                    listOfDecks.add(newDeck)
                            }
                            field.postValue(listOfDecks)
                        }
                    })
            //}
            return field
        }

    fun getDeck(deckId: String) : Deck?{
        for(deck in decks.value!!){
            if(deck.id == deckId){
                return deck
            }
        }
        return null
    }
}