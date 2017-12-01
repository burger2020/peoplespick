package picturePick.picture.main_user.profile_rating.swipe_fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.post_rater_data


/**
 * A simple [Fragment] subclass.
 */
class Home_rating_frag : Fragment() {

    companion object {
        var post_main_data : Post_Main_Data = Post_Main_Data()
        var post_rater_data = post_rater_data()
        var rating_checked = 0
        fun newInstance(post_main_data : Post_Main_Data,post_rater_data : post_rater_data, rating_checked : Int): Home_rating_frag {
            val fragment = Home_rating_frag()

            this.post_main_data = post_main_data
            this.post_rater_data = post_rater_data
            this.rating_checked = rating_checked

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater!!.inflate(R.layout.fragment_home_rating_frag, container, false)



        return rootView
    }

}// Required empty public constructor
