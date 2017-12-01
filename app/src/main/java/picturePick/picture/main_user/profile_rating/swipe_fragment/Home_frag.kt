package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_home_frag.view.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_get_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_basic_data.Companion.rating_list_add
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_NUM
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_SAVE_TIME
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.COMMENT_LIST_VIEW
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_COMMENT_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_COMMENT_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_RATING_BOOL
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.INIT_DATA_NUM
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_COMPLETE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.post_rater_data


//프래그먼트 생성시 임폴트에
//import android.support.v4.app.Fragment
//이거로 바꿔줘야함!
class Home_frag : Fragment() {
    private val COMMENT_CUSTOM_REP = 1234
    private var view_post_num : Int = INIT_DATA_NUM
    private lateinit var visibleLast :DocumentSnapshot
    companion object {

        private var onResume_ = false
        private var post_Main_Data: ArrayList<Post_Main_Data> = arrayListOf()
        private var post_rating_check: ArrayList<Int> = arrayListOf()
        private var post_comment_check: ArrayList<Boolean> = arrayListOf()
        lateinit var rating_list :  RecyclerView
        var visible_homeFrag: Boolean = true
        fun newInstance(Post_save_data : ArrayList<Post_Main_Data>,post_check_save : ArrayList<Int>,post_commnet_check : ArrayList<Boolean>): Home_frag {
            val fragment = Home_frag()
            post_Main_Data = Post_save_data
            post_rating_check = post_check_save
            post_comment_check = post_commnet_check
            onResume_ = post_Main_Data.size != 0
            return fragment
        }
        lateinit var mListener: interface_destroy_save_data
        fun main_frag_destroyed(mListener:interface_destroy_save_data) { this.mListener = mListener }
    }

    interface interface_destroy_save_data {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_destroy_save_data(post_Main_Data: ArrayList<Post_Main_Data>,post_check_save :ArrayList<Int>,post_commnet_check: ArrayList<Boolean>)
    }

    //    lateinit var childEventListener1 : ValueEventListener
    lateinit var post_loading : OnCompleteListener<QuerySnapshot>
    private var total_post_num : Long = 0
    private var total_post_num_old : Long = 0
    private var post_create : Boolean = false
    private var post_add_bot : Boolean = true // 포스트 리스트 추가 중복 안되도록
    private var post_refrash : Boolean = true // 포스트 리스트 추가 다했을때 바닥 닿을시 로드 가능하게
    private var post_click_bool : Boolean = false // 포스트 클릭 중복 방지
    private lateinit var adapter : Home_frag_adapter
    private var post_max = false
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.activity_home_frag, container, false)

        rating_list = rootView.home_rating_list
        val mSwipeRefreshLayout = rootView.swipe_layout  // 새로고침 하는 스와이프리프레쉬 레이아웃
        adapter = Home_frag_adapter(context, post_Main_Data,post_rating_check, post_comment_check, HOME,post_max, arrayListOf())

        //리사이클러뷰 어뎁터 및 초기화
        rating_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rating_list.adapter = adapter

        //TODO 파이어베이스 데이터베이스 최근 10개 목록 데이터 가져오기
        //데이터 가져오는 부분

        post_loading = OnCompleteListener { it ->
            total_post_num_old = post_Main_Data.size.toLong()
            if(post_add_bot)
                post_Main_Data.clear()
            post_rating_check.clear()
            post_comment_check.clear()

            it.result.mapTo(post_Main_Data) { (it.toObject(Post_Main_Data::class.java)) }

            try {
                visibleLast = it.result.documents[it.result.size()-1]

            }catch (e : IndexOutOfBoundsException){
            }

            //보여질 포스트갯수만큼 체크 리스트 자리 만들어주기
            for(i in 0 until post_Main_Data.size) {
                post_rating_check.add(HOME_RATING_NON)
                post_comment_check.add(HOME_COMMENT_NON)
            }

            total_post_num = post_Main_Data.size.toLong()
            if(!post_add_bot) {
                if (total_post_num == total_post_num_old) {
//                    try {
//                        Toast.makeText(context, getText(R.string.home_no_more_post), Toast.LENGTH_LONG).show()
//                    }catch (e:IllegalStateException){}
                    view_post_num -= rating_list_add
//                    rating_list.scrollToPosition(post_Main_Data.size-1)
                }
            }

            //내가 투표했는지 비교하고 투표 체크 표시 데이터 넣기
            for(i in 0 until post_Main_Data.size){
                //평가했는지 데이터 가져오기
                val position = my_post_get_data.getInt(""+post_Main_Data[i].save_time+ user_info.id, HOME_RATING_NON)
                if(position != HOME_RATING_NON){
                    post_rating_check[i] = position
                }
                //댓글 달았는지 데이터 가져오기
                val comment_position = my_post_get_data.getBoolean("comment"+post_Main_Data[i].save_time+ user_info.id, HOME_COMMENT_NON)
                if(comment_position != HOME_COMMENT_NON){
                    post_comment_check[i] = comment_position
                }

                if(i == post_Main_Data.size-1){
                    mSwipeRefreshLayout.isRefreshing = false
                    post_refrash = true
                    post_add_bot= true
                    adapter.notifyDataSetChanged()
                    rating_list.setOnTouchListener(null)
                }
            }
        }
