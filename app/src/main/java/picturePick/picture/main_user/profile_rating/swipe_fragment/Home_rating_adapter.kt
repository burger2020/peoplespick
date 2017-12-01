package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON


/**
 * Created by main_user on 2017-10-28.
 */

class Home_rating_adapter(val context:Context, val post_data:Post_Main_Data,val photo_select : ArrayList<Boolean>,
                          val rate_result : ArrayList<Int>,val rate_position : Int,val post_rating_position : Int) : BaseAdapter() {
//    companion object {
//        lateinit var mListener: interface_view_image
//        lateinit var mListener2: interface_PhotoCliked
//        fun ImageClicked(mListener:interface_view_image) { this.mListener = mListener }
//        fun PhotoClicked(mListener2:interface_PhotoCliked) { this.mListener2 = mListener2 }
//    }
//
//    interface interface_view_image {
//        // image 눌렀을 경우
//        fun interface_view_image(photo_url: ArrayList<String>,photo_like_state : ArrayList<Boolean>,capture_option: Boolean,rate_position:Int, photo_position:Int)
//    }
//    interface interface_PhotoCliked{
//        // 사진 클릭시 액티비티로 눌른 포지션 하트 클릭됐는지 인터페이스
//        fun interface_photo_clicked(position:Int,is_select:Boolean)
//    }


    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        val holder: ViewHolder
        var mainView: View? = null
        val anim: Animation = AnimationUtils.loadAnimation(context.applicationContext, (R.anim.heart_anim))
        if (mainView == null) {
            var is_select: Boolean
            var select_photo_position = 0
            var click_time = System.currentTimeMillis()
//            val storageRef_ : StorageReference = FirebaseStorage.getInstance("gs://pictures-pick/").reference
            val non_time = 700
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mainView = inflater.inflate(R.layout.list_home_rating, parent, false)
            holder = ViewHolder()

            holder.rating_photo = mainView.findViewById(R.id.rating_photo)

            holder.rating_photo_like_rating = mainView.findViewById(R.id.rating_photo_like_rater)
            holder.rating_photo_text_rating = mainView.findViewById(R.id.rating_photo_like_num)
            holder.rating_photo_like = mainView.findViewById(R.id.rating_photo_like)

            holder.rating_photo_enlargement = mainView.findViewById(R.id.rating_photo_enlargement)

            if (rate_position!= HOME_RATING_NON) {//평가 했을떄
                holder.rating_photo_like?.visibility = View.GONE
                holder.rating_photo_like_rating?.visibility = View.VISIBLE
                holder.rating_photo_text_rating?.visibility = View.VISIBLE
            } else {// 평가 안했을경우
                holder.rating_photo_like?.visibility = View.VISIBLE
                holder.rating_photo_like_rating?.visibility = View.GONE
                holder.rating_photo_text_rating?.visibility = View.GONE
            }

            //투표 사진만 붉은색
            when {
                post_data.photo_url.size < 3 -> //사진 2장
                    holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2, context.resources.displayMetrics.heightPixels / 2)
                post_data.photo_url.size < 5 -> // 사진 4장이하
                    holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2, context.resources.displayMetrics.heightPixels / 10*4)
                post_data.photo_url.size >= 5 -> // 사진 5장이상
                    holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2, context.resources.displayMetrics.heightPixels / 9 * 3)
            }

            if (rate_position!= HOME_RATING_NON) {
                //평가했을경우 하트 붉은색으로 바꾸기
                holder.rating_photo_like_rating?.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 8, context.resources.displayMetrics.widthPixels / 2 / 8)
                holder.rating_photo_text_rating?.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 8, context.resources.displayMetrics.widthPixels / 2 / 8)

                //투표 사진만 붉은색
                if(post_rating_position == position)
                    holder.rating_photo_like_rating?.setImageResource(R.mipmap.ic_home_rating_heart)
                else
                    holder.rating_photo_like_rating?.setImageResource(R.mipmap.ic_home_rating_heart_non)
                holder.rating_photo_like_rating?.animation = anim
                holder.rating_photo_like_rating?.startAnimation(anim)

                holder.rating_photo_enlargement?.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 7, context.resources.displayMetrics.widthPixels / 2 / 7)
            } else {
                //평가 안했을시 하트,배경 리사이징
                holder.rating_photo_enlargement?.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 7, context.resources.displayMetrics.widthPixels / 2 / 7)
                holder.rating_photo_like?.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 8, context.resources.displayMetrics.widthPixels / 2 / 8)
            }

            //TODO 하트 눌렀을경우
            holder.rating_photo_like?.setOnClickListener({
                if (rate_position!= HOME_RATING_NON) {
                } else {//평가 안했을경우
                    if (photo_select[position]) {  // 하트 누른거 취소
                        photo_select[position] = false
                        is_select = false
                        notifyDataSetChanged()
                    } else {                        // 하트 생성
                        for (i in 0 until post_data.photo_url.size)
                            photo_select[i] = false
                        photo_select[position] = true
                        is_select = true
                        select_photo_position = position
                        notifyDataSetChanged()
                    }
//                    mListener2.interface_photo_clicked(select_photo_position,is_select)
                }
            })
            //TODO 사진 클릭시
            holder.rating_photo?.setOnClickListener({
                if (rate_position!= HOME_RATING_NON) {
                } else {// 평가 안했을경우
                    if (photo_select[position]) { // 하트 취소
                        photo_select[position] = false
                        is_select = false
                        notifyDataSetChanged()
                    } else {                       //하트 생성
                        for (i in 0 until post_data.photo_url.size)
                            photo_select[i] = false
                        photo_select[position] = true
                        is_select = true
                        select_photo_position = position
                        notifyDataSetChanged()
                    }
//                    mListener2.interface_photo_clicked(select_photo_position,is_select)
                }
            })
            //TODO 사진 롱클릭 했을경우
            holder.rating_photo?.setOnLongClickListener({
                if (click_time + non_time < System.currentTimeMillis()) {
//                    mListener.interface_view_image(post_data.photo_url, photo_select, post_data.option_info_data.captuer_bool, rate_position,position)
                }
                return@setOnLongClickListener true
            })

            holder.rating_photo_enlargement?.setOnClickListener({
                if (click_time + non_time < System.currentTimeMillis()) {
//                    mListener.interface_view_image(post_data.photo_url, photo_select, post_data.option_info_data.captuer_bool, rate_position,position)
                }
            })
