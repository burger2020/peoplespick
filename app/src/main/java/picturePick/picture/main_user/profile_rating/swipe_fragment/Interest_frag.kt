package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import com.cunoraz.tagview.Tag
import com.cunoraz.tagview.TagView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_interest_frag.view.*
import picturePick.picture.main_user.profile_rating.Dialog.Inter_Add_Tag_Dialog
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_get_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_put_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.pic_basic_data.Companion.rating_list_add
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_COMMENT_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.HOME_RATING_NON
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.INIT_DATA_NUM
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.INTEREST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.TAG
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.TAG_SIZE
import picturePick.picture.main_user.profile_rating.data.post_rater_data

//프래그먼트 생성시 임폴트에
//import android.support.v4.app.Fragment
//이거로 바꿔줘야함!
class Interest_frag : Fragment() {
    companion object {
        lateinit var rating_list : RecyclerView
        var visible_interestFrag = true
        private var onResume_ = false
        private var interest_post_data = ArrayList<Post_Main_Data>()
        private var interest_post_data_temp = ArrayList<Post_Main_Data>()
        private var interest_post_rating_check: ArrayList<Int> = arrayListOf()
        private var interest_post_comment_check: ArrayList<Boolean> = arrayListOf()
        private var interest_post_Tag_cycle: ArrayList<Boolean> = arrayListOf()
        fun newInstance(Post_save_data : ArrayList<Post_Main_Data>,post_check_save : ArrayList<Int>,post_commnet_check : ArrayList<Boolean>): Interest_frag {
            val fragment = Interest_frag()
            interest_post_data = Post_save_data
            interest_post_rating_check = post_check_save
            interest_post_comment_check = post_commnet_check
            onResume_ = interest_post_data.size != 0
            return fragment
        }

        lateinit var mListener: interface_interest_frag_destroy_save_data
        fun interest_frag_destroyed(mListener: interface_interest_frag_destroy_save_data) { this.mListener = mListener }

        lateinit var mListener2: interfaceInterestTagClicked
        fun interfaceInterestTagClicked(mListener2: interfaceInterestTagClicked) { this.mListener2 = mListener2 }
    }

    interface interface_interest_frag_destroy_save_data {
        fun interface_interest_frag_destroy_save_data(post_Main_Data: ArrayList<Post_Main_Data>,post_check_save :ArrayList<Int>,interest_post_comment_check:ArrayList<Boolean>)
    }
    interface interfaceInterestTagClicked{
        fun interfaceInterestTagClicked(tag : String)
    }
    override fun onPause() {
        visible_interestFrag = false
        mListener.interface_interest_frag_destroy_save_data(interest_post_data, interest_post_rating_check, interest_post_comment_check)
        super.onPause()
    }

    lateinit var post_loading : OnCompleteListener<QuerySnapshot>
    private lateinit var list_adapter : Home_frag_adapter
    private var total_post_num_old : Long = 0
    private var total_post_num : Long = 0
    private var view_post_num : Int = INIT_DATA_NUM
    private var post_add_bot : Boolean = true // 포스트 리스트 추가 중복 안되도록
    private var post_refrash : Boolean = true // 포스트 리스트 추가 다했을때 바닥 닿을시 로드 가능하게
    private var post_click_bool : Boolean = false // 포스트 클릭 중복 방지
    private var post_max = false
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rootView = inflater!!.inflate(R.layout.activity_interest_frag, container, false)
        val interest_tag = arrayListOf<String>()
        var Tag_count = 0
        val empty_text = rootView.interest_empty_text
        var delete_bool = false
        val tag_group = rootView.tag_group as TagView
        var interest_layer = rootView.interest_layer
        //관심 태그 등록된 수 가져와서 등록된 태그 add
        try {
            (0 until my_post_get_data.getInt(user_info.id  + TAG_SIZE, 0)).mapTo(interest_tag) { my_post_get_data.getString(user_info.id  + TAG+ it, "#") }
        }catch (e:UninitializedPropertyAccessException){}

        if(interest_tag.size>0){
            //태그목록 있을시 엠티뷰 제거
            empty_text.visibility = View.GONE
            interest_layer.visibility = View.VISIBLE
        }

