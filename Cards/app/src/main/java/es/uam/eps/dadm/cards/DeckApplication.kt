package es.uam.eps.dadm.cards

import android.app.Application

class DeckApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CardRepository.initialize(this)
        val cardRepository = CardRepository.get()

        /*val database: FirebaseDatabase = FirebaseDatabase.getInstance()

        var card1 = Card(question="pregunta 1",answer="respuesta 1")
        var card2 = Card(question="pregunta 2",answer="respuesta 2")
        var card3 = Card(question="pregunta 3",answer="respuesta 3")

        var decks = mutableListOf<Deck>()

        decks.add(Deck(name="Mazo 1",cards= mutableListOf<Card>(card1,card2,card3)))
        decks.add(Deck(name="Mazo 2",cards= mutableListOf<Card>(card1,card2,card3)))
        decks.add(Deck(name="Mazo 3",cards= mutableListOf<Card>(card1,card2,card3)))
        val reference = database.getReference("decks")
        reference.setValue(decks)*/
        /*var newRef = reference.child("cards").push()
        newRef.setValue(deck.cards)*/

        //Log.d("Hola","${cardRepository.getDecks()}")
    }
}