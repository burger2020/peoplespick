package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.instacart.library.truetime.TrueTimeRx
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_home_raing_activity.*
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_get_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_put_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.profile_change_meta
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_NUM
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_NUM_REQ
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_SAVE_TIME
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_COMMENT_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_END
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_RATING_BOOL
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_CAPTURE_BOOL
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_HEART_BOOL
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_HEART_RESULT
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_HEART_RESULT_BOOL
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_PHOTOS
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_POSITION
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_RATE_BOOL
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_SCROLL_RATE_RESULT
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.POST_CUSTOM_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.POST_UPDATE_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.ratingPage_tagClick
import picturePick.picture.main_user.profile_rating.data.post_rater_data
import picturePick.picture.main_user.profile_rating.swipe_fragment.Home_posting_comment.Companion.post_Rater_data
import java.util.*


class Home_raing_activity : AppCompatActivity(),View.OnClickListener {
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.posting_backq_key->{
                //백키
                onBackPressed()
            }
            R.id.post_delete_button->{
                //삭제 아이콘
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder
                        .setTitle(R.string.delete_post_title)
                        .setMessage(getString(R.string.delete_post))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.delete)
                        ) { dialog, id ->
                            val id = post_main_data.save_time
                            val uri = post_main_data.photo_url
//                                MainActivity.Photo_Database.child("post").child("" + post_main_data.save_time).removeValue()

                            firestore_.collection("post").document("" + (10000000000000-id)).delete()
                            firestore_.collection(RATING_POST).document("" + (10000000000000-id)).delete()
                            for(i in 0 until uri.size)
                                storageRef.child(uri[i]).delete()

                            onBackPressed()
                            // 삭제 눌렀을떄
                        }
                        .setNegativeButton(getString(R.string.Cancel)
                        ) { dialog, id ->
                            // 다이얼로그 취소
                            dialog.cancel()
                        }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
            R.id.post_custom_button->{
                //수정 아이콘
                val intent = Intent(this,Post_Custom_act::class.java)
                intent.putExtra(POST_CUSTOM_DATA, post_main_data)

                startActivityForResult(intent,UPDATE_REQ)
            }
        }
    }

    companion object {
        lateinit var mListener3: interface_tag_clicked
        fun tagClicked_(mListener3:interface_tag_clicked) { this.mListener3 = mListener3 }
    }

    interface interface_tag_clicked{
        fun interface_tag_clicked(tag:String)
    }

    //    val storageRef : StorageReference = FirebaseStorage.getInstance("gs://pictures-pick/").reference
    val PHOTO_VIEW_RESULT = 1010
    val COMMENT_REQ = 10111
    val UPDATE_REQ = 1018

    var rating_position = 0  // 투표한 사진 포지션
    var rating_bool = false // 투표했는지 확인)

    lateinit var rater_recomment_num_txt : TextView
    val photo_select : ArrayList<Boolean> = arrayListOf()
    lateinit var post_main_data: Post_Main_Data
    lateinit var adapter : Home_rating_adapter
    lateinit var post_comment_txt : TextView
    lateinit var adapter_asy : Home_rating_adapter_basic
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_raing_activity)

