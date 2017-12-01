package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.StorageMetadata
import com.soundcloud.android.crop.Crop
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.activity_my_result_frag.view.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_get_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_profile_change_meta
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_profile_state
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.profile_change_meta
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_basic_data.Companion.rating_list_add
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.CUSTOM_PROFILE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_COMMENT_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.INIT_DATA_NUM
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.MYPOST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.NON_PROFILE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILE_CHANGE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILE_STATE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.post_rater_data
import java.io.ByteArrayOutputStream


//프래그먼트 생성시 임폴트에
//import android.support.v4.app.Fragment
//이거로 바꿔줘야함!
class My_Result_frag : Fragment() {

    companion object {
        var visible_myFrag = true
        lateinit var rating_list : RecyclerView
        private var onResume_ = false
        private var my_post_data = ArrayList<Post_Main_Data>()
        private var my_post_rating_check: ArrayList<Int> = arrayListOf()
        private var my_post_comment_check: ArrayList<Boolean> = arrayListOf()
        var my_post_max = false
        fun newInstance(Post_save_data : ArrayList<Post_Main_Data>,post_check_save : ArrayList<Int>): My_Result_frag {
            val fragment = My_Result_frag()
            my_post_data = Post_save_data
            my_post_rating_check = post_check_save
            onResume_ = my_post_data.size != 0
            return fragment
        }

        lateinit var mListener: interface_my_frag_destroy_save_data
        fun my_frag_destroyed(mListener: interface_my_frag_destroy_save_data) { this.mListener = mListener }
    }

    interface interface_my_frag_destroy_save_data {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_my_frag_destroy_save_data(post_Main_Data: ArrayList<Post_Main_Data>,post_check_save :ArrayList<Int>,my_post_comment_check:ArrayList<Boolean>)
    }

    override fun onPause() {
        visible_myFrag = false
        mListener.interface_my_frag_destroy_save_data(my_post_data, my_post_rating_check,my_post_comment_check)
        super.onPause()
    }
    lateinit var post_loading : OnCompleteListener<QuerySnapshot>
    private lateinit var adapter : Home_frag_adapter
    private var total_post_num_old : Long = 0
    private var total_post_num : Long = 0
    private var view_post_num : Int = pic_pic_string_data.INIT_DATA_NUM
    private var post_add_bot : Boolean = true // 포스트 리스트 추가 중복 안되도록
    private var post_refrash : Boolean = true // 포스트 리스트 추가 다했을때 바닥 닿을시 로드 가능하게
    private var post_click_bool : Boolean = false // 포스트 클릭 중복 방지
    @SuppressLint("ResourceType")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.activity_my_result_frag, container, false)
        rating_list = rootView.my_post_rating_list
        val mSwipeRefreshLayout = rootView.my_post_swipe_layout  // 새로고침 하는 스와이프리프레쉬 레이아웃
        val post_empty_txt = rootView.myPost_empty_text
        val my_profile_name = rootView.my_profile_name

        val my_profile_images = rootView.my_profile_images
        adapter = Home_frag_adapter(context, my_post_data,my_post_rating_check,my_post_comment_check, MYPOST, my_post_max, arrayListOf())


