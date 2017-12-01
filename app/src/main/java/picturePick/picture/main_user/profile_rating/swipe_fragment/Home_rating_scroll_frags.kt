package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_home_rating_scroll.view.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.R


class PlaceholderFragment : Fragment() {
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(rate_bool){
            heart_image_rate.animation = anim
            heart_image_rate.startAnimation(anim)
        }
        else {//평가 안했을경우
            if (init_flag) {
                //초기화 다되면 보여질때 하트 표시 및 애니메이션 ㄱ
                if (isVisibleToUser && Home_rating_scroll.heart_bools[position]) {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                } else {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser)
    }
    companion object {
        private val PHOTO_URI = "PHOTO_URI"
        private val HEART_BOOL = "HEART_BOOL"
        private val POSITION = "POSITION"
        private val RATE_BOOL = "RATE_BOOL"
        private val RATE_POSITION = "RATE_POSITION"
        private val RATE_RESULT = "RATE_RESULT"
        private var init_flag = false
        private var position = 0
        private var rate_bool = false
        private var rate_position = 0
        lateinit var heart_image :ImageView
        lateinit var heart_image_rate : ImageView
        lateinit var anim : Animation
        fun newInstance(sectionNumber: Int,photo_uri : String,heart_bool : Boolean,rate_bool : Boolean,rate_result : Int,rate_position : Int): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val args = Bundle()
            args.putString(PHOTO_URI, photo_uri)
            args.putInt(POSITION,sectionNumber)
            args.putInt(RATE_RESULT,rate_result)
            args.putBoolean(HEART_BOOL, heart_bool)
            args.putBoolean(RATE_BOOL,rate_bool)
            args.putInt(RATE_POSITION,rate_position)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_rating_scroll, container, false)
        val bundle : Bundle = arguments

        position  = bundle.getInt(POSITION)
        rate_bool = bundle.getBoolean(RATE_BOOL)
        rate_position = bundle.getInt(RATE_POSITION)
        val photo_uri = bundle.getString(PHOTO_URI)
        val rate_result = bundle.getInt(RATE_RESULT)

        val photo_view :PhotoView = rootView.widget_photoview

        val rate_non_layout = rootView.rate_non_layout//평가 안했을때 하트 레이어
        val heart_backGround : ImageView = rootView.heart_background
        heart_image = rootView.heart_image

        val rate_ok_layout = rootView.rate_ok_layout  //평가 했을때 하트,숫자 레이어
        val heart_backGround_rate = rootView.heart_background_rate
        val heart_text_backgroud_rate = rootView.heart_text_background_rate
        val heart_text_rate = rootView.heart_text_rate

        heart_image_rate = rootView.heart_image_rate

        anim = AnimationUtils.loadAnimation(context.applicationContext,(R.anim.heart_anim_invisible))

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(MainActivity.storageRef.child(photo_uri))
                .thumbnail(0.2f)
                .into(photo_view)

        if(rate_bool){//평가 했을때 or 평가 시간 끝
            rate_non_layout.visibility=View.GONE
            rate_ok_layout.visibility=View.VISIBLE
            if(position == rate_position)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    heart_image_rate.background = resources.getDrawable(R.mipmap.ic_home_rating_heart)
                    heart_image_rate.animation = anim
                    heart_image_rate.startAnimation(anim)
                }
            heart_backGround_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_backgroud_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)

            //투표수에 따라 글자입력 및 크기 변경
            heart_text_rate.text = ""+rate_result
            when{
                rate_result>=1000->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1000)
                }
                rate_result>=100->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_100)
                }
                rate_result>=10->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_10)
                }
                else->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1)
                }
            }
        }
        else {//평가 안했을떄
            rate_non_layout.visibility=View.VISIBLE
            rate_ok_layout.visibility=View.GONE
            heart_backGround.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
        }
        init_flag = true

        //하트 눌러져있을경우 하트 표시
        if(rate_bool){}
        else {//평가 안했을때
            if (Home_rating_scroll.heart_bools[position]) {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                heart_image.animation = anim
                heart_image.startAnimation(anim)
            } else {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
            }
        }

        //사진 롱클릭
        photo_view.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        //하트 클릭시
        heart_image.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        return rootView
    }
}