//        val actionBar : ActionBar? = supportActionBar
//        actionBar?.elevation = 5F
//        actionBar?.setDisplayShowCustomEnabled(true);
//        actionBar?.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
//        actionBar?.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
//        actionBar?.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val actionbar = inflater.inflate(R.layout.toolbar_rating_act, null)
//        actionBar?.customView = actionbar
        //액션바 양쪽 공백 없애기

        setSupportActionBar(toolbar_rating)
        //뒤로가기 클릭 리스너
        posting_backq_key.setOnClickListener(this)

        val Intent = intent
        post_main_data = Intent.getSerializableExtra(HOME_RATING_DATA) as Post_Main_Data //사진 uri
        var rate_position : Int = Intent.getIntExtra(HOME_RATING_RATING_BOOL,44) // 평가 했는지
        val post_rate_data : post_rater_data = Intent.getSerializableExtra(HOME_RATING_COMMENT_DATA) as post_rater_data // 사진마다 평가 결과
        var post_rating_position : Int = rate_position
        for (i in 0 until post_main_data.photo_url.size)
            photo_select.add(i,false)

        //TODO 배치다른 그리드뷰 테스트중
        val rating_photo_list = rating_photo_list  // 그리드뷰

        val rating_time_txt = rating_post_time_text // 평가 남은시간 및 종료 멘트
        val posting_time_txt = posting_time_text // 포스트 등록시간 및 초과시간
        val rater_num_txt = rater_post_num_txt // 평가자 수
        val poster_profile_name_txt = poster_post_profile_name //등록자 이름
        val poster_profile_img = poster_post_profile_image  // 등록자 프사
        post_comment_txt = rating_commenet_txt  // 글 내용
        rater_recomment_num_txt = rater_recomment_num

        //툴바 숨김
        rating_appbar.setExpanded(false, false)

        //코멘트에 태그 클릭스팬 넣기
        val Tag_start_position = arrayListOf<Int>()
        val Tag_end_position = arrayListOf<Int>()
        val Tag_text = arrayListOf<String>()
        var tag_bool = false
        var tag_unit = ""
        var ss_comment = SpannableString(post_main_data.comment)

        for(i in 0 until post_main_data.comment.length){
            if(post_main_data.comment[i] == '#'){
                tag_bool = true
            }else if(tag_bool&&(post_main_data.comment[i] == ' ')){
                tag_bool = false
                if(tag_unit.isNotEmpty()) {
                    //TODO 스팬 다는곳
                    Tag_start_position.add(i-tag_unit.length-1)
                    Tag_end_position.add(i-1)
                    Tag_text.add(tag_unit)
                    tag_unit = ""
                }
            }else if(tag_bool&&(i == post_main_data.comment.length-1)){
                tag_bool = false
                tag_unit += post_main_data.comment[i]
                //TODO 스팬 다는곳
                Tag_start_position.add(i-tag_unit.length)
                Tag_end_position.add(i)
                Tag_text.add(tag_unit)
                tag_unit = ""
            }else if(tag_bool&&post_main_data.comment[i] != ' '){
                tag_unit +=post_main_data.comment[i]
            }
        }

        //태그 클릭스팬 달아주기
        for(i in 0 until Tag_start_position.size){
            ss_comment.setSpan(
                    object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint?) {
                            ds?.color = resources.getColor(R.color.colorAccent)    // you can use custom color
                            ds?.isUnderlineText = false    // this remove the underline
                        }
                        override fun onClick(widget: View) {
                            widget.invalidate()
                            val intent = Intent
                            intent.putExtra("TAG",Tag_text[i])
                            setResult(ratingPage_tagClick,intent)
                            finish()
//                            mListener3.interface_tag_clicked(Tag_text[i])
                        }
                    }
                    ,Tag_start_position[i]
                    ,Tag_end_position[i]+1
                    , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        //코멘트 표시
        post_comment_txt.movementMethod = LinkMovementMethod.getInstance()
        post_comment_txt.setText(ss_comment,TextView.BufferType.SPANNABLE)


        //삭제, 수정 아이콘 작성자 맞으면 보이도록
        if(user_info.id == this.post_main_data.poster_token){
            option_icon_layout.visibility = View.VISIBLE
            post_delete_button.setOnClickListener(this)
            post_custom_button.setOnClickListener(this)
        }

        //투표한사람 선택 옵션 안보이기
        if(rate_position != HOME_RATING_NON){
            photo_pick_complete.visibility = View.GONE
        }

        //댓글 갯수 표시
        rater_recomment_num_txt.text = getString(R.string.rating_posting_recomment_num_f)+post_main_data.recomment_num + getString(R.string.rating_posting_recomment_num_b)
        //댓글 갯수 클릭시 다이얼로그로 댓글 보기창
        rater_recomment_num_txt.setOnClickListener({
            val intent = Intent(this, Home_posting_comment::class.java)
            intent.putExtra(COMMENT_LIST_SAVE_TIME, this.post_main_data.save_time)
            intent.putExtra(COMMENT_LIST_NUM, this.post_main_data.recomment_num)
            startActivityForResult(intent, COMMENT_REQ)
        })

        if(post_main_data.option_info_data.comment_bool)
            rater_recomment_num_txt.visibility = View.GONE
        else
            rater_recomment_num_txt.visibility = View.VISIBLE

        // 선택 옵션 누를시
        photo_pick_complete.setOnClickListener({
            if(rating_bool&&rate_position== HOME_RATING_NON) {

                firestore_.collection(RATING_POST).document(""+(10000000000000-post_main_data.save_time)).get().addOnCompleteListener({
                    post_rate_data.rater_num = it.result.toObject(post_Rater_data::class.java).rater_num+1

                    post_main_data.post_rater_num = post_rate_data.rater_num

                    post_rating_position = rating_position
                    rate_position = rating_position
                    rating_update(post_rate_data,rating_position,rater_num_txt,rating_time_txt,posting_time_txt, rater_recomment_num_txt,post_main_data,rate_position)
                })

            }else if(!rating_bool&&rate_position== HOME_RATING_NON){
                Toast.makeText(this,getString(R.string.rating_non),Toast.LENGTH_SHORT).show()
            }
        })

