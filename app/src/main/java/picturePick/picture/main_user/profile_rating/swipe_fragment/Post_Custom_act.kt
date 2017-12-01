package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.toolbar_edit.*
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_frag_second
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.POST_CUSTOM_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.POST_UPDATE_DATA

class Post_Custom_act : AppCompatActivity(),View.OnClickListener {
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.edit_toolbar_close->{
                finish()
            }
        }
    }

    companion object {
        lateinit var mListener: interface_custom_post_set
        fun customComplete(mListener:interface_custom_post_set) { this.mListener = mListener }
    }

    interface interface_custom_post_set {
        fun interface_custom_post_set(post_Main_Data: Post_Main_Data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post__custom_act)

        val intent = intent

        val post_main_data : Post_Main_Data = (intent.getSerializableExtra(POST_CUSTOM_DATA) as Post_Main_Data?)!!

        val actionBar : ActionBar? = supportActionBar
        actionBar?.elevation = 0F
        actionBar?.setDisplayShowCustomEnabled(true);
        actionBar?.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar?.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar?.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionbar = inflater.inflate(R.layout.toolbar_edit, null)
        actionBar?.customView = actionbar

        edit_toolbar_close.setOnClickListener(this)
        edit_toolbar_next.text = getText(R.string.edit_tool_produce)
        edit_toolbar_next.setOnClickListener({
            mListener.interface_custom_post_set(post_main_data)
        })

        var fragment: Fragment =  Edit_frag_second.customInstance(post_main_data,true)
        var fragmentManager: FragmentManager = supportFragmentManager
        var tramsaction = fragmentManager.beginTransaction()
        tramsaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        tramsaction?.replace(R.id.custom_frag_container, fragment)?.commit()

        Edit_frag_second.CustomFinish(object : Edit_frag_second.interface_custom_finish{
            override fun interface_custom_finish(post_Main_Data: Post_Main_Data) {
                val intent = Intent()
                intent.putExtra(POST_UPDATE_DATA,post_Main_Data)
                setResult(123,intent)
                finish()
            }
        })

    }
}
