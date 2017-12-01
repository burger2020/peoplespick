package picturePick.picture.main_user.profile_rating.bottom_menu_activity

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import picturePick.picture.main_user.profile_rating.R

class Alarm_frag : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_alarm_frag, container, false)
    }
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        fun newInstance(): Alarm_frag {
            val fragment = Alarm_frag()
            return fragment
        }
    }
}// Required empty public constructor