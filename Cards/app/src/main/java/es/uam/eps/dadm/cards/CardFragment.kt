/***
 * Fichero que contiene la clase del fragmento de edición de la tarjeta.
 */
package es.uam.eps.dadm.cards

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_card.*


/**
 * A simple [Fragment] subclass.
 * Use the [CardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var card: Card
    private lateinit var currentCard : Card
    private lateinit var deck : Deck
    private lateinit var decks : MutableList<Deck>
    private lateinit var deckId : String
    private lateinit var cardId : String
    private lateinit var fragmentStateViewModel : FragmentStateViewModel
    private val cardDetailViewModel: CardDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CardDetailViewModel::class.java)
    }
    private val cardListDetailViewModel : CardListDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CardListDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        card = Card()
        cardId = arguments?.getString(ARG_CARD_ID) ?: throw Exception("Fragment's id not found in argument")
        deckId = arguments?.getString(ARG_DECK_ID) ?: throw Exception("Fragment's id not found in argument")
        cardListDetailViewModel.loadDeck(deckId)
        cardDetailViewModel.loadCard(cardId,deckId)
        if(cardId != ""){
            card.id = cardId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    override fun onStart() {
        super.onStart()

        card.question = question_edit_text.text.toString()
        card.answer = answer_edit_text.text.toString()

        val questionTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.question = s.toString()
            }
        }

        question_edit_text.addTextChangedListener(questionTextWatcher)


        val answerTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                card.answer = s.toString()
            }
        }

        answer_edit_text.addTextChangedListener(answerTextWatcher)


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentStateViewModel = activity?.run{
            ViewModelProviders.of(this).get(FragmentStateViewModel::class.java)
        } ?: throw Exception("")

        if(cardId != ""){
            /*currentCard = cardListViewModel.currentCardEdit()
            question_edit_text.setText(currentCard.question)
            answer_edit_text.setText(currentCard.answer)*/

            uuid_label_text_view.text = "TARJETA ${card.id.takeWhile { it != '-' }.toUpperCase()}"
            /*date_text_view.text = currentCard.date.substringBeforeLast(':').substringBeforeLast(':')*/

            delete_button.visibility = View.VISIBLE
            delete_button.setOnClickListener {
                var fragment = CardListFragment.newInstance(deckId)

                cardDetailViewModel.deleteCurrentCard(decks,deck,currentCard)

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)?.addToBackStack("EDIT")?.commit()
                fragmentStateViewModel.changeState(FragmentStates.LIST)
            }
            save_button.setOnClickListener {
                var fragment = CardListFragment.newInstance(deckId)

                //cardDetailViewModel.updateCard(card)
                cardDetailViewModel.updateCard(decks,deck,card)

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)?.addToBackStack("EDIT")?.commit()
                fragmentStateViewModel.changeState(FragmentStates.LIST)
            }
        }else{
            var mensaje = Toast.makeText(activity,"La tarjeta debe tener los dos campos completos.",Toast.LENGTH_SHORT)

            uuid_label_text_view.text = "TARJETA ${card.id.takeWhile { it != '-' }.toUpperCase()}"
            date_text_view.text = card.date.substringBeforeLast(':').substringBeforeLast(':')

            delete_button.visibility = View.INVISIBLE
            save_button.setOnClickListener {
                if(card.question == "" || card.answer == ""){
                    mensaje.show()
                }else {
                    var fragment = CardListFragment.newInstance(deckId)
                    //cardDetailViewModel.addCard(card)

                    cardDetailViewModel.addCard(decks,deck,card)

                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, fragment)?.addToBackStack("EDIT")
                        ?.commit()
                    fragmentStateViewModel.changeState(FragmentStates.LIST)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardListDetailViewModel.deckLiveData.observe(
            viewLifecycleOwner,
            Observer { deck ->
                deck.let {
                    updateUI(deck ?: Deck(name="Nuevo",cards = mutableListOf()))
                }
            })
        cardListDetailViewModel.decksLiveData.observe(
            viewLifecycleOwner,
            Observer { decksLive ->
                decksLive.let {
                    this.decks = decksLive.toMutableList()
                }
            })
    }

    private fun updateUI(deck : Deck){
        this.deck = deck
        currentCard = deck.getCardById(cardId) ?: Card()
        card.question = currentCard.question
        card.answer = currentCard.answer
        card.currentDate = currentCard.currentDate
        card.easiness = currentCard.easiness
        card.nextPracticeDate = currentCard.nextPracticeDate
        card.interval = currentCard.interval
        card.quality = currentCard.quality
        card.date = currentCard.date
        card.repetitions = currentCard.repetitions

        question_edit_text.setText(currentCard.question)
        answer_edit_text.setText(currentCard.answer)
        date_text_view.setText(currentCard.date)
    }

    companion object {
        private const val ARG_CARD_ID = "card_uuid"
        private const val ARG_DECK_ID = "deck_uuid"
        fun newInstance(idDeck: String,idCard : String) : CardFragment {
            val args = Bundle()
            args.putString(ARG_CARD_ID,idCard)
            args.putString(ARG_DECK_ID,idDeck)

            return CardFragment().apply {
                arguments = args
            }
        }
    }
}
