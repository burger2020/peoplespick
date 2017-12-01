package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.instacart.library.truetime.TrueTimeRx
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.list_home_recycle.view.*
import kotlinx.android.synthetic.main.list_home_recycle_footer.view.*
import kotlinx.android.synthetic.main.list_interest_recycle.view.*
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.profile_change_meta
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Search_frag.Companion.search_post_max
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.FOOTER_TYPE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_COMMENT_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.INTEREST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.ITEM_TYPE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.MYPOST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.NON_PROFILE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.POST_CUSTOM_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILE_STATE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.SEARCH
import picturePick.picture.main_user.profile_rating.swipe_fragment.My_Result_frag.Companion.my_post_max
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by main_user on 2017-10-14.
 */


class Home_frag_adapter(val context: Context,val post_Main_Data: ArrayList<Post_Main_Data>, val rating_check : ArrayList<Int>,
                        val comment_check : ArrayList<Boolean>,val section : String,val post_max : Boolean,val interest_Tag_Check:ArrayList<Boolean>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder  //TODO is 쓰면 형변환 자동으로 맞춰짐
        ) {
            is ViewHolder -> {
                holder?.bindHolder(context, post_Main_Data, rating_check, comment_check, position, section,interest_Tag_Check)

//                if(section == HOME || section == SEARCH){
//                    holder?.bindHolder(context, post_Main_Data, rating_check, comment_check, position, section)
//                }else{
//                    holder?.bindHolder(context, post_Main_Data, rating_check, comment_check, position-1, section)
//                }
            }
            is Footer_ViewHolder -> {
                holder?.bindHolder(context, post_max, position, section)
            }
            is Header_ViewHolder -> holder?.bindHolder(context,post_Main_Data,position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
        // TODO item
            ITEM_TYPE -> {
                val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val mainView = inflater.inflate(R.layout.list_home_recycle, parent, false)
                return ViewHolder(mainView)
            }
        // TODO footer
            FOOTER_TYPE -> {
                val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val mainView = inflater.inflate(R.layout.list_home_recycle_footer, parent, false)
                return Footer_ViewHolder(mainView)
            }
        // TODO header
            else -> {
                val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val mainView = inflater.inflate(R.layout.list_home_recycle_header, parent, false)
                return Header_ViewHolder(mainView)
            }
        }
    }

    fun update(){
        notifyDataSetChanged()
    }
    override fun getItemViewType(position: Int): Int {
        return  if (isPositionFooter(position)) {
            FOOTER_TYPE
        } else ITEM_TYPE
//        return if(section == HOME || section == SEARCH)
//            if (isPositionFooter(position)) {
//                FOOTER_TYPE
//            } else ITEM_TYPE
//        else
//            when {
//                isPositionFooter(position) -> FOOTER_TYPE
//                position==0 -> HEADER_TYPE
//                else -> ITEM_TYPE
//            }
    }

    override fun getItemCount(): Int {
        return post_Main_Data.size + 1
//        return if(section == HOME || section == SEARCH) post_Main_Data.size + 1
//        else post_Main_Data.size + 2
    }

    private fun isPositionFooter(position : Int) : Boolean {
        return position == post_Main_Data.size
//        return if(section == HOME || section == SEARCH)position == post_Main_Data.size
//        else position == post_Main_Data.size+1
    }
}
//TODO itme 뷰홀더
class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

    private val mainView = view
    companion object {
        lateinit var mListener_home: interface_select_reting_post
        fun postClick_home(mListener:interface_select_reting_post) { this.mListener_home = mListener }
        lateinit var mListener_search: interface_select_reting_post
        fun postClick_search(mListener:interface_select_reting_post) { this.mListener_search = mListener }
        lateinit var mListener_myPost: interface_select_reting_post
        fun postClick_mypPost(mListener:interface_select_reting_post) { this.mListener_myPost = mListener}
        lateinit var mListener_interest: interface_select_reting_post
        fun postClick_interest(mListener:interface_select_reting_post) { this.mListener_interest = mListener}

        lateinit var mListener1_home: interface_delete_post
        fun postDelete_home(mListener1:interface_delete_post) { this.mListener1_home = mListener1 }
        lateinit var mListener1_search: interface_delete_post
        fun postDelete_search(mListener1:interface_delete_post) { this.mListener1_search = mListener1 }
        lateinit var mListener1_myPost: interface_delete_post
        fun postDelete_myPost(mListener1:interface_delete_post) { this.mListener1_myPost = mListener1 }
        lateinit var mListener1_interest: interface_delete_post
        fun postDelete_interest(mListener1:interface_delete_post) { this.mListener1_interest= mListener1 }

        lateinit var mListener2_home: interface_appPost
        fun postApppost_home(mListener2:interface_appPost) { this.mListener2_home = mListener2 }
        lateinit var mListener2_search: interface_appPost
        fun postApppost_search(mListener2:interface_appPost) { this.mListener2_search = mListener2 }
        lateinit var mListener2_myPost: interface_appPost
        fun postApppost_myPost(mListener2:interface_appPost) { this.mListener2_myPost = mListener2 }
        lateinit var mListener2_interest: interface_appPost
        fun postApppost_interest(mListener2:interface_appPost) { this.mListener2_interest= mListener2 }

        lateinit var mListener2: interface_post_comment_list_open
        fun postCommentList(mListener2:interface_post_comment_list_open) { this.mListener2 = mListener2 }

        lateinit var mListener3: interface_tag_clicked
        fun tagClicked(mListener3:interface_tag_clicked) { this.mListener3 = mListener3 }
    }

    interface interface_appPost{
        fun interface_appPost()
    }
    interface interface_select_reting_post{
        fun interface_delete_image(post_Main_Data: Post_Main_Data,rating_checked : Int)
    }
    interface interface_delete_post{
        fun interface_delete_post(position:Int)
    }
    interface interface_post_comment_list_open{
        fun interface_post_comment_list_open(position:Int,id:Long,comment_num:Int)
    }
    interface interface_tag_clicked{
        fun interface_tag_clicked(tag:String)
    }
    @SuppressLint("ResourceAsColor")
    fun bindHolder(context: Context, post_Main_Data:ArrayList<Post_Main_Data>, rating_check: ArrayList<Int>, comment_check: ArrayList<Boolean>,
                   position:Int, section : String,interest_Tag_Check: ArrayList<Boolean>) {
        val rating_photo = mainView.rating_photos_list
//        val rating_photo_ = mainView.rating_photos_list_
        val rating_photo1 = mainView.grid_img_1
        val rating_photo2 = mainView.grid_img_2
        val rating_comment_txt = mainView.rating_comment_txt
//        val recomment_num_txt = mainView.photo_num_txt
        val rating_time = mainView.rating_time_text
        val rating_posting_time = mainView.post_on_time
        val poster_profile = mainView.poster_profile_image
        val poster_name = mainView.poster_profile_name
        val rating_check_layout = mainView.rating_check_layer
        val rating_comment_layout = mainView.comment_check_layer
        val delete_btn = mainView.delete_button
        val custom_btn = mainView. custom_button
        val TagDividerLayer = mainView.TagDividerLayer
        val TagDividerTxt = mainView.TagDividerTxt


        val border_top_bottom = mainView.rating_photo_container

        if(section== INTEREST)
            if(interest_Tag_Check[position]){
                TagDividerLayer.visibility = View.VISIBLE
                TagDividerTxt.visibility = View.VISIBLE
            }else{
                TagDividerLayer.visibility = View.GONE
                TagDividerTxt.visibility = View.GONE
            }

        if(position == post_Main_Data.size-1&&post_Main_Data.size>8){
            when(section){
                HOME->{
                    mListener2_home.interface_appPost()
                }
                SEARCH->{
                    mListener2_search.interface_appPost()
                }
                MYPOST->{
                    mListener2_myPost.interface_appPost()
                }
                INTEREST->{
                    mListener2_interest.interface_appPost()
                }
            }
            mListener2_home.interface_appPost()
        }

        var TrueTime = try {
            TrueTimeRx.now().time
        }catch (e : IllegalStateException){
            System.currentTimeMillis()
        }

        if(section != MYPOST){
            //프사
            storageRef.child(post_Main_Data[position].poster_profile).metadata.addOnSuccessListener {
                if(it.getCustomMetadata(PROFILE_STATE)== NON_PROFILE){
                    Glide.with(context)
                            .load(R.mipmap.ic_non_profile)
                            .centerCrop()
                            .override(150, 150)
                            .signature(StringSignature(profile_change_meta))
                            .bitmapTransform(CropCircleTransformation(context))
                            .into(poster_profile)
                }else {
                    Glide.with(context)
                            .using(FirebaseImageLoader())
                            .load(storageRef.child(post_Main_Data[position].poster_profile))
                            .centerCrop()
                            .thumbnail(0.3F)
                            .override(150, 150)
                            .signature(StringSignature(profile_change_meta))
                            .bitmapTransform(CropCircleTransformation(context))
                            .into(poster_profile)
                }
            }

            //포스터 이름
            poster_name.text = post_Main_Data[position].poster_name

        }else{
            poster_name.visibility = View.GONE
            poster_profile.visibility = View.GONE
        }

        //등록 시간
        val time_registration : Long =  TrueTime - post_Main_Data[position].save_time
        if(time_registration>=24*60*60*1000*3){ // 등록한지 3일이상
            val date = Date(TrueTime)
            val date_ = Date(post_Main_Data[position].save_time)
            if(date.year == date_.year) // 같은 연도일경우 월.일 만 표시
                rating_posting_time.text = "${""+(date_.month+1)+context.getString(R.string.rating_posting_month)+date_.date+context.getString(R.string.rating_posting_day)}"
            else    //연도 다를경우
                rating_posting_time.text = "${""+(date_.year+1902)+context.getString(R.string.rating_posting_year)+(date_.month+1)+context.getString(R.string.rating_posting_month)+date_.date+context.getString(R.string.rating_posting_day)}"
        }else if(time_registration>=24*60*60*1000){ // 등록한지 3일이하 1일이상 (경과 일수 표시)
            //                                                 mil  분 시 일
            rating_posting_time.text = "${""+time_registration/1000/60/60/24+context.getString(R.string.rating_posting_before_day)}"
        }else if(time_registration>=60*60*1000){ // 등록한지 1일이하 ( 경과 시간 표시)
            rating_posting_time.text = "${""+time_registration/1000/60/60+context.getString(R.string.rating_posting_before_hour)}"
        }else{ // 등록 1시간 이하 (경과 분 표시)
            rating_posting_time.text = "${""+time_registration/1000/60+context.getString(R.string.rating_posting_before_min)}"
        }

        //삭제 수정 아이콘 visible 및 버튼 클릭 이벤트
        if(post_Main_Data[position].poster_token == user_info.id){
            delete_btn.visibility = View. VISIBLE
            custom_btn.visibility = View. VISIBLE
            delete_btn.setOnClickListener({
                //삭제 아이콘
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder
                        .setTitle(R.string.delete_post_title)
                        .setMessage(context.getString(R.string.delete_post))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.delete)
                        ) { dialog, id ->
                            val id = post_Main_Data[position].save_time
                            val uri = post_Main_Data[position].photo_url

                            firestore_.collection("post").document("" + (10000000000000-id)).delete()
                            firestore_.collection(RATING_POST).document("" + (10000000000000-id)).delete()
                            //TODO 나중에 해쉬태그 돼있는거 카운트 다운시켜야함  +  투표한 게시물 개인마다 저장되어있는 데이터 지울방법 모색...
                            for(i in 0 until uri.size)
                                storageRef.child(uri[i]).delete()
                            when(section){
                                HOME->{
                                    mListener1_home.interface_delete_post(position)
                                }
                                SEARCH->{
                                    mListener1_search.interface_delete_post(position)
                                }
                                MYPOST->{
                                    mListener1_myPost.interface_delete_post(position)
                                }
                                INTEREST->{
                                    mListener1_interest.interface_delete_post(position)
                                }
                            }
                            // 삭제 눌렀을떄
                        }
                        .setNegativeButton(context.getString(R.string.Cancel)
                        ) { dialog, id ->
                            // 다이얼로그 취소
                            dialog.cancel()
                        }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            })
            custom_btn.setOnClickListener({
                val intent = Intent(context,Post_Custom_act::class.java)
                intent.putExtra(POST_CUSTOM_DATA, post_Main_Data[position])
                ContextCompat.startActivity(context,intent,null)
            })
        }
        else {
            delete_btn.visibility = View.GONE
            custom_btn.visibility = View.GONE
        }



        //베이스 그리드뷰
        when(post_Main_Data[position].photo_url.size){
            2,4->{
                rating_photo1.visibility =View.GONE
                rating_photo2.visibility =View.GONE

                if(post_Main_Data[position].photo_url.size == 2){
                    rating_photo.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels /3*2)
                    border_top_bottom.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels /3*2)
                }else{
                    rating_photo.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels)
                    border_top_bottom.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels)
                }
                val adapter_asy = Home_frag_grid_adapter_basic(context,post_Main_Data,position,post_Main_Data[position].option_info_data.mosaic_bool)
                rating_photo.numColumns = 2
                rating_photo.adapter = adapter_asy
                adapter_asy.notifyDataSetChanged()
            }
            3,6->{
                rating_photo1.visibility =View.GONE
                rating_photo2.visibility =View.GONE
                if(post_Main_Data[position].photo_url.size == 3){
                    rating_photo.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels /3*2)
                    border_top_bottom.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels /3*2)
                }else {
                    rating_photo.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels)
                    border_top_bottom.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels)
                }
                val adapter_asy = Home_frag_grid_adapter_basic(context,post_Main_Data,position,post_Main_Data[position].option_info_data.mosaic_bool)
                rating_photo.numColumns = 3
                rating_photo.adapter = adapter_asy
                adapter_asy.notifyDataSetChanged()
            }
            5->{
                rating_photo1.visibility =View.VISIBLE
                rating_photo2.visibility =View.VISIBLE

                border_top_bottom.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels)
                rating_photo.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels, context.resources.displayMetrics.widthPixels /2)

                val adapter_asy = Home_frag_grid_adapter_basic(context,post_Main_Data,position,post_Main_Data[position].option_info_data.mosaic_bool)
                rating_photo.numColumns = 3
                rating_photo.adapter = adapter_asy
                adapter_asy.notifyDataSetChanged()

                rating_photo1.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels/2, context.resources.displayMetrics.widthPixels /2)
                rating_photo2.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels/2, context.resources.displayMetrics.widthPixels /2)

                Glide.with(context)
                        .using(FirebaseImageLoader())
                        .load(storageRef.child(post_Main_Data[position].photo_url[3]))
