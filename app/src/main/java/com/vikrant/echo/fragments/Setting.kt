package com.vikrant.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import com.vikrant.echo.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class Setting : Fragment() {
    var myActivity: Activity? = null
    var shackSwitck: Switch? = null

    object Statisted {
        var My_pref_name = "ShakeFeature"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_setting, container, false)
        activity?.title = "Setting"
        shackSwitck = view?.findViewById(R.id.SwitchSack)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var myPref = myActivity?.getSharedPreferences(Statisted.My_pref_name, Context.MODE_PRIVATE)
        val isAllowed = myPref?.getBoolean("feature", false)
        if (isAllowed as Boolean) {
            shackSwitck?.isChecked = true
        } else {
            shackSwitck?.isChecked = false
        }
        shackSwitck?.setOnCheckedChangeListener({ buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                val editor = myActivity?.getSharedPreferences(Statisted.My_pref_name, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {
                val editor = myActivity?.getSharedPreferences(Statisted.My_pref_name, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item=menu?.findItem(R.id.Sort_icon)
        item?.isVisible=false
        var item2=menu?.findItem(R.id.menuBar)
        item2?.isVisible=false
    }

}
