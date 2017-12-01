package picturePick.picture.main_user.profile_rating.bottom_menu_activity

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
import kotlinx.android.synthetic.main.fragment_search_frag.view.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_get_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_put_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_basic_data
import picturePick.picture.main_user.profile_rating.data.pic_basic_data.Companion.rating_list_add
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_COMMENT_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.SEARCH
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.SEARCH_HISTORY_LIST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.SEARCH_HISTORY_LIST_NUM
import picturePick.picture.main_user.profile_rating.data.post_rater_data
import picturePick.picture.main_user.profile_rating.swipe_fragment.Home_frag_adapter
import picturePick.picture.main_user.profile_rating.swipe_fragment.Home_raing_activity
import picturePick.picture.main_user.profile_rating.swipe_fragment.ViewHolder


class Search_frag : Fragment() {

    lateinit var post_searching : OnCompleteListener<QuerySnapshot>
    private lateinit var adapter : Home_frag_adapter
    private lateinit var history_adapter : Search_hostory_adapter
    private var total_post_num_old : Long = 0
    private var total_post_num : Long = 0
    private var view_post_num : Int = pic_pic_string_data.INIT_DATA_NUM
    private var post_add_bot : Boolean = false // 포스트 리스트 추가 중복 안되도록
    private var post_refrash : Boolean = true // 포스트 리스트 추가 다했을때 바닥 닿을시 로드 가능하게
    private var post_click_bool : Boolean = false // 포스트 클릭 중복 방지
    private var search_history_list : ArrayList<String> = arrayListOf()

    companion object {
        var search_post_max = false
        private var tagClicked = false
        private var searching_tag : String = ""
        private var search_post_data = ArrayList<Post_Main_Data>()
        private var search_post_rating_check: ArrayList<Int> = arrayListOf()
        private var search_post_comment_check: ArrayList<Boolean> = arrayListOf()
        private var search_flag = 0
        private var onResume_ = false
        fun newInstance(Post_save_data : ArrayList<Post_Main_Data>,post_check_save : ArrayList<Int>,search_flag: Int,post_comment_check : ArrayList<Boolean>): Search_frag{
            val fragment = Search_frag()
            this.search_flag = search_flag
            search_post_data = Post_save_data
            search_post_rating_check = post_check_save
            search_post_comment_check = post_comment_check
            onResume_ = search_post_data.size != 0
            tagClicked=false
            return fragment
        }
        fun searchInstance(Post_save_data : ArrayList<Post_Main_Data>,post_check_save : ArrayList<Int>,post_comment_check : ArrayList<Boolean>,search_flag: Int,
                           tag:String,tagClicked:Boolean): Search_frag{
            //메인액티비티 앱바 내리기
            val fragment = Search_frag()
            searching_tag = tag
            this.tagClicked = tagClicked
            this.search_flag = search_flag
            search_post_data = arrayListOf()
            search_post_rating_check = post_check_save
            search_post_comment_check = post_comment_check
            onResume_ = search_post_data.size != 0
            return fragment
        }
        lateinit var mListener: interface_search_frag_destroy_save_data
        fun search_frag_destroyed(mListener: interface_search_frag_destroy_save_data) { this.mListener = mListener }
    }
    interface interface_search_frag_destroy_save_data {
        fun interface_search_frag_destroy_save_data
                (post_Main_Data: ArrayList<Post_Main_Data>,post_check_save :ArrayList<Int>,searching_tag:String,search_comment_check : ArrayList<Boolean>,search_flag:Int)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater.inflate(R.layout.fragment_search_frag, container, false)
        val rating_list = rootView.search_post_rating_list
        val empty_view = rootView.search_empty_text
        val mSwipeRefreshLayout = rootView.search_post_swipe_layout  // 새로고침 하는 스와이프리프레쉬 레이아웃
        val search_history_layer = rootView.search_history_layer
        val search_result_layer = rootView.search_result_layer
        val tag_history_list = rootView.tag_history_list


        if(search_flag==0){
            search_result_layer.visibility = View.GONE
            search_history_layer.visibility = View.VISIBLE
        }else if(search_flag==1||search_flag==2){
            search_result_layer.visibility = View.VISIBLE
            search_history_layer.visibility = View.GONE
        }

        //검색 히스토리 데이터 가져오기
        val tag_history_num = MainActivity.my_post_get_data.getInt(SEARCH_HISTORY_LIST_NUM, 0)
        if(tag_history_num !=0){
            for(i in 1 .. tag_history_num){
                search_history_list.add(MainActivity.my_post_get_data.getString(SEARCH_HISTORY_LIST+(i-1),""))
            }
        }

        adapter = Home_frag_adapter(context, search_post_data, search_post_rating_check,search_post_comment_check, SEARCH,search_post_max, arrayListOf())
        history_adapter = Search_hostory_adapter(context,search_history_list)


        //리사이클러뷰 어뎁터 및 초기화
        rating_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rating_list.adapter = adapter

        tag_history_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        tag_history_list.adapter = history_adapter


