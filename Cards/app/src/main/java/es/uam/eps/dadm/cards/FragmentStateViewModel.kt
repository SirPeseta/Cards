/***
 * Clase que permite controlar qué fragmento está activo en la actividad.
 */
package es.uam.eps.dadm.cards

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/***
 * Clase enumerada con los diferentes estados de fragmentos existentes.
 */
enum class FragmentStates {
    LIST,STUDY,EDIT,DECK
}

class FragmentStateViewModel : ViewModel() {
    private var state = MutableLiveData<FragmentStates>()

    init {
        state.value = FragmentStates.DECK
    }

    fun changeState(stateIn : FragmentStates) {
        state.value = stateIn
    }

    fun getState() = state
}