//        fun rating_update(rater_list : ArrayList<String>,rate_result:ArrayList<Int>,rating_position:Int,posting_time:Long,
//                          photo_uri:ArrayList<String>,capture:Boolean,comment:Boolean,rater_num:Int,rate_bool:Boolean){

        //프로필 이미지, 이름
        poster_profile_name_txt.text = post_main_data.poster_name


        storageRef.child(post_main_data.poster_profile).metadata.addOnSuccessListener {
            if(it.getCustomMetadata(pic_pic_string_data.PROFILE_STATE)== pic_pic_string_data.NON_PROFILE){
                Glide.with(this)
                        .load(R.mipmap.ic_non_profile)
                        .override(150, 150)
                        .centerCrop()
                        .signature(StringSignature(profile_change_meta))
                        .bitmapTransform(CropCircleTransformation(this))
                        .into(poster_profile_img)
            }else {
                Glide.with(this)
                        .using(FirebaseImageLoader())
                        .load(storageRef.child(post_main_data.poster_profile))
                        .centerCrop()
                        .thumbnail(0.3F)
                        .override(150, 150)
                        .signature(StringSignature(profile_change_meta))
                        .bitmapTransform(CropCircleTransformation(this))
                        .into(poster_profile_img)
            }
        }

        val TrueTime = try {
            TrueTimeRx.now().time
        }catch (e : IllegalStateException){
            System.currentTimeMillis()
        }
        val time_remaining : Long = post_main_data.rating_end_time - TrueTime

        //남은시간, 투표자 수 표기
        when {
            time_remaining>3600000 -> { // 1시간 이상
                rating_time_txt.text = "${getString(R.string.rating_remaining_time)} : " +
                        "${(""+(time_remaining/1000-time_remaining/1000/60%60)/3600) +
                                getString(R.string.rating_remaining_hour) +
                                time_remaining/1000/60%60+getString(R.string.rating_remaining_min)}"
                rater_num_txt.text = ""+post_main_data.post_rater_num+getString(R.string.rating_num_)
            }
            time_remaining>0 -> {  // 1시간 이하
                rating_time_txt.text = "${getString(R.string.rating_remaining_time)} : ${""+time_remaining/1000/60+getString(R.string.rating_remaining_min)}"
                rater_num_txt.text = ""+post_main_data.post_rater_num+getString(R.string.rating_num_)
            }
            else -> {  // 평가시간 끝남
                rating_time_txt.text = ""
                rater_num_txt.text = ""+post_main_data.post_rater_num+getString(R.string.rating_num_end)
                if(rate_position == HOME_RATING_NON)
                    rate_position= HOME_RATING_END
                photo_pick_complete.visibility = View.GONE
            }
        }

        //올린시간 표기
        val time_registration : Long =  TrueTime - post_main_data.save_time
        if(time_registration>=24*60*60*1000*3){ // 등록한지 3일이상
            val date = Date(TrueTime)
            val date_ = Date(post_main_data.save_time)
            if(date.year == date_.year) // 같은 연도일경우 월.일 만 표시
                posting_time_txt.text = "${""+date_.month+1+getString(R.string.rating_posting_month)+date_.date+getString(R.string.rating_posting_day)}"
            else
                posting_time_txt.text = "${""+(date_.year+1902)+getString(R.string.rating_posting_year)+date_.month+1+getString(R.string.rating_posting_month)+date_.date+getString(R.string.rating_posting_day)}"
        }else if(time_registration>=24*60*60*1000){ // 등록한지 3일이하 1일이상 (경과 일수 표시)
            //                                              mil  분 시 일
            posting_time_txt.text = "${""+time_registration/1000/60/60/24+getString(R.string.rating_posting_before_day)}"
        }else if(time_registration>=60*60*1000){ // 등록한지 1일이하 ( 경과 시간 표시)
            posting_time_txt.text = "${""+time_registration/1000/60/60+getString(R.string.rating_posting_before_hour)}"
        }else{ // 등록 1시간 이하 (경과 분 표시)
            posting_time_txt.text = "${""+time_registration/1000/60+getString(R.string.rating_posting_before_min)}"
        }

        //캡쳐 막기
        if(post_main_data.option_info_data.captuer_bool)
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        //사진 롱클릭시 스크롤화면 넘어가기 (인터페이스)
        Home_rating_adapter_basic.ImageClicked(object :Home_rating_adapter_basic.interface_view_image{
            override fun interface_view_image(photo_url: ArrayList<String>, photo_like_state: ArrayList<Boolean>, capture_option: Boolean, rate_position: Int, photo_position: Int) {

                val intent = Intent(applicationContext,Home_rating_scroll::class.java)
                intent.putStringArrayListExtra(HOME_RATING_SCROLL_PHOTOS,photo_url) // 사진 uri
                for(i in 0 until post_main_data.photo_url.size) // 하트 눌러진곳 지정
                    intent.putExtra(HOME_RATING_SCROLL_HEART_BOOL+i,photo_select[i])
                intent.putExtra(HOME_RATING_SCROLL_POSITION,photo_position)// 처음 보여질 사진 포지션
                intent.putExtra(HOME_RATING_SCROLL_CAPTURE_BOOL,capture_option)//캡쳐옵션 ㅇ
                intent.putExtra(HOME_RATING_SCROLL_RATE_BOOL,rate_position)// 평가했는지 안했는지
                intent.putIntegerArrayListExtra(HOME_RATING_SCROLL_RATE_RESULT,post_rate_data.rating_result)// 평가했는지 안했는지
                startActivityForResult(intent,PHOTO_VIEW_RESULT)
            }

        })
        //사진 클릭시
        Home_rating_adapter_basic.PhotoClicked(object :Home_rating_adapter_basic.interface_PhotoCliked{
            override fun interface_photo_clicked(position: Int, is_select: Boolean) {
                rating_position = position
                Log.d("position",""+position)
                rating_bool = is_select
                //5번째 사진 하트 제거
                if(post_main_data.photo_url.size==5&&position!=4)
                    rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart_non)

                if(rating_bool) {
                    rating_appbar.setExpanded(true, true)
                }
                else {
                    //보이게
                    rating_appbar.setExpanded(false, true)
                }
            }
        })
        //바로 확대 못하도록
        var click_time = System.currentTimeMillis()
        val non_time = 700
        adapter = Home_rating_adapter(this,post_main_data,photo_select,post_rate_data.rating_result,rate_position,post_rating_position)

        //그리드뷰 크기 상태바 제외한 크기에 맞추기

        adapter_asy = Home_rating_adapter_basic(this,post_main_data, photo_select, post_rate_data.rating_result, rate_position,post_rating_position)
        when(post_main_data.photo_url.size){
            2->{
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                photo_5_layer.visibility = View.GONE
                rating_photo_list.numColumns = 1
            }
            3->{
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                photo_5_layer.visibility = View.GONE
                rating_photo_list.numColumns = 1
            }
            4->{
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                photo_5_layer.visibility = View.GONE
                rating_photo_list.numColumns = 2
            }
            5->{
                val position_4 = 4
                val anim: Animation = AnimationUtils.loadAnimation(applicationContext, (R.anim.heart_anim))
                //평가 했을 경우
                if(rate_position!= HOME_RATING_NON){
                    rating_photo_like.visibility = View.GONE
                    rating_photo_like_rater.visibility = View.VISIBLE
                    rating_photo_like_num.visibility = View.VISIBLE

                    rating_photo_like_rater.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics. widthPixels / 2 / 8)
                    rating_photo_like_num.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)

                    if(post_rating_position == position_4)
                        rating_photo_like_rater.setImageResource(R.mipmap.ic_home_rating_heart)
                    else
                        rating_photo_like_rater.setImageResource(R.mipmap.ic_home_rating_heart_non)

                    rating_photo_like_rater.animation = anim
                    rating_photo_like_rater.startAnimation(anim)

                    rating_photo_enlargement?.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 7, resources.displayMetrics.widthPixels / 2 / 7)

                    val params = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 7, resources.displayMetrics.widthPixels / 2 / 7)
                    params.rightMargin = 10

                    rating_photo_like_rater.layoutParams = params

                    rating_photo_like_num.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)

                    rating_photo_like_num.text = ""+post_rate_data.rating_result[position_4].toLong()
                    when{
                        post_rate_data.rating_result[position_4].toLong()>=1000->{
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_1000)
                        }
                        post_rate_data.rating_result[position_4].toLong()>=100->{
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_100)
                        }
                        post_rate_data.rating_result[position_4].toLong()>=10->{
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_10)
                        }
                        else->{
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_1)
                        }
                    }

                }else{ // 평가 안했을시 셋
                    rating_photo_like.visibility = View.VISIBLE
                    rating_photo_like_rater.visibility = View.GONE
                    rating_photo_like_num.visibility = View.GONE
                    rating_photo_enlargement.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)
                    rating_photo_like.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)
                }

                //확대 버튼
                rating_photo_enlargement.setOnClickListener({
                    if (click_time + non_time < System.currentTimeMillis())
                        Home_rating_adapter_basic.mListener.interface_view_image(
                                post_main_data.photo_url, photo_select, post_main_data.option_info_data.captuer_bool,rate_position, position_4)
                })
                //TODO 사진 롱클릭 했을경우
                rating_photo_list1.setOnLongClickListener({
                    Home_rating_adapter_basic.mListener.interface_view_image(
                            post_main_data.photo_url, photo_select, post_main_data.option_info_data.captuer_bool,rate_position, position_4)
                    return@setOnLongClickListener true
                })
                //5번째 사진 클릭
                rating_photo_list1.setOnClickListener({
                    if (rate_position!= HOME_RATING_NON) {
                    }else{
                        if (photo_select[position_4]) { // 하트 취소
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart_non)
                            photo_select[position_4] = false
                            rating_bool = false
                            adapter_asy.notifyDataSetChanged()
                        } else {                       //하트 생성
                            for (i in 0 until post_main_data.photo_url.size)
                                photo_select[i] = false
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart)
                            rating_photo_like.animation = anim
                            rating_photo_like.startAnimation(anim)
                            photo_select[position_4] = true
                            rating_position = position_4
                            rating_bool = true
                            adapter_asy.notifyDataSetChanged()
                        }
                        Home_rating_adapter_basic.mListener2.interface_photo_clicked(4,rating_bool)
                    }
                })
                //TODO 하트 눌렀을경우
                rating_photo_like.setOnClickListener({
                    if (rate_position!= HOME_RATING_NON) {
                    }else{
                        if (photo_select[position_4]) { // 하트 취소
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart_non)
                            photo_select[position_4] = false
                            rating_bool = false
                            adapter_asy.notifyDataSetChanged()
                        } else {                       //하트 생성
                            for (i in 0 until post_main_data.photo_url.size)
                                photo_select[i] = false
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart)
                            rating_photo_like.animation = anim
                            rating_photo_like.startAnimation(anim)
                            photo_select[position_4] = true
                            rating_position = position_4
                            rating_bool = true
                            adapter_asy.notifyDataSetChanged()
                        }
                        Home_rating_adapter_basic.mListener2.interface_photo_clicked(position_4,rating_bool)
                    }
                })

                //그리드뷰, 5번 이미지 셋
                photo_5_layer.visibility = View.VISIBLE
                photo_5_layer.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels/2 , getStatusBarHeight()/3)
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                rating_photo_list1.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels/2 , getStatusBarHeight()/3)
                Glide.with(this)
                        .using(FirebaseImageLoader())
                        .load(storageRef.child(post_main_data.photo_url[position_4]))
                        .centerCrop()
                        .into(rating_photo_list1)
                rating_photo_list.numColumns = 2
            }
            6->{
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                photo_5_layer.visibility = View.GONE
                rating_photo_list.numColumns = 2
            }
        }

        rating_photo_list.adapter = adapter_asy
        adapter_asy.notifyDataSetChanged()

    }
    //스테이터스바 제외한 사이즈
    fun getStatusBarHeight() : Int{
        var statusHeight= 0
        var screenSizeType = (applicationContext.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK)

        if(screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            var resourceId = applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")

            if (resourceId > 0) {
                statusHeight = applicationContext.resources.getDimensionPixelSize(resourceId);
            }
        }

        return resources.displayMetrics.heightPixels-statusHeight
    }
    //투표시 업데이트 목록
    fun rating_update(post_rater_data: post_rater_data,rating_position:Int,
                      rater_num_txt:TextView,rating_time_txt:TextView,posting_time_txt:TextView,rater_recomment_num_txt : TextView,
                      post_data : Post_Main_Data,rate_position: Int){

        //사진 투표 수 업데이트
        val rating_photo_vote_num = post_rater_data.rating_result[rating_position]+1

        post_rater_data.rating_result[rating_position]= rating_photo_vote_num // 사진에 대한 투표수

        //투표자 수 업데이트할라고 다들고왔다 씨벌

        //등록일, 평가자수 업데이트
        val TrueTime = try {
            TrueTimeRx.now().time
        }catch (e : IllegalStateException){
            System.currentTimeMillis()
        }
        val time_remaining : Long = post_data.rating_end_time - TrueTime

        //남은시간, 투표자 수 표기
        when {
            time_remaining>3600000 -> { // 1시간 이상
                rating_time_txt.text = "${getString(R.string.rating_remaining_time)} : " +
                        "${(""+(time_remaining/1000-time_remaining/1000/60%60)/3600) +
                                getString(R.string.rating_remaining_hour) + time_remaining/1000/60%60+getString(R.string.rating_remaining_min)}"
                rater_num_txt.text = ""+post_data.post_rater_num+getString(R.string.rating_num_)
            }
            time_remaining>0 -> {  // 1시간 이하
                rating_time_txt.text = "${getString(R.string.rating_remaining_time)} : ${""+time_remaining/1000/60+getString(R.string.rating_remaining_min)}"
                rater_num_txt.text = ""+post_data.post_rater_num+getString(R.string.rating_num_)
            }
            else -> {  // 평가시간 끝남
                rating_time_txt.text = ""
                rater_num_txt.text = ""+post_data.post_rater_num+getString(R.string.rating_num_end)
            }
        }

        //올린시간 ,지난시간 표기 업데이트
        val time_registration : Long =  TrueTime - post_data.save_time
        if(time_registration>=24*60*60*1000*3){ // 등록한지 3일이상
            val date = Date(TrueTime)
            val date_ = Date(post_data.save_time)
            if(date.year == date_.year) // 같은 연도일경우 월.일 만 표시
                posting_time_txt.text = "${""+date_.month+1+getString(R.string.rating_posting_month)+date_.date+getString(R.string.rating_posting_day)}"
            else
                posting_time_txt.text = "${""+(date_.year+1902)+getString(R.string.rating_posting_year)+date_.month+1+getString(R.string.rating_posting_month)+date_.date+getString(R.string.rating_posting_day)}"
        }else if(time_registration>=24*60*60*1000){ // 등록한지 3일이하 1일이상 (경과 일수 표시)
            //                                              mil  분 시 일
            posting_time_txt.text = "${""+time_registration/1000/60/60/24+getString(R.string.rating_posting_before_day)}"
        }else if(time_registration>=60*60*1000){ // 등록한지 1일이하 ( 경과 시간 표시)
            posting_time_txt.text = "${""+time_registration/1000/60/60+getString(R.string.rating_posting_before_hour)}"
        }else{ // 등록 1시간 이하 (경과 분 표시)
            posting_time_txt.text = "${""+time_registration/1000/60+getString(R.string.rating_posting_before_min)}"
        }

        //댓글 갯수 표시
        rater_recomment_num_txt.text = getString(R.string.rating_posting_recomment_num_f)+post_data.recomment_num + getString(R.string.rating_posting_recomment_num_b)

        //그리드뷰 리프레쉬
