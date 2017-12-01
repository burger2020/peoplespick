package picturePick.picture.main_user.profile_rating.Auth

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_auth_frag_sign_up.view.*
import picturePick.picture.main_user.profile_rating.Auth.Auth_MainActivity.Companion.progress_bar
import picturePick.picture.main_user.profile_rating.R

class Auth_frag_sign_up : Fragment() {

    companion object {
        var google_sign_up_bool = false
        fun newInstance(google_sign_up_bool  : Boolean): Auth_frag_sign_up{
            val fragment = Auth_frag_sign_up()

            this.google_sign_up_bool = google_sign_up_bool

            return fragment
        }

        lateinit var mListener: interface_sign_up_complete
        fun interface_sign_up_complete(mListener:interface_sign_up_complete) { this.mListener = mListener }
    }

    interface interface_sign_up_complete {
        fun interface_sign_up_complete(uemail:String,uname:String,upass:String)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_auth_frag_sign_up, container, false)

        if(google_sign_up_bool){
            rootView.auth_custom_sign_ip_email_txt.visibility = View.GONE
            rootView.auth_custom_sign_ip_pass_txt.visibility = View.GONE
            rootView.auth_custom_sign_ip_repass_txt.visibility = View.GONE
            rootView.disname_comment.visibility = View.VISIBLE
        }else{
            rootView.disname_comment.visibility = View.GONE
        }
        rootView.custom_complete_btn.setOnClickListener({
            progress_bar.visibility = View.VISIBLE
            val uemail : String = rootView.auth_custom_sign_ip_email_txt.text.toString()
            val uname : String = rootView.auth_custom_sign_ip_name_txt.text.toString()
            val upass : String = rootView.auth_custom_sign_ip_pass_txt.text.toString()
            val urepass : String = rootView.auth_custom_sign_ip_repass_txt.text.toString()

            if(!google_sign_up_bool){
                when {
                    uemail.length<=7 -> {
                        Toast.makeText(context,getString(R.string.auth_custom_sign_up_email_short), Toast.LENGTH_SHORT).show()
                        progress_bar.visibility = View.GONE
                    }
                    uname.length<=2 -> {
                        Toast.makeText(context,getString(R.string.auth_custom_sign_up_name_short), Toast.LENGTH_SHORT).show()
                        progress_bar.visibility = View.GONE
                    }
                    upass.length <6 -> {
                        Toast.makeText(context,getString(R.string.auth_custom_sign_up_pass_shoet), Toast.LENGTH_SHORT).show()
                        progress_bar.visibility = View.GONE
                    }
                    upass != urepass -> {
                        Toast.makeText(context,getString(R.string.auth_custom_sign_up_pass_dif), Toast.LENGTH_SHORT).show()
                        progress_bar.visibility = View.GONE
                    }
                    else -> mListener.interface_sign_up_complete(uemail, uname, upass)
                }
            }else{
                if(uname.length<=2){
                    Toast.makeText(context,getString(R.string.auth_custom_sign_up_name_short), Toast.LENGTH_SHORT).show()
                    progress_bar.visibility = View.GONE
                }
                else {
                    mListener.interface_sign_up_complete("non", uname, "non")
                }
            }
        })

        return rootView
    }
}