class PlaceholderFragment2 : Fragment() {
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(rate_bool){
            heart_image_rate.animation = anim
            heart_image_rate.startAnimation(anim)
        }
        else {//평가 안했을경우
            if (init_flag) {
                //초기화 다되면 보여질때 하트 표시 및 애니메이션 ㄱ
                if (isVisibleToUser && Home_rating_scroll.heart_bools[position]) {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                } else {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser)
    }
    companion object {
        private val PHOTO_URI = "PHOTO_URI"
        private val HEART_BOOL = "HEART_BOOL"
        private val POSITION = "POSITION"
        private val RATE_BOOL = "RATE_BOOL"
        private val RATE_POSITION = "RATE_POSITION"
        private val RATE_RESULT = "RATE_RESULT"
        private var init_flag = false
        private var position = 0
        private var rate_bool = false
        private var rate_position = 0
        lateinit var heart_image :ImageView
        lateinit var heart_image_rate : ImageView
        lateinit var anim : Animation
        fun newInstance(sectionNumber: Int,photo_uri : String,heart_bool : Boolean,rate_bool : Boolean,rate_result : Int,rate_position : Int): PlaceholderFragment2 {
            val fragment = PlaceholderFragment2()
            val args = Bundle()
            args.putString(PHOTO_URI, photo_uri)
            args.putInt(POSITION,sectionNumber)
            args.putInt(RATE_RESULT,rate_result)
            args.putBoolean(HEART_BOOL, heart_bool)
            args.putBoolean(RATE_BOOL,rate_bool)
            args.putInt(RATE_POSITION,rate_position)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_rating_scroll, container, false)
        val bundle : Bundle = arguments

        position  = bundle.getInt(POSITION)
        rate_bool = bundle.getBoolean(RATE_BOOL)
        rate_position = bundle.getInt(RATE_POSITION)
        val photo_uri = bundle.getString(PHOTO_URI)
        val rate_result = bundle.getInt(RATE_RESULT)

        val photo_view :PhotoView = rootView.widget_photoview

        val rate_non_layout = rootView.rate_non_layout//평가 안했을때 하트 레이어
        val heart_backGround : ImageView = rootView.heart_background
        heart_image = rootView.heart_image

        val rate_ok_layout = rootView.rate_ok_layout  //평가 했을때 하트,숫자 레이어
        val heart_backGround_rate = rootView.heart_background_rate
        val heart_text_backgroud_rate = rootView.heart_text_background_rate
        val heart_text_rate = rootView.heart_text_rate

        heart_image_rate = rootView.heart_image_rate

        anim = AnimationUtils.loadAnimation(context.applicationContext,(R.anim.heart_anim_invisible))

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(MainActivity.storageRef.child(photo_uri))
                .thumbnail(0.2f)
                .into(photo_view)

        if(rate_bool){//평가 했을때 or 평가 시간 끝
            rate_non_layout.visibility=View.GONE
            rate_ok_layout.visibility=View.VISIBLE
            if(position == rate_position)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    heart_image_rate.background = resources.getDrawable(R.mipmap.ic_home_rating_heart)
                    heart_image_rate.animation = anim
                    heart_image_rate.startAnimation(anim)
                }
            heart_backGround_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_backgroud_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)

            //투표수에 따라 글자입력 및 크기 변경
            heart_text_rate.text = ""+rate_result
            when{
                rate_result>=1000->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1000)
                }
                rate_result>=100->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_100)
                }
                rate_result>=10->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_10)
                }
                else->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1)
                }
            }
        }
        else {//평가 안했을떄
            rate_non_layout.visibility=View.VISIBLE
            rate_ok_layout.visibility=View.GONE
            heart_backGround.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
        }
        init_flag = true

        //하트 눌러져있을경우 하트 표시
        if(rate_bool){}
        else {//평가 안했을때
            if (Home_rating_scroll.heart_bools[position]) {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                heart_image.animation = anim
                heart_image.startAnimation(anim)
            } else {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
            }
        }

        //사진 롱클릭
        photo_view.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        //하트 클릭시
        heart_image.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        return rootView
    }
}

