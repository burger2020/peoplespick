package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Context
import android.content.res.Configuration
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON
import java.util.*

class Home_rating_adapter_basic : BaseAdapter {
    private val layoutInflater: LayoutInflater
    //상속받은 클래스에 콘텍스트가 있어서? (getter 생성하려는데)
    @get:JvmName("getContext_") private val context : Context
    private val post_data : Post_Main_Data
    private var photo_select : ArrayList<Boolean>
    private val rate_result : ArrayList<Int>
    private val rate_position : Int
    private val post_rating_position : Int


    constructor(context: Context, post_data: Post_Main_Data, photo_select : ArrayList<Boolean>,
                rate_result : ArrayList<Int>, rate_position : Int, post_rating_position : Int){
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.rate_result = rate_result
        this.post_data = post_data
        this.photo_select = photo_select
        this.rate_position = rate_position
        this.post_rating_position = post_rating_position
    }

    constructor(context: Context){
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.rate_result = arrayListOf()
        this.post_data = Post_Main_Data()
        this.photo_select = arrayListOf()
        this.rate_position = 0
        this.post_rating_position = 0
    }

    companion object {
        lateinit var mListener: interface_view_image
        lateinit var mListener2: interface_PhotoCliked
        fun ImageClicked(mListener:interface_view_image) { this.mListener = mListener }
        fun PhotoClicked(mListener2:interface_PhotoCliked) { this.mListener2 = mListener2 }
    }

    interface interface_view_image {
        // image 눌렀을 경우
        fun interface_view_image(photo_url: ArrayList<String>,photo_like_state : ArrayList<Boolean>,capture_option: Boolean,rate_position:Int, photo_position:Int)
    }
    interface interface_PhotoCliked{
        // 사진 클릭시 액티비티로 눌른 포지션 하트 클릭됐는지 인터페이스
        fun interface_photo_clicked(position:Int,is_select:Boolean)
    }

    override fun getView(position: Int, convertView: View?, @NonNull parent: ViewGroup): View? {
        var mainView: View? = null
        val holder : ViewHolders
        val anim: Animation = AnimationUtils.loadAnimation(context.applicationContext, (R.anim.heart_anim))

        if (mainView == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mainView = inflater.inflate(R.layout.list_home_rating, parent, false)
            holder = ViewHolders()

            var is_select: Boolean
            var select_photo_position = 0
            var click_time = System.currentTimeMillis()
            val non_time = 700

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
            //없어도 될듯 / 리사이징 안하고 math parent 해놓으면 알아서 최대크기 크롭 돼버림 ㄱㅇㄷ

            if (rate_position!= HOME_RATING_NON) {
                //평가했을경우 하트 붉은색으로 바꾸기
                holder.rating_photo_like_rating?.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 8, context.resources.displayMetrics. widthPixels / 2 / 8)
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
                    mListener2.interface_photo_clicked(select_photo_position,is_select)
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
                    mListener2.interface_photo_clicked(select_photo_position,is_select)
                }
            })
            //TODO 사진 롱클릭 했을경우
            holder.rating_photo?.setOnLongClickListener({
                if (click_time + non_time < System.currentTimeMillis()) {
                    mListener.interface_view_image(post_data.photo_url, photo_select, post_data.option_info_data.captuer_bool,rate_position, position)
                }
                return@setOnLongClickListener true
            })

            //확대 버튼 클릭
            holder.rating_photo_enlargement?.setOnClickListener({
                if (click_time + non_time < System.currentTimeMillis()) {
                    mListener.interface_view_image(post_data.photo_url, photo_select, post_data.option_info_data.captuer_bool,rate_position, position)
                }
            })
//            holder.rating_photo_text_rating?.text = ""+rate_result[position]
            //TODO 사진 목록 넣기
//            Glide.with(context).using(FirebaseImageLoader()).load(storageRef_.child(post_data.photo_url[position])).centerCrop().thumbnail(0.2f)
//                    .into(holder.rating_photo)
//            if(position!=0)
            holder.rating_photo?.scaleType = ImageView.ScaleType.CENTER_CROP


        } else {
            holder = convertView?.tag as ViewHolders
        }

        //사진 사이징
        when(post_data.photo_url.size){
            2->{
                holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight()/2)
            }
            3->{
                holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight()/3)
            }
            4->{
                holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight()/2)
            }
            5->{
                holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight()/3)
            }
            6->{
                holder.rating_photo?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight()/3)
            }
        }

        Glide.with(context)
                //파이어베이스 경로
                .using(FirebaseImageLoader())
                .load(MainActivity.storageRef.child(post_data.photo_url[position]))
                //URI
//                .load(post_data.photo_url[position])
                .centerCrop()
                .thumbnail(0.2f)
                .into(holder.rating_photo)

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
    //스테이터스바 제외한 사이즈
    fun getStatusBarHeight() : Int{
        var statusHeight= 0
        var screenSizeType = (context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK)

        if(screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            var resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")

            if (resourceId > 0) {
                statusHeight = context.resources.getDimensionPixelSize(resourceId);
            }
        }

        return context.resources.displayMetrics.heightPixels-statusHeight
    }


    internal class ViewHolders{
        var rating_photo : ImageView?= null

        //평가 안했을 때 쓰는놈들
        var rating_photo_like : ImageView?= null

        //평가 했을 때 쓰는놈들
        var rating_photo_like_rating : ImageView?= null
        var rating_photo_text_rating : TextView?= null

        var rating_photo_enlargement : ImageView? = null

    }

    override fun getItem(p0: Int): Any = false

    override fun getItemId(p0: Int): Long = 0

    override fun getCount(): Int {
        return if(post_data.photo_url.size!=5)
            post_data.photo_url.size
        else
            4
    }

}