//            }catch (e: FirebaseFirestoreException){}
//        }catch (e: RuntimeExecutionException){
//            firestore_.collection("post").limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
//        }

        ViewHolder.postApppost_home(object : ViewHolder.interface_appPost{
            override fun interface_appPost() {
                if(post_add_bot) {
                    try {
                        post_add_bot = false
                        view_post_num += rating_list_add
                        firestore_.collection("post").startAfter(visibleLast).limit(rating_list_add.toLong()).get().addOnCompleteListener(post_loading)
                    } catch (e: UninitializedPropertyAccessException) {
                    }
                    Log.d("collect", "collect")
                }
            }
        })
        //바닥 닿을시 처리
//        var lastItemVisibleFlag = false
//        rating_list.setOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView?, ScrollState: Int) {
//                if (ScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag&&post_add_bot) {
//                    //화면이 바닥에 닿을때 포스트 rating_list_add 개 더들고오기
////                    if (total_post_num >= view_post_num + rating_list_add) { // 남은포스트가 10개 이상일경우
//
////                    MainActivity.Photo_Database.child("post").limitToLast(view_post_num).addListenerForSingleValueEvent(childEventListener1)
////                    firestore_.collection("post").limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
//                }
//            }
//            override fun onScrolled(recyclerView: RecyclerView?, firstVisibleItem: Int, visibleItemCount: Int) {
//                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
//                lastItemVisibleFlag = (post_Main_Data.size > 0) && (firstVisibleItem + visibleItemCount >= post_Main_Data.size)
//            }
//        })

        mSwipeRefreshLayout.setOnRefreshListener {
            //리프레쉬할 코드(올려서 새로고침)
            rating_list.setOnTouchListener { p0, p1 -> true }
            post_refrash = false
            view_post_num = INIT_DATA_NUM
            //리프레쉬하는동안 스크롤 막기
//            MainActivity.Photo_Database.child("post").limitToLast(view_post_num).addListenerForSingleValueEvent(childEventListener1)

            firestore_.collection("post").limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
        }

        //데이터 저장소에 저장후 인터페이스로 받아와서 저장
        MainActivity.Create_Posts(object : MainActivity.interface_photo_list_edit_complate{
            override fun interface_photo_list_edit_complate(post_Main_Data1: Post_Main_Data) {
//                post_Main_Data.add(0,post_Main_Data)
//                data_time = ""+post_Main_Data.save_id
//                adapter.updateData()
                rating_list.scrollToPosition(0)
                post_create = true
//                MainActivity.Photo_Database.child("total_post_num").addListenerForSingleValueEvent(Total_Post_Num_Listener1)
                view_post_num = INIT_DATA_NUM
//                MainActivity.Photo_Database.child("post").limitToLast(view_post_num).addListenerForSingleValueEvent(childEventListener1)

                firestore_.collection("post").limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
            }
        })
        //댓글 보기 선택
        ViewHolder.postCommentList(object :ViewHolder.interface_post_comment_list_open{
            override fun interface_post_comment_list_open(position: Int, id: Long, comment_num: Int) {
                val intent = Intent(context, Home_posting_comment::class.java)
                intent.putExtra(COMMENT_LIST_SAVE_TIME,id)
                intent.putExtra(COMMENT_LIST_NUM,comment_num)
                intent.putExtra(COMMENT_LIST_VIEW,true)
                startActivityForResult(intent,COMMENT_CUSTOM_REP)
            }
        })
        //포스트 삭제
        ViewHolder.postDelete_home(object :ViewHolder.interface_delete_post{
            override fun interface_delete_post(position: Int) {
                post_Main_Data.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })
        //포스트선택시 평가페이지로 넘어가기 TODO 프래그먼트로 띄우는거로 바꾸는중...
        ViewHolder.postClick_home(object : ViewHolder.interface_select_reting_post{
            override fun interface_delete_image(post_data: Post_Main_Data, rating_checked: Int) {
                if(!post_click_bool) {
                    post_click_bool = true
//                    thread {
                    (activity as MainActivity).progress.visibility = View.VISIBLE
                    firestore_.collection(RATING_POST).document("" + (10000000000000-post_data.save_time)).get().addOnCompleteListener({
                        val post_rater_data = it.result.toObject(post_rater_data::class.java)

                        post_data.post_rater_num = post_rater_data.rater_num

                        post_clicked(post_data, post_rater_data,rating_checked)
                    })
//                    }
                }
            }
        })
        return rootView
    }

    //포스트 클릭하고 평가페이지로 넘어갈때 인터페이스< TODO 평가페이지 프래그먼트로 변경중..
    fun post_clicked(post_main_data : Post_Main_Data, post_rater_data : post_rater_data,rating_checked : Int){
        val intent = Intent(context,Home_raing_activity::class.java)

        intent.putExtra(HOME_RATING_DATA,post_main_data)    //포스트 기본정보
        intent.putExtra(HOME_RATING_COMMENT_DATA, post_rater_data)  // 포스트 댓글, 하트 갯수
        intent.putExtra(HOME_RATING_RATING_BOOL, rating_checked)//평가했는지 체크

//        intent.putExtra(HOME_RATING_RATING_BOOL, true)  //TODO 테스트용

        (activity as MainActivity).progress.visibility = View.GONE
        startActivityForResult(intent, RATING_COMPLETE)
        post_click_bool = false
    }

    override fun onResume() {
        super.onResume()
        visible_homeFrag = true
        //리쥼될시 포스트 리프레쉬
        if(!onResume_) {
//            thread {
            if((view_post_num.toLong())<INIT_DATA_NUM)
                view_post_num= INIT_DATA_NUM
            firestore_.collection("post").limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)

//            if(!visibleBool)
//                firestore_.collection("post").limit(INIT_DATA_NUM.toLong()).get().addOnCompleteListener(post_loading)
//            else
//                firestore_.collection("post").startAfter(visibleLast).limit(INIT_DATA_NUM.toLong()).get().addOnCompleteListener(post_loading)
        }
        else{
        }
//        firestore_.collection("post").whereGreaterThanOrEqualTo("TAG","김").whereLessThanOrEqualTo("TAG","김\uf8ff")
//                .limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
    }

    override fun onPause() {
        visible_homeFrag=false
        mListener.interface_destroy_save_data(post_Main_Data, post_rating_check, post_comment_check)
        super.onPause()
    }
}