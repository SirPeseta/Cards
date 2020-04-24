package es.uam.eps.dadm.cards.cardfire

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.Deck

private const val  DATABASENAME = "decks"

class FirebaseLiveData : LiveData<List<Deck>>()  {
    override fun onActive() {
        super.onActive()

        val reference = FirebaseDatabase.getInstance().getReference(DATABASENAME)

        reference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                var listOfCards: MutableList<Deck> = mutableListOf<Deck>()
                for (card in p0.children) {
                    var newCard = card.getValue(Deck::class.java)
                    if (newCard != null)
                        listOfCards.add(newCard)
                }
                value = listOfCards
            }
        })
    }
}