class PlaceholderFragment3 : Fragment() {
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(rate_bool){
            heart_image_rate.animation = anim
            heart_image_rate.startAnimation(anim)
        }
        else {//평가 안했을경우
            if (init_flag) {
                //초기화 다되면 보여질때 하트 표시 및 애니메이션 ㄱ
                if (isVisibleToUser && Home_rating_scroll.heart_bools[position]) {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                } else {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser)
    }
    companion object {
        private val PHOTO_URI = "PHOTO_URI"
        private val HEART_BOOL = "HEART_BOOL"
        private val POSITION = "POSITION"
        private val RATE_BOOL = "RATE_BOOL"
        private val RATE_POSITION = "RATE_POSITION"
        private val RATE_RESULT = "RATE_RESULT"
        private var init_flag = false
        private var position = 0
        private var rate_bool = false
        private var rate_position = 0
        lateinit var heart_image :ImageView
        lateinit var heart_image_rate : ImageView
        lateinit var anim : Animation
        fun newInstance(sectionNumber: Int,photo_uri : String,heart_bool : Boolean,rate_bool : Boolean,rate_result : Int,rate_position : Int): PlaceholderFragment3 {
            val fragment = PlaceholderFragment3()
            val args = Bundle()
            args.putString(PHOTO_URI, photo_uri)
            args.putInt(POSITION,sectionNumber)
            args.putInt(RATE_RESULT,rate_result)
            args.putBoolean(HEART_BOOL, heart_bool)
            args.putBoolean(RATE_BOOL,rate_bool)
            args.putInt(RATE_POSITION,rate_position)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_rating_scroll, container, false)
        val bundle : Bundle = arguments

        position  = bundle.getInt(POSITION)
        rate_bool = bundle.getBoolean(RATE_BOOL)
        rate_position = bundle.getInt(RATE_POSITION)
        val photo_uri = bundle.getString(PHOTO_URI)
        val rate_result = bundle.getInt(RATE_RESULT)

        val photo_view :PhotoView = rootView.widget_photoview

        val rate_non_layout = rootView.rate_non_layout//평가 안했을때 하트 레이어
        val heart_backGround : ImageView = rootView.heart_background
        heart_image = rootView.heart_image

        val rate_ok_layout = rootView.rate_ok_layout  //평가 했을때 하트,숫자 레이어
        val heart_backGround_rate = rootView.heart_background_rate
        val heart_text_backgroud_rate = rootView.heart_text_background_rate
        val heart_text_rate = rootView.heart_text_rate

        heart_image_rate = rootView.heart_image_rate

        anim = AnimationUtils.loadAnimation(context.applicationContext,(R.anim.heart_anim_invisible))

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(MainActivity.storageRef.child(photo_uri))
                .thumbnail(0.2f)
                .into(photo_view)

        if(rate_bool){//평가 했을때 or 평가 시간 끝
            rate_non_layout.visibility=View.GONE
            rate_ok_layout.visibility=View.VISIBLE
            if(position == rate_position)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    heart_image_rate.background = resources.getDrawable(R.mipmap.ic_home_rating_heart)
                    heart_image_rate.animation = anim
                    heart_image_rate.startAnimation(anim)
                }
            heart_backGround_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_backgroud_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)

            //투표수에 따라 글자입력 및 크기 변경
            heart_text_rate.text = ""+rate_result
            when{
                rate_result>=1000->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1000)
                }
                rate_result>=100->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_100)
                }
                rate_result>=10->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_10)
                }
                else->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1)
                }
            }
        }
        else {//평가 안했을떄
            rate_non_layout.visibility=View.VISIBLE
            rate_ok_layout.visibility=View.GONE
            heart_backGround.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
        }
        init_flag = true

        //하트 눌러져있을경우 하트 표시
        if(rate_bool){}
        else {//평가 안했을때
            if (Home_rating_scroll.heart_bools[position]) {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                heart_image.animation = anim
                heart_image.startAnimation(anim)
            } else {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
            }
        }

        //사진 롱클릭
        photo_view.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        //하트 클릭시
        heart_image.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        return rootView
    }
}