//        adapter = Home_rating_adapter(this,post_main_data,photo_select,post_rater_data.rating_result,rate_position,rating_position)

        var click_time = System.currentTimeMillis()
        val non_time = 700
        adapter_asy = Home_rating_adapter_basic(this,post_main_data, photo_select, post_rater_data.rating_result, rate_position,rating_position)
        when(post_main_data.photo_url.size){
            2->{
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                photo_5_layer.visibility = View.GONE
                rating_photo_list.numColumns = 1
            }
            3->{
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                photo_5_layer.visibility = View.GONE
                rating_photo_list.numColumns = 1
            }
            4->{
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                photo_5_layer.visibility = View.GONE
                rating_photo_list.numColumns = 2
            }
            5-> {
                val position_4 = 4
                val anim: Animation = AnimationUtils.loadAnimation(applicationContext, (R.anim.heart_anim))
                //평가 했을 경우
                if (rate_position != HOME_RATING_NON) {
                    rating_photo_like.visibility = View.GONE
                    rating_photo_like_rater.visibility = View.VISIBLE
                    rating_photo_like_num.visibility = View.VISIBLE

                    rating_photo_like_rater.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)
                    rating_photo_like_num.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)

                    if (rating_position == position_4)
                        rating_photo_like_rater.setImageResource(R.mipmap.ic_home_rating_heart)
                    else
                        rating_photo_like_rater.setImageResource(R.mipmap.ic_home_rating_heart_non)

                    rating_photo_like_rater.animation = anim
                    rating_photo_like_rater.startAnimation(anim)

                    rating_photo_enlargement?.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 7, resources.displayMetrics.widthPixels / 2 / 7)

                    val params = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 7, resources.displayMetrics.widthPixels / 2 / 7)
                    params.rightMargin = 10

                    rating_photo_like_rater.layoutParams = params

                    rating_photo_like_num.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)

                    rating_photo_like_num.text = "" + post_rater_data.rating_result[4].toLong()
                    when {
                        post_rater_data.rating_result[position_4].toLong() >= 1000 -> {
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_1000)
                        }
                        post_rater_data.rating_result[position_4].toLong() >= 100 -> {
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_100)
                        }
                        post_rater_data.rating_result[position_4].toLong() >= 10 -> {
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_10)
                        }
                        else -> {
                            rating_photo_like_num.textSize = resources.getDimension(R.dimen.result_text_size_1)
                        }
                    }

                } else { // 평가 안했을시 셋
                    rating_photo_like.visibility = View.VISIBLE
                    rating_photo_like_rater.visibility = View.GONE
                    rating_photo_like_num.visibility = View.GONE
                    rating_photo_enlargement.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)
                    rating_photo_like.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2 / 8, resources.displayMetrics.widthPixels / 2 / 8)
                }

                //확대 버튼
                rating_photo_enlargement.setOnClickListener({
                    if (click_time + non_time < System.currentTimeMillis())
                        Home_rating_adapter_basic.mListener.interface_view_image(
                                post_main_data.photo_url, photo_select, post_main_data.option_info_data.captuer_bool, rate_position, position_4)
                })
                //TODO 사진 롱클릭 했을경우
                rating_photo_list1.setOnLongClickListener({
                    Home_rating_adapter_basic.mListener.interface_view_image(
                            post_main_data.photo_url, photo_select, post_main_data.option_info_data.captuer_bool, rate_position, position_4)
                    return@setOnLongClickListener true
                })
                //5번째 사진 클릭
                rating_photo_list1.setOnClickListener({
                    if (rate_position != HOME_RATING_NON) {
                    } else {
                        if (photo_select[position_4]) { // 하트 취소
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart_non)
                            photo_select[position_4] = false
                            rating_bool = false
                            adapter_asy.notifyDataSetChanged()
                        } else {                       //하트 생성
                            for (i in 0 until post_main_data.photo_url.size)
                                photo_select[i] = false
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart)
                            rating_photo_like.animation = anim
                            rating_photo_like.startAnimation(anim)
                            photo_select[position_4] = true
//                            rate_position = position_4
                            rating_bool = true
                            adapter_asy.notifyDataSetChanged()
                        }
                        Home_rating_adapter_basic.mListener2.interface_photo_clicked(position_4, rating_bool)
                    }
                })
                //TODO 하트 눌렀을경우
                rating_photo_like.setOnClickListener({
                    if (rate_position != HOME_RATING_NON) {
                    } else {
                        if (photo_select[position_4]) { // 하트 취소
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart_non)
                            photo_select[position_4] = false
                            rating_bool = false
                            adapter_asy.notifyDataSetChanged()
                        } else {                       //하트 생성
                            for (i in 0 until post_main_data.photo_url.size)
                                photo_select[i] = false
                            rating_photo_like.setImageResource(R.mipmap.ic_home_rating_heart)
                            rating_photo_like.animation = anim
                            rating_photo_like.startAnimation(anim)
                            photo_select[position_4] = true
//                            rating_position = position_4
                            rating_bool = true
                            adapter_asy.notifyDataSetChanged()
                        }
                        Home_rating_adapter_basic.mListener2.interface_photo_clicked(position_4, rating_bool)
                    }
                })

                //그리드뷰, 5번 이미지 셋
                photo_5_layer.visibility = View.VISIBLE
                photo_5_layer.layoutParams = LinearLayout.LayoutParams(resources.displayMetrics.widthPixels / 2, getStatusBarHeight() / 3)
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels, getStatusBarHeight() / 3 * 2)
                rating_photo_list1.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels / 2, getStatusBarHeight() / 3)
                Glide.with(this)
                        .using(FirebaseImageLoader())
                        .load(storageRef.child(post_main_data.photo_url[position_4]))
                        .centerCrop()
                        .into(rating_photo_list1)
                rating_photo_list.numColumns = 2
            }
            6->{
                photo_5_layer.visibility = View.GONE
                rating_photo_list.layoutParams = RelativeLayout.LayoutParams(resources.displayMetrics.widthPixels , getStatusBarHeight())
                rating_photo_list.numColumns = 2
            }
        }

        rating_photo_list.adapter = adapter_asy
        adapter_asy.notifyDataSetChanged()
        //그리드뷰 사진갯수에 맞게 리사이징


        //선택 옵션 안보이게
        photo_pick_complete.visibility = View.GONE

        //생성자에 수정할 필드만 데이터 넣어서 객체 생성(수정안할건 안건드림)
        val Post_Main_Data_Update = Post_Main_Data(post_main_data.post_rater_num,0)
        //수정할 필드 객체만든거 map형식으로 변형
        val map : Map<String,Any> = Post_Main_Data_Update.raterNum_update()

        val post_rater_data_min = post_rater_data(post_rater_data.rater_num, post_rater_data.rating_result)
        val map2 : Map<String,Any> = post_rater_data_min.rating_update()

        //서버에 투표 현황 투표자 리스트 투표자 수 업데이트
        firestore_.collection("post").document("" + (10000000000000-post_data.save_time)).update(map)

        firestore_.collection(RATING_POST).document("" + (10000000000000-post_data.save_time)).update(map2)

        if(my_post_get_data.getBoolean(user_info.id+"RATE_NON_FIRST",false))
            firestore_.collection("USER_DATA").document(user_info.id).update(mapOf(""+post_data.save_time+ user_info.id to rating_position))
        else {
            firestore_.collection("USER_DATA").document(user_info.id ).set(mapOf("" + post_data.save_time + user_info.id to rating_position))
            my_post_put_data.putBoolean(user_info.id+"RATE_NON_FIRST",true)
        }
