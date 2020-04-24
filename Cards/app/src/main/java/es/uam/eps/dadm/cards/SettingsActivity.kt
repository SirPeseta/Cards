package es.uam.eps.dadm.cards

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    companion object {
        val MAXIMUM_KEY = "maximum_number"
        val MAXIMUM_DEFAULT = "20"

        fun getMaximumNumberOfCards(context: Context) : String{
            return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(MAXIMUM_KEY, MAXIMUM_DEFAULT) ?: ""
        }

        fun setMaximumNumberOfCards(context: Context,max : String){
            val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = sharedPreferences.edit()
            editor.putString(SettingsActivity.MAXIMUM_KEY,max)
            editor.commit()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //PreferenceManager.setDefaultValues(this,R.xml.root_preferences,false)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}