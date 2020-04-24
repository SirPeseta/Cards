/***
 * Fichero que contiene la clase del fragmento donde se estudian las tarjetas.
 */
package es.uam.eps.dadm.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

/**
 * A simple [Fragment] subclass.
 * Use the [StudyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudyFragment : Fragment() {
    private lateinit var questionText : TextView
    private lateinit var deckId : String
    private lateinit var answerButton : Button
    private lateinit var difficultyButtons: LinearLayout
    private lateinit var difficultButton : Button
    private lateinit var doubtButton : Button
    private lateinit var easyButton : Button
    private lateinit var decks : MutableList<Deck>
    private var card : Card? = null
    private lateinit var deck : Deck
    private lateinit var cardListDetailViewModel: CardListDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deck = arguments?.getSerializable(ARG_DECK) as Deck
        /*cardListDetailViewModel.loadDeck(deckId)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study, container, false)

        questionText = view.findViewById(R.id.question_text_view)
        answerButton = view.findViewById(R.id.answer_button)
        difficultyButtons = view.findViewById(R.id.difficulty_buttons)

        difficultButton = view.findViewById(R.id.difficult_button)
        doubtButton = view.findViewById(R.id.doubt_button)
        easyButton = view.findViewById(R.id.easy_button)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cardListDetailViewModel = activity?.run{
            ViewModelProviders.of(this).get(CardListDetailViewModel::class.java)
        } ?: throw Exception("")

        cardListDetailViewModel.decksLiveData.observe(
            viewLifecycleOwner,
            Observer { decksLive ->
                decksLive.let {
                    this.decks = decksLive.toMutableList()
                }
            })

        card = cardListDetailViewModel.currentCard(deck)

        if(card == null){
            answerButton.visibility = View.INVISIBLE
            difficultyButtons.visibility = View.INVISIBLE
            questionText.text = resources.getString(R.string.not_available)
        }else {
            answerButton.setOnClickListener {
                cardListDetailViewModel.answered = true
                questionText.text = (card?.answer) ?: resources.getString(R.string.not_available)
                answerButton.visibility = View.INVISIBLE
                difficultyButtons.visibility = View.VISIBLE
            }

            if (cardListDetailViewModel.answered) {
                questionText.text = (card?.answer) ?: resources.getString(R.string.not_available)
                answerButton.visibility = View.INVISIBLE
                difficultyButtons.visibility = View.VISIBLE
            } else {
                questionText.text = (card?.question) ?: resources.getString(R.string.not_available)
                answerButton.visibility = View.VISIBLE
                difficultyButtons.visibility = View.INVISIBLE
            }

            difficultButton.setOnClickListener {
                card = cardListDetailViewModel.nextCardSimulation(decks,deck,0)
                if(card == null){
                    cardListDetailViewModel.answered = false
                    answerButton.visibility = View.INVISIBLE
                    difficultyButtons.visibility = View.INVISIBLE
                    questionText.text = resources.getString(R.string.no_study)
                }else {
                    cardListDetailViewModel.answered = false
                    questionText.text = (card?.question) ?: resources.getString(R.string.not_available)
                    answerButton.visibility = View.VISIBLE
                    difficultyButtons.visibility = View.INVISIBLE
                }
            }

            doubtButton.setOnClickListener {
                card = cardListDetailViewModel.nextCardSimulation(decks,deck,3)
                if(card == null){
                    cardListDetailViewModel.answered = false
                    answerButton.visibility = View.INVISIBLE
                    difficultyButtons.visibility = View.INVISIBLE
                    questionText.text = resources.getString(R.string.no_study)
                }else {
                    cardListDetailViewModel.answered = false
                    questionText.text = (card?.question) ?: resources.getString(R.string.not_available)
                    answerButton.visibility = View.VISIBLE
                    difficultyButtons.visibility = View.INVISIBLE
                }
            }

            easyButton.setOnClickListener {
                card = cardListDetailViewModel.nextCardSimulation(decks,deck,5)
                if(card == null){
                    cardListDetailViewModel.answered = false
                    answerButton.visibility = View.INVISIBLE
                    difficultyButtons.visibility = View.INVISIBLE
                    questionText.text = resources.getString(R.string.no_study)
                }else {
                    cardListDetailViewModel.answered = false
                    questionText.text = (card?.question) ?: resources.getString(R.string.not_available)
                    answerButton.visibility = View.VISIBLE
                    difficultyButtons.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(card == null){
            questionText.text = resources.getString(R.string.no_study)
        }
    }

    companion object {
        private const val ARG_DECK = "deck"
        fun newInstance(deck : Deck): StudyFragment {
            val args = Bundle()
            args.putSerializable(ARG_DECK,deck)

            return StudyFragment().apply {
                arguments = args
            }
        }
    }
}
