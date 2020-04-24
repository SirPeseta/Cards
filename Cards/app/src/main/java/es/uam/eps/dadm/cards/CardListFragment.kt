/***
 * Fichero que contiene la clase del fragmento de la lista de tarjetas.
 */
package es.uam.eps.dadm.cards

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_card_list.*


/**
 * A simple [Fragment] subclass.
 * Use the [CardListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class CardListFragment : Fragment() {
    private lateinit var cardRecyclerView: RecyclerView
    private lateinit var adapter: CardAdapter
    private lateinit var deckId : String
    private lateinit var deck : Deck
    private lateinit var decks : MutableList<Deck>

    private lateinit var fragmentStateViewModel : FragmentStateViewModel

    private val cardListDetailViewModel: CardListDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CardListDetailViewModel::class.java)
    }
    private val cardDetailViewModel: CardDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CardDetailViewModel::class.java)
    }
    private lateinit var cardListDetailViewModel2: CardListDetailViewModel


    interface  onCardListFragmentInteractionListener{
        fun onCardSelected(cardId: String,deckId: String)
    }

    var listener : onCardListFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as onCardListFragmentInteractionListener?
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
        deckId = arguments?.getString(ARG_DECK_ID).toString()
        cardListDetailViewModel.loadDeck(deckId)
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_card_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_card -> {
                cardDetailViewModel.addCard(deck,Card())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_card_list, container, false)

        Log.d("Hola","${SettingsActivity.getMaximumNumberOfCards(context!!)}")
        SettingsActivity.setMaximumNumberOfCards(context!!,"50")
        cardRecyclerView = view.findViewById(R.id.card_recycler_view) as RecyclerView
        cardRecyclerView.layoutManager = LinearLayoutManager(activity)

        fragmentStateViewModel = activity?.run{
            ViewModelProviders.of(this).get(FragmentStateViewModel::class.java)
        } ?: throw Exception("")

        Log.d("Hola","${SettingsActivity.getMaximumNumberOfCards(context!!)}")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        new_card_fab.setOnClickListener {
            cardDetailViewModel.addCard(decks,deck,Card(question="Pregunta",answer="Respuesta"))
            Snackbar.make(view,"Tarjeta añadida",Snackbar.LENGTH_SHORT).show()
        }
        cardListDetailViewModel.deckLiveData.observe(
            viewLifecycleOwner,
            Observer { listOfCards ->
                listOfCards?.let {
                    this.deck = listOfCards
                    updateUI(listOfCards.cards)
                }
            })
        cardListDetailViewModel.decksLiveData.observe(
            viewLifecycleOwner,
            Observer { listOfDecks ->
                listOfDecks?.let {
                    this.decks = listOfDecks.toMutableList()
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

    private inner class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var card: Card
        private val questionTextView: TextView = itemView.findViewById(R.id.list_item_question)
        private val answerTextView:TextView = itemView.findViewById(R.id.list_item_answer)
        private val dateTextView: TextView = itemView.findViewById(R.id.list_item_date)
        private val easiness: TextView = itemView.findViewById(R.id.list_item_easiness)
        private val repetition: TextView = itemView.findViewById(R.id.list_item_repetition)
        private val interval: TextView = itemView.findViewById(R.id.list_item_interval)
        private val nextDate: TextView = itemView.findViewById(R.id.list_item_next)
        private val detailsLayout: LinearLayout = itemView.findViewById(R.id.details_layout)

        init {
            itemView.setOnClickListener {
                listener?.onCardSelected(card.id,deckId)
            }
            val detailsObserver = Observer<Boolean> {
                when(it){
                    true -> {
                        detailsLayout.visibility = View.VISIBLE
                    }
                    false -> {
                        detailsLayout.visibility = View.INVISIBLE
                    }
                    else -> {

                    }
                }
            }

            activity?.let {
                cardListDetailViewModel2 = activity?.run{
                    ViewModelProviders.of(this).get(cardListDetailViewModel::class.java)
                } ?: throw Exception("")
                cardListDetailViewModel2.getDetails().observe(it,detailsObserver)
            }
        }

        fun bind(card: Card) {
            this.card = card
            questionTextView.text = card.question
            answerTextView.text = card.answer
            dateTextView.text = card.date.substring(0,13)
            easiness.text = "${resources.getString(R.string.easiness_text)} = " + "%.3f".format(card.easiness)
            repetition.text = "${resources.getString(R.string.repetition_text)} = ${card.repetitions}"
            interval.text = "${resources.getString(R.string.interval_text)} = ${card.interval}"
            nextDate.text = "${resources.getString(R.string.nextDate_text)} = ${card.nextPracticeDate}"
        }
    }

    private inner class CardAdapter(val cards : List<Card>) : RecyclerView.Adapter<CardHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
            val view = layoutInflater.inflate(R.layout.list_item_card, parent, false)
            return CardHolder(view)
        }

        override fun getItemCount() = cards.size

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            holder.bind(cards[position])
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentStateViewModel = activity?.run{
            ViewModelProviders.of(this).get(FragmentStateViewModel::class.java)
        } ?: throw Exception("")
    }

    private fun updateUI(cards: List<Card>) {
        adapter = CardAdapter(cards)
        cardRecyclerView.adapter = adapter
    }

    companion object {
        private const val ARG_DECK_ID = "deck_uuid"
        fun newInstance(id : String): CardListFragment {
            val args = Bundle()
            args.putString(ARG_DECK_ID,id)

            return CardListFragment().apply {
                arguments = args
            }
        }
    }
}

