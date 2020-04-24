/***
 * Fichero que contiene el fragmento que muestra la lista de mazos.
 */
package es.uam.eps.dadm.cards

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uam.eps.dadm.cards.cardfire.FirebaseViewModel

//private const val  DATABASENAME = "decks"

/**
 * A simple [Fragment] subclass.
 * Use the [DeckFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeckFragment : Fragment() {
    private lateinit var deckRecyclerView: RecyclerView
    private var adapter: DeckAdapter? = DeckAdapter(emptyList())

    private lateinit var cardListDetailViewModel : CardListDetailViewModel
    private lateinit var deckListViewModel : DeckListViewModel

    private lateinit var fragmentStateViewModel : FragmentStateViewModel
    private val firebaseViewModel : FirebaseViewModel by lazy{
        ViewModelProviders.of(this).get(FirebaseViewModel::class.java)
    }

    interface  onDeckListFragmentInteractionListener{
        fun onDeckSelected(deckId: String)
    }

    var listener : onDeckListFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as onDeckListFragmentInteractionListener?
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_deck, container, false)

        deckRecyclerView = view.findViewById(R.id.deck_recycler_view) as RecyclerView
        deckRecyclerView.layoutManager = LinearLayoutManager(activity)

        fragmentStateViewModel = activity?.run{
            ViewModelProviders.of(this).get(FragmentStateViewModel::class.java)
        } ?: throw Exception("")

        cardListDetailViewModel = activity?.run{
            ViewModelProviders.of(this).get(CardListDetailViewModel::class.java)
        } ?: throw Exception("")

        deckListViewModel = activity?.run{
            ViewModelProviders.of(this).get(DeckListViewModel::class.java)
        } ?: throw Exception("")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val observer = object : Observer<List<Deck>> {
            override fun onChanged(t: List<Deck>?) {
                if (t != null){
                    updateUI(t)
                    Log.d("DeckFragment", "Lista actualizada ${t.size}")
                }
            }
        }

        firebaseViewModel.decks.observe(viewLifecycleOwner, observer)
        /*val reference = FirebaseDatabase.getInstance().getReference(DATABASENAME)
        reference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                var listOfDecks : MutableList<Deck> = mutableListOf<Deck>()

                for(deck in p0.children){
                    var newDeck = deck.getValue(Deck::class.java)
                    if(newDeck != null)
                        listOfDecks.add(newDeck)
                }
                updateUI(listOfDecks)
                Log.d("DeckFragment","Lista actualizada")
            }
        })*/

        /*deckListViewModel.decks.observe(
            viewLifecycleOwner,
            Observer { listOfDecks ->
                listOfDecks.let {
                    updateUI(listOfDecks)
                }
            })*/
    }

    private inner class DeckHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var deck: Deck
        private val deckTextView: TextView = itemView.findViewById(R.id.list_item_deck)

        init {
            itemView.setOnClickListener {
                listener?.onDeckSelected(deck.id)
            }
        }

        fun bind(deck: Deck) {
            this.deck = deck
            deckTextView.text = deck.name
        }
    }

    private inner class DeckAdapter(val decks : List<Deck>) : RecyclerView.Adapter<DeckHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckHolder {
            val view = layoutInflater.inflate(R.layout.list_item_deck, parent, false)
            return DeckHolder(view)
        }

        override fun getItemCount() = decks.size

        override fun onBindViewHolder(holder: DeckHolder, position: Int) {
            holder.bind(decks[position])
        }
    }

    private fun updateUI(decks: List<Deck>) {
        adapter = DeckAdapter(decks)
        deckRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance() = DeckFragment()
    }
}