        //엠티뷰 뷰
        if(interest_tag.size==0){
            empty_text.visibility = View.VISIBLE
            interest_layer.visibility = View.GONE
        }else{
            empty_text.visibility = View.GONE
            interest_layer.visibility = View.VISIBLE
        }
        //태그 셋
        Tag_set(interest_tag,tag_group)
        //태그 클릭시
        tag_group.setOnTagClickListener({ tag: Tag, position: Int ->
            if(tag.text == getString(R.string.add_tag)){
                //태그 추가
                val dialog = Inter_Add_Tag_Dialog(context)
                dialog.show()
            }else{
                //누를시 삭제
                if(delete_bool) {
                    Toast.makeText(context, interest_tag[position], Toast.LENGTH_LONG).show()

                    interest_tag.removeAt(position)
                    for(i in 0 until interest_tag.size){
                        my_post_put_data.putString(user_info.id  + TAG+i, interest_tag[i])
                    }
                    my_post_put_data.putInt(user_info.id  + TAG_SIZE, interest_tag.size)
                    my_post_put_data.commit()
                    if(interest_tag.size==0){
                        //태그 모두 지워졌을시
                        delete_bool = !delete_bool
                        empty_text.visibility = View.VISIBLE
                        interest_layer.visibility = View.GONE
                        Tag_set(interest_tag,tag_group)
                    }else {
                        Tag_count =0
                        reflash_list(interest_tag)
                        Tag_deleteMode(interest_tag, tag_group)
                    }
                }
                else{
                    //태그로 검색
                    val tag_ = tag.text.replace("#","")
                    mListener2.interfaceInterestTagClicked(tag_)
                }
            }
        })

        //액션바 삭제 버튼
        (activity as MainActivity).Interest_delete_btn.setOnClickListener({
            delete_bool = !delete_bool
            if(delete_bool){
                Tag_deleteMode(interest_tag,tag_group)
            }else{
                Tag_set(interest_tag,tag_group)
            }
        })

        //다이얼로그에 추가 누를시 인터페이스
        Inter_Add_Tag_Dialog.AddInterestTag(object :Inter_Add_Tag_Dialog.interface_add_interestTag{
            override fun interface_add_interestTag(addTag: String) {
                var overlap_tagBool = false
                for(i in 0 until interest_tag.size){
                    if(interest_tag[i] == addTag) {
                        Toast.makeText(context, getString(R.string.inter_overlap), Toast.LENGTH_LONG).show()
                        overlap_tagBool = true
                        break
                    }
                }
                if(!overlap_tagBool) {
                    interest_tag.add(0, addTag)

                    for (i in 0 until interest_tag.size) {
                        my_post_put_data.putString(user_info.id + TAG + i, interest_tag[i])
                    }

                    my_post_put_data.putInt(user_info.id + TAG_SIZE, interest_tag.size)
                    my_post_put_data.commit()

                    empty_text.visibility = View.GONE
                    interest_layer.visibility = View.VISIBLE
                    Tag_count =0
                    reflash_list(interest_tag)
                    Tag_set(interest_tag, tag_group)
                }
            }
        })

        //리사이클러뷰@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        val mSwipeRefreshLayout = rootView.interest_post_swipe_layout  // 새로고침 하는 스와이프리프레쉬 레이아웃
        val post_empty_txt = rootView.interestPost_empty_text

        rating_list = rootView.interest_List

        list_adapter = Home_frag_adapter(context, interest_post_data,interest_post_rating_check,interest_post_comment_check, INTEREST,post_max,interest_post_Tag_cycle)
//리사이클러뷰 어뎁터 및 초기화
        rating_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rating_list.adapter = list_adapter

        //데이터 들고오기~~~~~~~~~~~~~~~~~~~~~
        post_loading = OnCompleteListener { it ->
            Tag_count++
            total_post_num_old = interest_post_data .size.toLong()
//            if(post_add_bot)
//                interest_post_data .clear()

            it.result.mapTo(interest_post_data_temp ) { (it.toObject(Post_Main_Data::class.java)) }


            //보여질 포스트갯수만큼 체크 리스트 자리 만들어주기
            for(i in 0 until interest_post_data_temp .size) {
                interest_post_rating_check.add(HOME_RATING_NON)
                interest_post_comment_check.add(HOME_COMMENT_NON)
                if(i==0)
                    interest_post_Tag_cycle.add(true)
                else
                    interest_post_Tag_cycle.add(false)
            }
            total_post_num = interest_post_data_temp .size.toLong()
            if(post_add_bot==false) {
                if (total_post_num == total_post_num_old) {
                    try {
                        Toast.makeText(context, getText(R.string.home_no_more_post), Toast.LENGTH_LONG).show()
                        view_post_num -= rating_list_add
                        rating_list.scrollToPosition(interest_post_data_temp .size-1)
                    }catch (e : IllegalStateException){}
                }
            }

            //내가 투표했는지 비교하고 투표 체크 표시 데이터 넣기
            for(i in 0 until interest_post_data_temp .size){
                val position = my_post_get_data.getInt(""+interest_post_data_temp [i].save_time + user_info.id, HOME_RATING_NON)
                if(position != HOME_RATING_NON){
                    interest_post_rating_check[i] = position
                }
                val commet_position = my_post_get_data.getBoolean("comment"+interest_post_data_temp [i].save_time + user_info.id, HOME_COMMENT_NON)
                if(commet_position != HOME_COMMENT_NON){
                    interest_post_comment_check[i] = commet_position
                }
                if(i == interest_post_data_temp .size-1&&Tag_count==interest_tag.size){
                    Tag_count=0
                    interest_post_data.clear()
                    interest_post_data.addAll(interest_post_data_temp)
                    interest_post_data_temp.clear()
                    mSwipeRefreshLayout.isRefreshing = false
                    post_refrash = true
                    post_add_bot= true
                    list_adapter .notifyDataSetChanged()
                    rating_list.setOnTouchListener(null)
                }
            }
            if(interest_post_data .size==0){
                //검색 데이터 없을 경우
                mSwipeRefreshLayout.visibility = View.GONE
                post_empty_txt.visibility = View.VISIBLE
            }
            else{
                mSwipeRefreshLayout.visibility = View.VISIBLE
                post_empty_txt.visibility = View.GONE
            }
        }