//        my_profile_background.scaleType = ImageView.ScaleType.CENTER_CROP
        //프로필 사진 이름 세팅
        //기본 프로필사진 아닐경우
        MainActivity.ProfileSet(object :MainActivity.profileSet{
            override fun profileSet() {
                if(my_profile_state == CUSTOM_PROFILE)
                    try {
                        Glide.with(context)
                                .using(FirebaseImageLoader())
                                .load(storageRef.child(user.photoUrl.toString()))
                                .signature(StringSignature(my_profile_change_meta))
                                .centerCrop()
                                .bitmapTransform(CropCircleTransformation(context))
                                .override(150,150)
                                .into(my_profile_images)
                    }catch (e : IllegalArgumentException){}
            }
        })
        if(my_profile_state == CUSTOM_PROFILE) {
            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(storageRef.child(user.photoUrl.toString()))
                    .signature(StringSignature(my_profile_change_meta))
                    .centerCrop()
                    .bitmapTransform(CropCircleTransformation(context))
                    .override(150,150)
                    .into(my_profile_images)
        }

        my_profile_name.text = user_info.dis_name

        //프사 클릭시 변경 옵션
        my_profile_images.setOnClickListener({
            val selelct_list = (R.array.profile_image_change)
            val builder = AlertDialog.Builder(context)     // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정
            builder.setTitle(getString(R.string.profile_dialog_title))        // 제목 설정
                    .setItems(selelct_list, { dialog, index ->
                        // 목록 클릭시 설정
                        when(index){
                            0->{//갤러리
                                Crop.pickImage(activity)
                            }
//                            1->{//카메라
//                                checkPermission()
//                                val metadata : StorageMetadata = StorageMetadata.Builder()
//                                        .setCustomMetadata(PROFILE_STATE,NON_PROFILE)
//                                        .build()
//                            }
                            1->{
                                storageRef.child("/PROFILE/"+user?.uid).metadata.addOnSuccessListener {
                                    //기본 프로필 일경우 그냥 그대로 유지하고 아닐경우만 다시 넣어주기
                                    if(my_profile_state == CUSTOM_PROFILE){
                                        my_profile_state = NON_PROFILE
                                        my_profile_change_meta = System.currentTimeMillis().toString()
                                        profile_change_meta = (System.currentTimeMillis()+1).toString()
                                        Glide.with(context)
                                                .load(R.mipmap.ic_non_profile)
                                                .signature(StringSignature(NON_PROFILE))
                                                .centerCrop()
                                                .bitmapTransform(CropCircleTransformation(context))
                                                .override(150,150)
                                                .into(my_profile_images)
                                        val metadata : StorageMetadata = StorageMetadata.Builder()
                                                .setCustomMetadata(PROFILE_STATE, my_profile_state)
                                                .setCustomMetadata(PROFILE_CHANGE, my_profile_change_meta )
                                                .build()

                                        val icon = BitmapFactory.decodeResource(context.resources,R.drawable.ic_non_profile)
                                        val baos = ByteArrayOutputStream()
                                        icon.compress(Bitmap.CompressFormat.JPEG,100,baos)
                                        val data = baos.toByteArray()
                                        val uploadTask = storageRef.child("/PROFILE/"+user?.uid).putBytes(data,metadata)
                                        uploadTask.addOnSuccessListener {
                                            Log.d("successsuccess","success")
                                        }
                                    }
                                }
                            }
                        }
                    })

            val dialog = builder.create()    // 알림창 객체 생성
            dialog.show()    // 알림창 띄우기
        })
        //프사 변경 / 프사 클릭시

        //리사이클러뷰 어뎁터 및 초기화
        rating_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rating_list.adapter = adapter
        //데이터 들고오기~~~~~~~~~~~~~~~~~~~~~
        post_loading = OnCompleteListener { it ->
            total_post_num_old = my_post_data.size.toLong()
            my_post_data.clear()
            my_post_rating_check.clear()
            my_post_comment_check.clear()

            it.result.mapTo(my_post_data) { (it.toObject(Post_Main_Data::class.java)) }

            my_post_max = my_post_data.size%5 !=0 || my_post_data.size.toLong() == total_post_num_old
//            try {
//                visibleLast = it.result.documents[it.result.size()-1]
//
//            }catch (e : IndexOutOfBoundsException){
//            }

            //보여질 포스트갯수만큼 체크 리스트 자리 만들어주기
            for(i in 0 until my_post_data.size) {
                my_post_rating_check.add(HOME_RATING_NON)
                my_post_comment_check.add(HOME_COMMENT_NON)
            }
            total_post_num = my_post_data.size.toLong()
            if(post_add_bot==false) {
                if (total_post_num == total_post_num_old) {
                    try {
//                        Toast.makeText(context, getText(R.string.home_no_more_post), Toast.LENGTH_LONG).show()
                        view_post_num -= rating_list_add
//                        rating_list.scrollToPosition(my_post_data.size-1)
                    }catch (e : IllegalStateException){}
                }
            }

            //내가 투표했는지 비교하고 투표 체크 표시 데이터 넣기
            for(i in 0 until my_post_data.size){
                val position = my_post_get_data.getInt(""+my_post_data[i].save_time + user_info.id, HOME_RATING_NON)
                if(position != HOME_RATING_NON){
                    my_post_rating_check[i] = position
                }
                val commet_position = my_post_get_data.getBoolean("comment"+my_post_data[i].save_time+ user_info.id, HOME_COMMENT_NON)
                if(commet_position != HOME_COMMENT_NON){
                    my_post_comment_check[i] = commet_position
                }
                if(i == my_post_data.size-1){
                    mSwipeRefreshLayout.isRefreshing = false
                    post_refrash = true
                    post_add_bot= true

                    adapter.update()
                    rating_list.setOnTouchListener(null)
                }
            }
            if(my_post_data.size==0){
                //검색 데이터 없을 경우
                mSwipeRefreshLayout.visibility = View.GONE
                post_empty_txt.visibility = View.VISIBLE
            }
            else{
                mSwipeRefreshLayout.visibility = View.VISIBLE
                post_empty_txt.visibility = View.GONE
            }
        }

        //포스트 추가
        ViewHolder.postApppost_myPost(object : ViewHolder.interface_appPost{
            override fun interface_appPost() {
                if(post_add_bot&&!my_post_max) {
                    try {
                        post_add_bot = false
                        view_post_num += rating_list_add
                        firestore_.collection("post")
                                .whereEqualTo("poster_token",user_info.id)
                                .limit(view_post_num.toLong()).get()
                                .addOnCompleteListener(post_loading)
                    } catch (e: UninitializedPropertyAccessException) {
                    }
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener {
            //리프레쉬할 코드(올려서 새로고침)
            rating_list.setOnTouchListener { p0, p1 -> true }
            post_refrash = false
            view_post_num = INIT_DATA_NUM
            //리프레쉬하는동안 스크롤 막기
            firestore_.collection("post").whereEqualTo("poster_token",user_info.id).limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
        }

        //포스트 삭제
        ViewHolder.postDelete_myPost(object :ViewHolder.interface_delete_post{
            override fun interface_delete_post(position: Int) {
                my_post_data.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })
        //포스트선택시 평가페이지로 넘어가기
        ViewHolder.postClick_mypPost(object : ViewHolder.interface_select_reting_post{
            override fun interface_delete_image(post_data: Post_Main_Data, rating_checked: Int) {
                if(post_click_bool==false) {
                    post_click_bool = true

                    (activity as MainActivity).progress.visibility = View.VISIBLE
                    firestore_.collection(RATING_POST).document(""+(10000000000000-post_data.save_time)).get().addOnCompleteListener({
                        var post_rater_data = it.result.toObject(post_rater_data::class.java)

                        post_data.post_rater_num = post_rater_data.rater_num

                        post_clicked(post_data, post_rater_data,rating_checked)
                    })

                }
            }
        })
        return rootView
    }


    fun post_clicked(post_main_data : Post_Main_Data, post_rater_data : post_rater_data, rating_checked : Int){
        val intent = Intent(context,Home_raing_activity::class.java)
        intent.putExtra(pic_pic_string_data.HOME_RATING_DATA,post_main_data)    //포스트 기본정보
        intent.putExtra(pic_pic_string_data.HOME_RATING_COMMENT_DATA, post_rater_data)  // 포스트 댓글, 하트 갯수
        intent.putExtra(pic_pic_string_data.HOME_RATING_RATING_BOOL, rating_checked)//평가했는지 체크
//        intent.putExtra(HOME_RATING_RATING_BOOL, true)  //TODO 테스트용
        startActivityForResult(intent, pic_pic_string_data.RATING_COMPLETE)
        (activity as MainActivity).progress.visibility = View.GONE
        post_click_bool = false
    }

    override fun onResume() {
        super.onResume()
        visible_myFrag = true
        load_data()
    }

    fun load_data(){

        //태그 또는 팔로잉한사람 게시물 선택해서 ㅗ봐야하는데 데이타베이스로는 안됨(여러게 검색)
//        MainActivity.Photo_Database.child("post").orderByChild("poster_token").equalTo(user_info.id).limitToLast(view_post_num).addListenerForSingleValueEvent(childEventListener1)
        //메인액티비티에서 데이터 저장해서 보내주면 로드 안함
        if(!onResume_)
            firestore_.collection("post")
                    .whereEqualTo("poster_token",user_info.id)
                    .limit(INIT_DATA_NUM.toLong())
                    .get()
                    .addOnCompleteListener(post_loading)
    }

}