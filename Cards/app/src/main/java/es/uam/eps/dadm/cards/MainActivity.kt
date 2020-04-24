/***
 * Fichero que contiene la actividad principal de la
 * aplicación (la única y por la que empieza).
 */
package es.uam.eps.dadm.cards

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import es.uam.eps.dadm.cards.FragmentStates.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DeckFragment.onDeckListFragmentInteractionListener, CardListFragment.onCardListFragmentInteractionListener {
    private val fragmentStateViewModel : FragmentStateViewModel by lazy{
        ViewModelProviders.of(this).get(FragmentStateViewModel::class.java)
    }

    private val cardListDetailViewModel : CardListDetailViewModel by lazy{
        ViewModelProviders.of(this).get(CardListDetailViewModel::class.java)
    }

    private lateinit var decks : List<Deck>

    override fun onDeckSelected(deckId: String) {
        var fragment = CardListFragment.newInstance(deckId)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("DECK")
            .commit()

        cardListDetailViewModel.loadDeck(deckId)
        cardListDetailViewModel.setCurrentDate(0)
        fragmentStateViewModel.changeState(FragmentStates.LIST)
    }

    override fun onCardSelected(cardId: String,deckId: String) {
        val fragment2 = CardFragment.newInstance(deckId,cardId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment2)
            .addToBackStack("LIST")
            .commit()

        fragmentStateViewModel.changeState(FragmentStates.EDIT)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId == R.id.settings)
            startActivity(Intent(this, SettingsActivity::class.java))
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceManager.setDefaultValues(this,R.xml.root_preferences,false)

        /*val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference = database.getReference("mensaje")
        reference.setValue("Adios desde Cards")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Toast.makeText(baseContext,"${p0.value.toString()}",Toast.LENGTH_LONG).show()
            }
        })*/

        /*var button = Button(this)
        button.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                TODO("Not yet implemented")
            }
        })*/

        var fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment == null) {
            fragment = DeckFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val observer = object : Observer<List<Deck>?> {
            override fun onChanged(t: List<Deck>?) {
                if (t != null){
                    changeDecks(t)
                }
            }
        }

        cardListDetailViewModel.decksLiveData.observe(this, observer)

        val fragmentStateObserver = Observer<FragmentStates> {
            when (it) {
                LIST -> {
                    study_button.text = resources.getString(R.string.study_button)
                    study_button.visibility = View.VISIBLE
                    choose_deck_button.visibility = View.VISIBLE
                    details_button.visibility = View.VISIBLE
                    day_button.visibility = View.VISIBLE
                }
                STUDY -> {
                    study_button.text = resources.getString(R.string.end_study_button)
                    study_button.visibility = View.VISIBLE
                    choose_deck_button.visibility = View.INVISIBLE
                    details_button.visibility = View.INVISIBLE
                    day_button.visibility = View.INVISIBLE
                }
                EDIT -> {
                    study_button.text = resources.getString(R.string.back_button)
                    study_button.visibility = View.VISIBLE
                    choose_deck_button.visibility = View.INVISIBLE
                    details_button.visibility = View.INVISIBLE
                    day_button.visibility = View.INVISIBLE
                }
                DECK -> {
                    study_button.visibility = View.INVISIBLE
                    choose_deck_button.visibility = View.INVISIBLE
                    details_button.visibility = View.INVISIBLE
                    day_button.visibility = View.INVISIBLE
                }
                else -> {

                }
            }
        }

        fragmentStateViewModel.getState().observe(this, fragmentStateObserver)

        val dayObserver = Observer<Int> {
            day_button.text = "${resources.getString(R.string.day_text)} ${it.toString()}"
        }
        
        day_button.setOnClickListener { 
            if(fragmentStateViewModel.getState().value == FragmentStates.LIST)
                cardListDetailViewModel.nextDay()
        }

        cardListDetailViewModel.getCurrentDate().observe(this,dayObserver)

        details_button.setOnClickListener {
            cardListDetailViewModel.changeDetailsState()
        }

        val detailsObserver = Observer<Boolean> {
            when(it){
                true -> details_button.text = "- ${resources.getString(R.string.details_button)}"
                false -> details_button.text = "+ ${resources.getString(R.string.details_button)}"
                else -> {

                }
            }
        }

        cardListDetailViewModel.getDetails().observe(this,detailsObserver)

        choose_deck_button.setOnClickListener {
            val fragment2 = DeckFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment2)
                .addToBackStack("LIST")
                .commit()

            fragmentStateViewModel.changeState(DECK)
        }
        study_button.setOnClickListener {
            if(fragmentStateViewModel.getState().value == LIST) {
                /*if(cardListViewModel.currentCard() == null){
                    cardListViewModel.nextDay()
                }*/

                val fragment2 = StudyFragment.newInstance(decks[0])
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment2)
                    .addToBackStack("LIST")
                    .commit()
                study_button.text = resources.getString(R.string.end_study_button)
                fragmentStateViewModel.changeState(STUDY)
            } else if(fragmentStateViewModel.getState().value == STUDY || fragmentStateViewModel.getState().value == EDIT) {
                val fragment3 = CardListFragment.newInstance(cardListDetailViewModel.getCurrentDeckId())
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment3)
                    .addToBackStack("STUDY")
                    .commit()
                study_button.text = resources.getString(R.string.study_button)
                fragmentStateViewModel.changeState(LIST)
            }else if(fragmentStateViewModel.getState().value == EDIT) {
                val fragment3 = CardListFragment.newInstance(cardListDetailViewModel.getCurrentDeckId())
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment3)
                    .addToBackStack("EDIT")
                    .commit()
                study_button.text = resources.getString(R.string.study_button)
                fragmentStateViewModel.changeState(LIST)
            }
        }
    }

    private fun changeDecks(t: List<Deck>) {
        this.decks = t
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && ((event?.repeatCount) ?: 0) == 0){
            if(supportFragmentManager.backStackEntryCount > 0)
                when(supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name){
                    null -> fragmentStateViewModel.changeState(DECK)
                    "LIST" -> fragmentStateViewModel.changeState(LIST)
                    "EDIT" -> fragmentStateViewModel.changeState(EDIT)
                    "DECK" -> fragmentStateViewModel.changeState(DECK)
                    "STUDY" -> fragmentStateViewModel.changeState(STUDY)
                }
        }
        return super.onKeyDown(keyCode, event)
    }
}