//            holder.rating_photo_text_rating?.text = ""+rate_result[position]
            //TODO 사진 목록 넣기
//            Glide.with(context).using(FirebaseImageLoader()).load(storageRef_.child(post_data.photo_url[position])).centerCrop().thumbnail(0.2f)
//                    .into(holder.rating_photo)
//            if(position!=0)
            Glide.with(context)
                    .load((post_data.photo_url[position]))
                    .centerCrop()
                    .thumbnail(0.2f)
                    .into(holder.rating_photo)
        } else {
            holder = view?.tag as ViewHolder
        }

        //TODO 사진 클릭상태 확인후 하트 이미지 변경
        if (rate_position== HOME_RATING_NON) // 평가 안했을 경우
        {
            if (photo_select[position]) {
                holder.rating_photo_like?.setImageResource(R.mipmap.ic_home_rating_heart)
                holder.rating_photo_like?.animation = anim
                holder.rating_photo_like?.startAnimation(anim)
            } else {
                holder.rating_photo_like_rating?.setImageResource(R.mipmap.ic_home_rating_heart_non)
            }
        }
        else{//평가 했을시
            holder.rating_photo_like?.visibility =View.GONE
            holder.rating_photo_like_rating?.visibility = View.VISIBLE
            holder.rating_photo_text_rating?.visibility = View.VISIBLE
            holder.rating_photo_like_rating = mainView!!.findViewById(R.id.rating_photo_like_rater)
            holder.rating_photo_text_rating= mainView.findViewById(R.id.rating_photo_like_num)

            val params = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 7, context.resources.displayMetrics.widthPixels / 2 / 7)
            params.rightMargin = 10

            holder.rating_photo_like_rating?.layoutParams = params

            holder.rating_photo_text_rating?.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 8, context.resources.displayMetrics.widthPixels / 2 / 8)

//            holder.rating_photo_like_rating?.setBackgroundResource(R.mipmap.ic_home_rating_heart)
//            holder.rating_photo_like_rating?.animation = anim
//            holder.rating_photo_like_rating?.startAnimation(anim)

            holder.rating_photo_text_rating?.text = ""+rate_result[position].toLong()
            when{
                rate_result[position]>=1000->{
                    holder.rating_photo_text_rating?.textSize = context.resources.getDimension(R.dimen.result_text_size_1000)
                }
                rate_result[position]>=100->{
                    holder.rating_photo_text_rating?.textSize = context.resources.getDimension(R.dimen.result_text_size_100)
                }
                rate_result[position]>=10->{
                    holder.rating_photo_text_rating?.textSize = context.resources.getDimension(R.dimen.result_text_size_10)
                }
                else->{
                    holder.rating_photo_text_rating?.textSize = context.resources.getDimension(R.dimen.result_text_size_1)
                }
            }
        }
        return mainView
    }

    internal class ViewHolder{
        var rating_photo : ImageView?= null

        //평가 안했을 때 쓰는놈들
        var rating_photo_like : ImageView?= null

        //평가 했을 때 쓰는놈들
        var rating_photo_like_rating : ImageView?= null
        var rating_photo_text_rating : TextView?= null

        var rating_photo_enlargement : ImageView? = null

    }

    override fun getItem(p0: Int): Any =true
    override fun getItemId(p0: Int): Long = 0
    override fun getCount(): Int = post_data.photo_url.size
}