        //관심태그는 최대 할당량 정해서 관심 태그 최근 목록만
//        //바닥 닿을시 처리
//        var lastItemVisibleFlag = false
//        rating_list.setOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView?, ScrollState: Int) {
//                if (ScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag&&post_add_bot&&post_refrash) {
//                    post_add_bot = false
//                    view_post_num += rating_list_add
//                    firestore_.collection("post").whereEqualTo("poster_token",user_info.id).limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
//                }
//                else{}
//            }
//            override fun onScrolled(recyclerView: RecyclerView?, firstVisibleItem: Int, visibleItemCount: Int) {
//                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
//                lastItemVisibleFlag = (interest_post_data.size > 0) && (firstVisibleItem + visibleItemCount >= interest_post_data.size)
//            }
//        })

        mSwipeRefreshLayout.setOnRefreshListener {
            //리프레쉬할 코드(올려서 새로고침)
            rating_list.setOnTouchListener { p0, p1 -> true }
            post_refrash = false
            view_post_num = INIT_DATA_NUM
            //리프레쉬하는동안 스크롤 막기
            Tag_count =0
            reflash_list(interest_tag)
//            firestore_.collection("post").whereEqualTo("poster_token",user_info.id).limit(view_post_num.toLong()).get().addOnCompleteListener(post_loading)
        }

        //포스트 삭제
        ViewHolder.postDelete_myPost(object :ViewHolder.interface_delete_post{
            override fun interface_delete_post(position: Int) {
                interest_post_data.removeAt(position)
                list_adapter .notifyDataSetChanged()
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

        //첫리줌시 데이터 로드
        if(!onResume_) {
            if(interest_tag.size>0) {
                Tag_count =0
                reflash_list(interest_tag)
            }
        }
        return rootView
    }

    fun Tag_set(interest_tag : ArrayList<String>,tag_group : TagView){
        tag_group.removeAll()
        for(i in 0 until interest_tag.size ){
            val tag = Tag("#"+interest_tag[i])
            tag.radius = 20f
            tag.layoutColor = resources.getColor(R.color.colorMain_mini)
            tag.tagTextSize = 15f
            tag_group.addTag(tag)
        }
        if(interest_tag.size<5){
            val tag = Tag(getString(R.string.add_tag))
            tag.radius = 10f
            tag.layoutColor = resources.getColor(R.color.colorAccent)
            tag_group.addTag(tag)
        }
    }
    fun Tag_deleteMode(interest_tag : ArrayList<String>,tag_group : TagView){
        tag_group.removeAll()
        for(i in 0 until interest_tag.size ){
            val tag = Tag("#"+interest_tag[i]+"  x")
            tag.radius = 5f
            tag.layoutColor = resources.getColor(R.color.colorAccent)
            tag_group.addTag(tag)
        }
    }


    fun post_clicked(post_main_data : Post_Main_Data, post_rater_data : post_rater_data, rating_checked : Int){
        val intent = Intent(context,Home_raing_activity::class.java)
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
        startActivityForResult(intent, pic_pic_string_data.RATING_COMPLETE)
        post_click_bool = false
    }
    fun reflash_list(interest_tag: ArrayList<String>){
//        interest_post_data .clear()
        interest_post_rating_check.clear()
        interest_post_comment_check.clear()
        interest_post_Tag_cycle.clear()
        for(i in 0 until interest_tag.size) {
            Log.d("interest_tag", "" + interest_tag[i])
            firestore_.collection("post")
                    .whereEqualTo(interest_tag[i], interest_tag[i])
//                    .whereEqualTo(interest_tag[1],interest_tag[1])
                    .limit(INIT_DATA_NUM.toLong())
                    .get()
                    .addOnCompleteListener(post_loading)
        }
    }
    override fun onResume() {
        super.onResume()
        visible_interestFrag = true
    }
}