class PlaceholderFragment4 : Fragment() {
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(rate_bool){
            heart_image_rate.animation = anim
            heart_image_rate.startAnimation(anim)
        }
        else {//평가 안했을경우
            if (init_flag) {
                //초기화 다되면 보여질때 하트 표시 및 애니메이션 ㄱ
                if (isVisibleToUser && Home_rating_scroll.heart_bools[position]) {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                } else {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser)
    }
    companion object {
        private val PHOTO_URI = "PHOTO_URI"
        private val HEART_BOOL = "HEART_BOOL"
        private val POSITION = "POSITION"
        private val RATE_BOOL = "RATE_BOOL"
        private val RATE_POSITION = "RATE_POSITION"
        private val RATE_RESULT = "RATE_RESULT"
        private var init_flag = false
        private var position = 0
        private var rate_bool = false
        private var rate_position = 0
        lateinit var heart_image :ImageView
        lateinit var heart_image_rate : ImageView
        lateinit var anim : Animation
        fun newInstance(sectionNumber: Int,photo_uri : String,heart_bool : Boolean,rate_bool : Boolean,rate_result : Int,rate_position : Int): PlaceholderFragment4 {
            val fragment = PlaceholderFragment4()
            val args = Bundle()
            args.putString(PHOTO_URI, photo_uri)
            args.putInt(POSITION,sectionNumber)
            args.putInt(RATE_RESULT,rate_result)
            args.putBoolean(HEART_BOOL, heart_bool)
            args.putBoolean(RATE_BOOL,rate_bool)
            args.putInt(RATE_POSITION,rate_position)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_rating_scroll, container, false)
        val bundle : Bundle = arguments

        position  = bundle.getInt(POSITION)
        rate_bool = bundle.getBoolean(RATE_BOOL)
        rate_position = bundle.getInt(RATE_POSITION)
        val photo_uri = bundle.getString(PHOTO_URI)
        val rate_result = bundle.getInt(RATE_RESULT)

        val photo_view :PhotoView = rootView.widget_photoview

        val rate_non_layout = rootView.rate_non_layout//평가 안했을때 하트 레이어
        val heart_backGround : ImageView = rootView.heart_background
        heart_image = rootView.heart_image

        val rate_ok_layout = rootView.rate_ok_layout  //평가 했을때 하트,숫자 레이어
        val heart_backGround_rate = rootView.heart_background_rate
        val heart_text_backgroud_rate = rootView.heart_text_background_rate
        val heart_text_rate = rootView.heart_text_rate

        heart_image_rate = rootView.heart_image_rate

        anim = AnimationUtils.loadAnimation(context.applicationContext,(R.anim.heart_anim_invisible))

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(MainActivity.storageRef.child(photo_uri))
                .thumbnail(0.2f)
                .into(photo_view)

        if(rate_bool){//평가 했을때 or 평가 시간 끝
            rate_non_layout.visibility=View.GONE
            rate_ok_layout.visibility=View.VISIBLE
            if(position == rate_position)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    heart_image_rate.background = resources.getDrawable(R.mipmap.ic_home_rating_heart)
                    heart_image_rate.animation = anim
                    heart_image_rate.startAnimation(anim)
                }
            heart_backGround_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_backgroud_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)

            //투표수에 따라 글자입력 및 크기 변경
            heart_text_rate.text = ""+rate_result
            when{
                rate_result>=1000->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1000)
                }
                rate_result>=100->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_100)
                }
                rate_result>=10->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_10)
                }
                else->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1)
                }
            }
        }
        else {//평가 안했을떄
            rate_non_layout.visibility=View.VISIBLE
            rate_ok_layout.visibility=View.GONE
            heart_backGround.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
        }
        init_flag = true

        //하트 눌러져있을경우 하트 표시
        if(rate_bool){}
        else {//평가 안했을때
            if (Home_rating_scroll.heart_bools[position]) {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                heart_image.animation = anim
                heart_image.startAnimation(anim)
            } else {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
            }
        }

        //사진 롱클릭
        photo_view.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        //하트 클릭시
        heart_image.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        return rootView
    }
}