        //액션바 검색||키보드 검색 키 누를경우
        MainActivity.searchTag(object : MainActivity.interface_search{
            override fun interface_search(tag: String, searching: Boolean) {
                //검색 버튼 눌렀을때
                if(!searching) {
                    if(tag ==""){
                        search_result_layer.visibility = View.GONE
                        search_history_layer.visibility = View.VISIBLE
                    }
                    else {
                        search_result_layer.visibility = View.VISIBLE
                        search_history_layer.visibility = View.GONE
                        search_flag = 1
                        searchTag(tag)
                    }
                }
                else{
                }
            }
        })

        //리스트 삭제||리스트 클릭시
        Search_hostory_Viewholder.tagClear(object : Search_hostory_Viewholder.TagHistoryClearEvent{
            //태그 삭제 버튼
            override fun interface_Tag_Clear(tag_position: Int) {
                search_history_list.removeAt(tag_position)

                MainActivity.my_post_put_data.putInt(SEARCH_HISTORY_LIST_NUM, search_history_list.size)
                for (i in 0 until search_history_list.size)
                    MainActivity.my_post_put_data.putString(SEARCH_HISTORY_LIST + i, search_history_list[i])

                MainActivity.my_post_put_data.commit()
                history_adapter.notifyDataSetChanged()
            }
        })


        var visibleLast : DocumentSnapshot? = null