//            MainActivity.Photo_Database.child("post").child("" + post_data.save_time).updateChildren(map)
//            MainActivity.Photo_Database.child(RATING_POST).child("" + post_data.save_time).updateChildren(map2)
        //서버에 평가한 포스트 데이터 등록(홈화면에서 표시)

//            MainActivity.Photo_Database.child("USER_DATA").child(RATE_LIST).child("" + user_info.id).updateChildren(mapOf((""+post_data.save_time) to rating_position))

        //shared 에 저장할것 들
        my_post_put_data.putInt(""+post_data.save_time+ user_info.id,rating_position)
        my_post_put_data.commit()

//            MainActivity.my_post_put_data.putInt(pic_pic_string_data.MY_RATING_POST_NUM_CHECK,(rating_post_num+1))
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PHOTO_VIEW_RESULT->{
                //스크롤뷰 투표 후에
                val a = photo_select.size
                photo_select.clear()
                (0 until a).mapTo(photo_select) { data!!.getBooleanExtra(HOME_RATING_SCROLL_HEART_RESULT + it, false) }
                (0 until  photo_select.size)
                        .filter { photo_select[it] }
                        .forEach { rating_position = it }
                rating_bool = data!!.getBooleanExtra(HOME_RATING_SCROLL_HEART_RESULT_BOOL, false)
                if(rating_bool){
                    rating_appbar.setExpanded(true, false)
                }
                else
                    rating_appbar.setExpanded(false, false)
                adapter.notifyDataSetChanged()
                adapter_asy.notifyDataSetChanged()
            }
            COMMENT_REQ ->{
                //댓글 달고 나오면
                post_main_data.recomment_num = data!!.getIntExtra(COMMENT_LIST_NUM_REQ,0)
                rater_recomment_num_txt.text = getString(R.string.rating_posting_recomment_num_f)+ post_main_data.recomment_num + getString(R.string.rating_posting_recomment_num_b)
//                thread {
//                    MainActivity.Photo_Database.child("post").child("" + post_main_data.save_time).setValue(post_main_data)
//                }
            }
            UPDATE_REQ->{
                //수정 하고왓을때
                if(resultCode==123) {
                    post_main_data = data?.getSerializableExtra(POST_UPDATE_DATA) as Post_Main_Data
                    //댓글옵션
                    if (post_main_data.option_info_data.comment_bool)
                        rater_recomment_num_txt.visibility = View.GONE
                    else
                        rater_recomment_num_txt.visibility = View.VISIBLE
                    //캡쳐 옵션
                    if (post_main_data.option_info_data.captuer_bool)
                        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    //글 내용
                    post_comment_txt.text = post_main_data.comment
                }
            }
        }
    }
}