//                        .load(post_Main_Data[position].photo_url[3])
                        .placeholder(R.color.DividerColor_)
                        .centerCrop()
                        .into(rating_photo1)
                Glide.with(context)
                        .using(FirebaseImageLoader())
                        .load(storageRef.child(post_Main_Data[position].photo_url[4]))
//                        .load(post_Main_Data[position].photo_url[4])
                        .placeholder(R.color.DividerColor_)
                        .centerCrop()
                        .into(rating_photo2)
            }
        }

        rating_photo1.setOnClickListener({
            when(section){
                HOME->{
                    mListener_home.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                SEARCH->{
                    mListener_search.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                MYPOST->{
                    mListener_myPost.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                INTEREST->{
                    mListener_interest.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
            }})
        rating_photo2.setOnClickListener({
            when(section){
                HOME->{
                    mListener_home.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                SEARCH->{
                    mListener_search.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                MYPOST->{
                    mListener_myPost.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                INTEREST->{
                    mListener_interest.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
            }})
        //그리드뷰 클릭
        rating_photo.onItemClickListener = AdapterView.OnItemClickListener { view, p1, p2, p3 ->
            when(section){
                HOME->{
                    mListener_home.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                SEARCH->{
                    mListener_search.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                MYPOST->{
                    mListener_myPost.interface_delete_image(post_Main_Data[position],rating_check[position])
                }
                INTEREST->{

                }
            }
        }
//        }
//그리드뷰 셋 끝

        //평가한 게시물은 포스트에 체크표시
        if(rating_check[position] != HOME_RATING_NON){
            rating_check_layout.visibility = View.VISIBLE
        }
        else
            rating_check_layout.visibility = View.GONE
        //댓글 표시 TODO@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        if(comment_check[position] != HOME_COMMENT_NON){
            rating_comment_layout.visibility = View. VISIBLE
        }
        else
            rating_comment_layout.visibility = View.GONE

        //코멘트에 태그 클릭스팬 넣기
        val Tag_start_position = arrayListOf<Int>()
        val Tag_end_position = arrayListOf<Int>()
        val Tag_text = arrayListOf<String>()
        var tag_bool = false
        var tag_unit = ""
        var ss_comment = SpannableString(post_Main_Data[position].comment)

        for(i in 0 until post_Main_Data[position].comment.length){
            if(post_Main_Data[position].comment[i] == '#'){
                tag_bool = true
            }else if(tag_bool&&(post_Main_Data[position].comment[i] == ' ')){
                tag_bool = false
                if(tag_unit.isNotEmpty()) {
                    //TODO 스팬 다는곳
                    Tag_start_position.add(i-tag_unit.length-1)
                    Tag_end_position.add(i-1)
                    Tag_text.add(tag_unit)
                    tag_unit = ""
                }
            }else if(tag_bool&&(i == post_Main_Data[position].comment.length-1)){
                tag_bool = false
                tag_unit += post_Main_Data[position].comment[i]
                //TODO 스팬 다는곳
                Tag_start_position.add(i-tag_unit.length)
                Tag_end_position.add(i)
                Tag_text.add(tag_unit)
                tag_unit = ""
            }else if(tag_bool&&post_Main_Data[position].comment[i] != ' '){
                tag_unit +=post_Main_Data[position].comment[i]
            }
        }

        for(i in 0 until Tag_start_position.size){
            ss_comment.setSpan(
                    object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint?) {
                            ds?.color = context.resources.getColor(R.color.colorAccent)    // you can use custom color
                            ds?.isUnderlineText = false    // this remove the underline
                        }
                        override fun onClick(widget: View) {
                            widget.invalidate()
                            mListener3.interface_tag_clicked(Tag_text[i])
                        }
                    }
                    ,Tag_start_position[i]
                    ,Tag_end_position[i]+1
                    ,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        //코멘트 표시
        rating_comment_txt.movementMethod = LinkMovementMethod.getInstance()
        rating_comment_txt.setText(ss_comment,TextView.BufferType.SPANNABLE)

        //사진 갯수
//        recomment_num_txt.text = "+ "+ (post_Main_Data[position].photo_list_num-1)

        //포스트 등록 경과시간 및 올린 날짜


        val time_remaining : Long = post_Main_Data[position].rating_end_time - TrueTime

        // 남은시간 표시
        var ss: SpannableString
        var time_text: String
        var rater_num_text: String
        var recommnet_num_text: String

        //댓글창 열기
        val clicked_span = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint?) {
                super.updateDrawState(ds)
                ds?.isUnderlineText = false    // this remove the underline
            }

            override fun onClick(widget: View) {
                widget.invalidate()
                mListener2.interface_post_comment_list_open(position,post_Main_Data[position].save_time,post_Main_Data[position].recomment_num)
            }
        }

        when {
            time_remaining>3600000 -> { // 1시간 이상
                time_text = "${context.getString(R.string.rating_remaining_time)} : " +
                        "${(""+(time_remaining/1000-time_remaining/1000/60%60)/3600) + context.getString(R.string.rating_remaining_hour) + time_remaining/1000/60%60+context.getString(R.string.rating_remaining_min)}"
                rater_num_text = ""+post_Main_Data[position].post_rater_num +context.getString(R.string.rating_num_)
                recommnet_num_text= context.getString(R.string.rating_posting_recomment_num_f)+post_Main_Data[position].recomment_num + context.getString(R.string.rating_posting_recomment_num_b)

            }
            time_remaining>0 -> {  // 1시간 이하
                time_text = "${context.getString(R.string.rating_remaining_time)} : ${""+time_remaining/1000/60+context.getString(R.string.rating_remaining_min)}"
                rater_num_text= ""+post_Main_Data[position].post_rater_num + context.getString(R.string.rating_num_)
                recommnet_num_text= context.getString(R.string.rating_posting_recomment_num_f)+post_Main_Data[position].recomment_num + context.getString(R.string.rating_posting_recomment_num_b)

            }else -> {  // 평가시간 끝남
            time_text = context.getString(R.string.home_rating_time_over)
            rater_num_text= ""+post_Main_Data[position].post_rater_num + context.getString(R.string.rating_num_end)
            recommnet_num_text= context.getString(R.string.rating_posting_recomment_num_f)+post_Main_Data[position].recomment_num + context.getString(R.string.rating_posting_recomment_num_b)
        }
        }

        if(post_Main_Data[position].option_info_data.comment_bool)
            recommnet_num_text=""

        ss = SpannableString(time_text+"\n"+rater_num_text+"\n"+recommnet_num_text)
        //클릭스팬
        ss.setSpan(clicked_span,
                time_text.length+1+rater_num_text.length+1
                ,time_text.length+1+rater_num_text.length+1+recommnet_num_text.length
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //댓글 갯수 표시

        //스팬 컬러
        if(time_text == context.getString(R.string.home_rating_time_over)) {
            //평가 끝
            ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.DividerColor)),
                    0
                    , time_text.length
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.SecondaryTextColor)),
                    time_text.length+1
                    , time_text.length+1+rater_num_text.length + 1 + recommnet_num_text.length
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        else{
            ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.PrimaryTextColor)),
                    0
                    , time_text.length + 1 + rater_num_text.length + 1 + recommnet_num_text.length
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(ForegroundColorSpan(context.resources.getColor(R.color.SecondaryTextColor)),
                    time_text.length + 1 + rater_num_text.length + 1
                    , time_text.length + 1 + rater_num_text.length + 1 + recommnet_num_text.length
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        rating_time.text = ss
        rating_time.movementMethod = LinkMovementMethod.getInstance()

    }

    private inner class DownloadImageTask(internal var bmImage: ImageView,context: Context) : AsyncTask<String, Void, Bitmap>() {

        val context = context
        override fun doInBackground(vararg urls: String): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = java.net.URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }
            return mIcon11
        }

        override fun onPostExecute(result: Bitmap) {
            val stream = ByteArrayOutputStream()
            result.compress(Bitmap.CompressFormat.PNG,100,stream)
            Glide.with(context).load(stream.toByteArray()).thumbnail(0.1F).into(bmImage)
        }
    }
}


//TODO footer 뷰홀더
class Footer_ViewHolder(view: View) : RecyclerView.ViewHolder(view){
    private val mainView = view
    fun bindHolder(context: Context, post_max:Boolean,position:Int,section:String) {
        val progressbar = mainView.progressbar
        Log.d("collection,post_max11",""+post_max)
        when(section){
            HOME->{
                progressbar.visibility=View.VISIBLE
            }
            INTEREST->{
                progressbar.visibility = View.GONE
            }
            MYPOST->{
                if(my_post_max)
                    progressbar.visibility = View.GONE
                else
                    progressbar.visibility=View.VISIBLE
            }
            SEARCH->{
                if(search_post_max)
                    progressbar.visibility = View.GONE
                else
                    progressbar.visibility=View.VISIBLE

            }
        }
    }
}
//TODO footer 뷰홀더
class Header_ViewHolder(view: View) : RecyclerView.ViewHolder(view){
    fun bindHolder(context: Context, Post_Main_Data:ArrayList<Post_Main_Data>,position:Int) {
    }
}