class PlaceholderFragment5 : Fragment() {
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(rate_bool){
            heart_image_rate.animation = anim
            heart_image_rate.startAnimation(anim)
        }
        else {//평가 안했을경우
            if (init_flag) {
                //초기화 다되면 보여질때 하트 표시 및 애니메이션 ㄱ
                if (isVisibleToUser && Home_rating_scroll.heart_bools[position]) {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                } else {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser)
    }
    companion object {
        private val PHOTO_URI = "PHOTO_URI"
        private val HEART_BOOL = "HEART_BOOL"
        private val POSITION = "POSITION"
        private val RATE_BOOL = "RATE_BOOL"
        private val RATE_POSITION = "RATE_POSITION"
        private val RATE_RESULT = "RATE_RESULT"
        private var init_flag = false
        private var position = 0
        private var rate_bool = false
        private var rate_position = 0
        lateinit var heart_image :ImageView
        lateinit var heart_image_rate : ImageView
        lateinit var anim : Animation
        fun newInstance(sectionNumber: Int,photo_uri : String,heart_bool : Boolean,rate_bool : Boolean,rate_result : Int,rate_position : Int): PlaceholderFragment5 {
            val fragment = PlaceholderFragment5()
            val args = Bundle()
            args.putString(PHOTO_URI, photo_uri)
            args.putInt(POSITION,sectionNumber)
            args.putInt(RATE_RESULT,rate_result)
            args.putBoolean(HEART_BOOL, heart_bool)
            args.putBoolean(RATE_BOOL,rate_bool)
            args.putInt(RATE_POSITION,rate_position)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_rating_scroll, container, false)
        val bundle : Bundle = arguments

        position  = bundle.getInt(POSITION)
        rate_bool = bundle.getBoolean(RATE_BOOL)
        rate_position = bundle.getInt(RATE_POSITION)
        val photo_uri = bundle.getString(PHOTO_URI)
        val rate_result = bundle.getInt(RATE_RESULT)

        val photo_view :PhotoView = rootView.widget_photoview

        val rate_non_layout = rootView.rate_non_layout//평가 안했을때 하트 레이어
        val heart_backGround : ImageView = rootView.heart_background
        heart_image = rootView.heart_image

        val rate_ok_layout = rootView.rate_ok_layout  //평가 했을때 하트,숫자 레이어
        val heart_backGround_rate = rootView.heart_background_rate
        val heart_text_backgroud_rate = rootView.heart_text_background_rate
        val heart_text_rate = rootView.heart_text_rate

        heart_image_rate = rootView.heart_image_rate

        anim = AnimationUtils.loadAnimation(context.applicationContext,(R.anim.heart_anim_invisible))

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(MainActivity.storageRef.child(photo_uri))
                .thumbnail(0.2f)
                .into(photo_view)

        if(rate_bool){//평가 했을때 or 평가 시간 끝
            rate_non_layout.visibility=View.GONE
            rate_ok_layout.visibility=View.VISIBLE
            if(position == rate_position)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    heart_image_rate.background = resources.getDrawable(R.mipmap.ic_home_rating_heart)
                    heart_image_rate.animation = anim
                    heart_image_rate.startAnimation(anim)
                }
            heart_backGround_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_backgroud_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)

            //투표수에 따라 글자입력 및 크기 변경
            heart_text_rate.text = ""+rate_result
            when{
                rate_result>=1000->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1000)
                }
                rate_result>=100->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_100)
                }
                rate_result>=10->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_10)
                }
                else->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1)
                }
            }
        }
        else {//평가 안했을떄
            rate_non_layout.visibility=View.VISIBLE
            rate_ok_layout.visibility=View.GONE
            heart_backGround.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
        }
        init_flag = true

        //하트 눌러져있을경우 하트 표시
        if(rate_bool){}
        else {//평가 안했을때
            if (Home_rating_scroll.heart_bools[position]) {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                heart_image.animation = anim
                heart_image.startAnimation(anim)
            } else {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
            }
        }

        //사진 롱클릭
        photo_view.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        //하트 클릭시
        heart_image.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        return rootView
    }
}

