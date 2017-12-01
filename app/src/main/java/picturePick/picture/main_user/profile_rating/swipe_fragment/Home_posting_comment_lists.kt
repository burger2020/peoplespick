package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.instacart.library.truetime.TrueTimeRx
import kotlinx.android.synthetic.main.activity_home_posting_comment_lists.view.*
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.AUTH_NON_PROFILE
import picturePick.picture.main_user.profile_rating.data.post_rater_data
import java.util.*
import android.content.DialogInterface
import com.firebase.ui.storage.images.FirebaseImageLoader
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef


/**
 * Created by main_user on 2017-11-03.
 */
class Home_posting_comment_lists(val context:Context,var comment_data: post_rater_data) : RecyclerView.Adapter<post_comment_Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): post_comment_Viewholder {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.activity_home_posting_comment_lists,parent,false)
        return post_comment_Viewholder(mainView)
    }

    override fun onBindViewHolder(holder: post_comment_Viewholder?, position: Int) {
        holder?.bindHolder(context,comment_data,position)
    }

    override fun getItemCount(): Int = comment_data.comment_data.size
}

class post_comment_Viewholder(view: View): RecyclerView.ViewHolder(view){

    companion object {
        lateinit var mListener: interface_delete_comment
        fun CommetDelete(mListener:interface_delete_comment) { this.mListener = mListener }
    }

    interface interface_delete_comment{
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_delete_comment(comment_position:Int)
    }

    val commenter_profile = view.commenter_profile
    val commenter_name = view.commenter_name
    val comment_time = view.comment_time
    val comment_txt = view. commenter_comment
    val comment_delete_btn = view.comment_delete
    fun bindHolder(context : Context, comment_data: post_rater_data, position: Int){
        commenter_name.text = comment_data.comment_data[position].name
        comment_txt.text = comment_data.comment_data[position].comment

        if(comment_data.comment_data[position].proffile== AUTH_NON_PROFILE)
            Glide.with(context)
                    .load(R.mipmap.ic_non_profile)
                    .thumbnail(0.2F)
                    .into(commenter_profile)
        else
            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(storageRef.child(comment_data.comment_data[position].proffile))
                    .thumbnail(0.2F)
                    .into(commenter_profile)

        val TrueTime = TrueTimeRx.now().time
        //올린시간 ,지난시간 표기 업데이트
        val time_registration : Long =  TrueTime - comment_data.comment_data[position].comment_time
        if(time_registration>=24*60*60*1000*3){ // 등록한지 3일이상
            val date = Date(TrueTime)
            val date_ = Date(comment_data.comment_data[position].comment_time)
            if(date.year == date_.year) // 같은 연도일경우 월.일 만 표시
                comment_time.text = "${""+date_.month+1+context.getString(R.string.rating_posting_month)+date_.date+context.getString(R.string.rating_posting_day)}"
            else
                comment_time.text = "${""+(date_.year+1902)+context.getString(R.string.rating_posting_year)+date_.month+1+context.getString(R.string.rating_posting_month)+date_.date+context.getString(R.string.rating_posting_day)}"
        }else if(time_registration>=24*60*60*1000){ // 등록한지 3일이하 1일이상 (경과 일수 표시)
            //                                              mil  분 시 일
            comment_time.text = "${""+time_registration/1000/60/60/24+context.getString(R.string.rating_posting_before_day)}"
        }else if(time_registration>=60*60*1000){ // 등록한지 1일이하 ( 경과 시간 표시)
            comment_time.text = "${""+time_registration/1000/60/60+context.getString(R.string.rating_posting_before_hour)}"
        }else{ // 등록 1시간 이하 (경과 분 표시)
            comment_time.text = "${""+time_registration/1000/60+context.getString(R.string.rating_posting_before_min)}"
        }
        if(comment_data.comment_data[position].commenter_id == user_info.id)
            comment_delete_btn.visibility = View.VISIBLE
        else
            comment_delete_btn.visibility = View.GONE

        //댓글 삭제
        comment_delete_btn.setOnClickListener({
            mListener.interface_delete_comment(position)
        })
    }
}