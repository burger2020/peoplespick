package com.example.main_user.profile_rating.bottom_menu_activity

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.main_user.profile_rating.R
import com.example.main_user.profile_rating.swipe_fragment.Home_frag

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Search_frag.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Search_frag.newInstance] factory method to
 * create an instance of this fragment.
 */
class Search_frag : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_search_frag, container, false)
    }
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(): Search_frag{
            val fragment = Search_frag()
            return fragment
        }
    }
}// Required empty public constructor