class PlaceholderFragment6 : Fragment() {
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(rate_bool){
            heart_image_rate.animation = anim
            heart_image_rate.startAnimation(anim)
        }
        else {//평가 안했을경우
            if (init_flag) {
                //초기화 다되면 보여질때 하트 표시 및 애니메이션 ㄱ
                if (isVisibleToUser && Home_rating_scroll.heart_bools[position]) {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                } else {
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                }
            }
        }
        super.setUserVisibleHint(isVisibleToUser)
    }
    companion object {
        private val PHOTO_URI = "PHOTO_URI"
        private val HEART_BOOL = "HEART_BOOL"
        private val POSITION = "POSITION"
        private val RATE_BOOL = "RATE_BOOL"
        private val RATE_POSITION = "RATE_POSITION"
        private val RATE_RESULT = "RATE_RESULT"
        private var init_flag = false
        private var position = 0
        private var rate_bool = false
        private var rate_position = 0
        lateinit var heart_image :ImageView
        lateinit var heart_image_rate : ImageView
        lateinit var anim : Animation
        fun newInstance(sectionNumber: Int,photo_uri : String,heart_bool : Boolean,rate_bool : Boolean,rate_result : Int,rate_position : Int): PlaceholderFragment6 {
            val fragment = PlaceholderFragment6()
            val args = Bundle()
            args.putString(PHOTO_URI, photo_uri)
            args.putInt(POSITION,sectionNumber)
            args.putInt(RATE_RESULT,rate_result)
            args.putBoolean(HEART_BOOL, heart_bool)
            args.putBoolean(RATE_BOOL,rate_bool)
            args.putInt(RATE_POSITION,rate_position)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_rating_scroll, container, false)
        val bundle : Bundle = arguments

        position  = bundle.getInt(POSITION)
        rate_bool = bundle.getBoolean(RATE_BOOL)
        rate_position = bundle.getInt(RATE_POSITION)
        val photo_uri = bundle.getString(PHOTO_URI)
        val rate_result = bundle.getInt(RATE_RESULT)

        val photo_view :PhotoView = rootView.widget_photoview

        val rate_non_layout = rootView.rate_non_layout//평가 안했을때 하트 레이어
        val heart_backGround : ImageView = rootView.heart_background
        heart_image = rootView.heart_image

        val rate_ok_layout = rootView.rate_ok_layout  //평가 했을때 하트,숫자 레이어
        val heart_backGround_rate = rootView.heart_background_rate
        val heart_text_backgroud_rate = rootView.heart_text_background_rate
        val heart_text_rate = rootView.heart_text_rate

        heart_image_rate = rootView.heart_image_rate

        anim = AnimationUtils.loadAnimation(context.applicationContext,(R.anim.heart_anim_invisible))

        Glide.with(context)
                .using(FirebaseImageLoader())
                .load(MainActivity.storageRef.child(photo_uri))
                .thumbnail(0.2f)
                .into(photo_view)

        if(rate_bool){//평가 했을때 or 평가 시간 끝
            rate_non_layout.visibility=View.GONE
            rate_ok_layout.visibility=View.VISIBLE
            if(position == rate_position)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    heart_image_rate.background = resources.getDrawable(R.mipmap.ic_home_rating_heart)
                    heart_image_rate.animation = anim
                    heart_image_rate.startAnimation(anim)
                }
            heart_backGround_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_backgroud_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_text_rate.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)

            //투표수에 따라 글자입력 및 크기 변경
            heart_text_rate.text = ""+rate_result
            when{
                rate_result>=1000->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1000)
                }
                rate_result>=100->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_100)
                }
                rate_result>=10->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_10)
                }
                else->{
                    heart_text_rate.textSize = context.resources.getDimension(R.dimen.result_scroll_text_size_1)
                }
            }
        }
        else {//평가 안했을떄
            rate_non_layout.visibility=View.VISIBLE
            rate_ok_layout.visibility=View.GONE
            heart_backGround.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
            heart_image.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 2 / 6, context.resources.displayMetrics.widthPixels / 2 / 6)
        }
        init_flag = true

        //하트 눌러져있을경우 하트 표시
        if(rate_bool){}
        else {//평가 안했을때
            if (Home_rating_scroll.heart_bools[position]) {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                heart_image.animation = anim
                heart_image.startAnimation(anim)
            } else {
                heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
            }
        }

        //사진 롱클릭
        photo_view.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        //하트 클릭시
        heart_image.setOnClickListener({
            if(rate_bool){}
            else {//평가 안했을때
                if (Home_rating_scroll.heart_bools[position]) {
                    Home_rating_scroll.heart_bools[position] = false
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart_non)
                } else {
                    for (i in 0 until Home_rating_scroll.heart_bools.size)
                        Home_rating_scroll.heart_bools[i] = false
                    Home_rating_scroll.heart_bools[position] = true
                    heart_image.setBackgroundResource(R.mipmap.ic_home_rating_heart)
                    heart_image.animation = anim
                    heart_image.startAnimation(anim)
                }
            }
        })
        return rootView
    }
}