        //데이터 들고오기~~~~~~~~~~~~~~~~~~~~
        post_searching = OnCompleteListener { it ->
            total_post_num_old = search_post_data.size.toLong()
            if(post_add_bot)
                search_post_data.clear()
            search_post_rating_check.clear()

            it.result.mapTo(search_post_data) { (it.toObject(Post_Main_Data::class.java)) }


            search_post_max = search_post_data.size%5 !=0 || search_post_data.size.toLong() == total_post_num_old
            try {
                visibleLast = it.result.documents[it.result.size()-1]

            }catch (e : IndexOutOfBoundsException){
            }

            //보여질 포스트갯수만큼 체크 리스트 자리 만들어주기
            for(i in 0 until search_post_data.size) {
                search_post_rating_check.add(HOME_RATING_NON)
                search_post_comment_check.add(HOME_COMMENT_NON)
            }
            total_post_num = search_post_data.size.toLong()
            if(post_add_bot==false) {
                if (total_post_num == total_post_num_old) {
//                    Toast.makeText(context, getText(R.string.home_no_more_post), Toast.LENGTH_LONG).show()
                    view_post_num -= pic_basic_data.rating_list_add
//                    rating_list.scrollToPosition(search_post_data.size-1)
                }
            }

            //내가 투표했는지 비교하고 투표 체크 표시 데이터 넣기
            for(i in 0 until search_post_data.size){
                val position = my_post_get_data.getInt(""+search_post_data[i].save_time + user_info.id, HOME_RATING_NON)
                if(position != HOME_RATING_NON){
                    search_post_rating_check[i] = position
                }
                val comment_position = my_post_get_data.getBoolean("comment"+search_post_data[i].save_time + user_info.id, HOME_COMMENT_NON)
                if(comment_position != HOME_COMMENT_NON){
                    search_post_comment_check[i] = comment_position
                }
                //실행 끝나고 마지막에 할것들
                if(i == search_post_data.size-1){
                    mSwipeRefreshLayout.isRefreshing = false
                    post_refrash = true
                    post_add_bot= true
                    adapter.notifyDataSetChanged()
                    rating_list.setOnTouchListener(null)
                }
            }
            if(search_post_data.size==0){
                //검색 데이터 없을 경우
                mSwipeRefreshLayout.visibility = View.GONE
                empty_view.visibility = View.VISIBLE
            }
            else{
                mSwipeRefreshLayout.visibility = View.VISIBLE
                empty_view.visibility = View.GONE
            }
        }

//포스트 추가
        ViewHolder.postApppost_search(object : ViewHolder.interface_appPost{
            override fun interface_appPost() {
                if(post_add_bot&&!search_post_max) {
                    try {

                        post_add_bot = false
                        view_post_num += rating_list_add
                        firestore_.collection("post")
                                .whereGreaterThanOrEqualTo(tag,tag)
                                .whereLessThanOrEqualTo(tag,tag+"\uf8ff")
                                .startAfter(visibleLast).limit(rating_list_add.toLong())
                                .get()
                                .addOnCompleteListener(post_searching)
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
//                if (ScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag&&post_add_bot&&post_refrash) {
//                    try {
//                        post_add_bot = false
//                        view_post_num += rating_list_add
//                        firestore_.collection("post").whereGreaterThanOrEqualTo(tag,tag).whereLessThanOrEqualTo(tag,tag+"\uf8ff") .startAfter(visibleLast).limit(rating_list_add.toLong()).get().addOnCompleteListener(post_searching)
//
//                    }catch (e: UninitializedPropertyAccessException){}
//                }
//                else{}
//            }
//            override fun onScrolled(recyclerView: RecyclerView?, firstVisibleItem: Int, visibleItemCount: Int) {
//                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
//                lastItemVisibleFlag = (search_post_data.size > 0) && (firstVisibleItem + visibleItemCount >= search_post_data.size)
//            }
//        })

        mSwipeRefreshLayout.setOnRefreshListener {
            //리프레쉬할 코드(올려서 새로고침)
            rating_list.setOnTouchListener { p0, p1 -> true }
            post_refrash = false
            view_post_num = pic_pic_string_data.INIT_DATA_NUM
            //리프레쉬하는동안 스크롤 막기
            firestore_.collection("post")
                    .whereGreaterThanOrEqualTo(searching_tag,searching_tag)
                    .whereLessThanOrEqualTo(searching_tag,searching_tag+"\uf8ff")
                    .limit(pic_pic_string_data.INIT_DATA_NUM.toLong())
                    .get()
                    .addOnCompleteListener(post_searching)
        }

        //포스트 삭제
        ViewHolder.postDelete_search(object : ViewHolder.interface_delete_post{
            override fun interface_delete_post(position: Int) {
                search_post_data.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })
        //포스트선택시 평가페이지로 넘어가기
        ViewHolder.postClick_search(object : ViewHolder.interface_select_reting_post{
            override fun interface_delete_image(post_data: Post_Main_Data, rating_checked: Int) {
                if(!post_click_bool) {
                    post_click_bool = true
                    (activity as MainActivity).progress.visibility = View.VISIBLE
                    firestore_.collection(pic_pic_string_data.RATING_POST).document(""+(10000000000000-post_data.save_time)).get().addOnCompleteListener({
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
        val intent = Intent(context, Home_raing_activity::class.java)
        intent.putExtra(pic_pic_string_data.HOME_RATING_DATA,post_main_data)    //포스트 기본정보
        intent.putExtra(pic_pic_string_data.HOME_RATING_COMMENT_DATA, post_rater_data)  // 포스트 댓글, 하트 갯수
        intent.putExtra(pic_pic_string_data.HOME_RATING_RATING_BOOL, rating_checked)//평가했는지 체크
//        for (i in 0..post_rater_data.rater_list.size-1)
//            for (j in 0..post_rater_data.rater_list[i].size-1)
//                if(post_rater_data.rater_list[i][j] == user_info.id) {
//                    intent.putExtra(HOME_RATING_RATING_BOOL, true)
//                    intent.putExtra(HOME_RATING_RATING_POSITION, i)
//                    break
//                }
//        intent.putExtra(HOME_RATING_RATING_BOOL, true)  //TODO 테스트용
        (activity as MainActivity).progress.visibility = View.GONE
        startActivityForResult(intent, pic_pic_string_data.RATING_COMPLETE)
        post_click_bool = false
    }

    override fun onResume() {
        super.onResume()

        if(searching_tag !="")
            searchTag(searching_tag)
    }


    fun searchTag(tag : String){
        if(!tagClicked) {
            search_history_list.clear()

            //검색 히스토리 내부저장된 데이터 가져오기
            var tag_history_num = MainActivity.my_post_get_data.getInt(SEARCH_HISTORY_LIST_NUM, 0)
            if(tag_history_num !=0){
                for(i in 1 .. tag_history_num){
                    search_history_list.add(MainActivity.my_post_get_data.getString(SEARCH_HISTORY_LIST+(i-1),""))
                }
            }

            searching_tag = tag
            tag_history_num+=1

            //중복 기록 있으면 확인후 저장 ㄴㄴ
            val tag_duplication = (0 until search_history_list.size).any { search_history_list[it] == tag }

            if (!tag_duplication) {
                search_history_list.add(0, tag)
                if (search_history_list.size > 20) {
                    search_history_list.removeAt(20)
                    tag_history_num = 20
                }

                my_post_put_data.putInt(SEARCH_HISTORY_LIST_NUM, tag_history_num)
                for (i in 0 until search_history_list.size)
                    my_post_put_data.putString(SEARCH_HISTORY_LIST + i, search_history_list[i])

                my_post_put_data.commit()
                history_adapter.notifyDataSetChanged()
            } else { // 같은거 입력했을때 같은 리스트 최상위로 올려서 저장
                search_history_list.remove(tag)
                search_history_list.add(0, tag)
                my_post_put_data.putInt(SEARCH_HISTORY_LIST_NUM, tag_history_num - 1)
                for (i in 0 until search_history_list.size)
                    my_post_put_data.putString(SEARCH_HISTORY_LIST + i, search_history_list[i])

                my_post_put_data.commit()
                history_adapter.notifyDataSetChanged()
            }
        }

        firestore_.collection("post")
                .whereGreaterThanOrEqualTo(tag,tag)
                .whereLessThanOrEqualTo(tag,tag+"\uf8ff")
                .limit(pic_pic_string_data.INIT_DATA_NUM.toLong())
                .get()
                .addOnCompleteListener(post_searching)
    }
    override fun onPause() {
        mListener.interface_search_frag_destroy_save_data(search_post_data,search_post_rating_check,searching_tag,search_post_comment_check,search_flag)
        super.onPause()
    }
}// Required empty public constructor