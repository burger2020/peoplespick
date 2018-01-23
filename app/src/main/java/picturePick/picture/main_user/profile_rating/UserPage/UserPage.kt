package picturePick.picture.main_user.profile_rating.UserPage

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.QuerySnapshot
import com.instacart.library.truetime.TrueTime
import kotlinx.android.synthetic.main.activity_user_page.*
import picturePick.picture.main_user.profile_rating.Chatting.ChattingPage
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_get_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_put_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.statisticsInfo
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.*
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.BLOCKLIST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.BLOCK_USER_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.BLOCK_USER_LIST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.FOLLOW
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.FOLLOWINGLIST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.FOLLOWNUM
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.NOTIFICATION
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILEOPEN_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_LIST_ADD
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.USERPOST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.USER_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.USER_NAME
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.USER_PROFILE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.USER_TOKEN
import picturePick.picture.main_user.profile_rating.swipe_fragment.HomeFragAdapter
import picturePick.picture.main_user.profile_rating.swipe_fragment.HomeRaingActivity
import picturePick.picture.main_user.profile_rating.swipe_fragment.ViewHolder


class UserPage : AppCompatActivity() {

    lateinit var postLoading : OnCompleteListener<QuerySnapshot>
    private lateinit var adapter : HomeFragAdapter
    private var total_post_num_old = 0L
    private var total_post_num : Long = 0
    private var my_post_data = ArrayList<Post_Main_Data>()
    private var my_post_rating_check: ArrayList<Int> = arrayListOf()
    private var my_post_comment_check: ArrayList<Boolean> = arrayListOf()
    private var postAddBot: Boolean = true // 포스트 리스트 추가 중복 안되도록
    private var view_post_num : Long = pic_pic_string_data.INIT_DATA_NUM
    private var post_click_bool : Boolean = false // 포스트 클릭 중복 방지
    private var postRefresh: Boolean = true // 포스트 리스트 추가 다했을때 바닥 닿을시 로드 가능하게
    private var userInfo = statisticsData()
    private lateinit var userName : String
    private lateinit var userProfile : String
    private lateinit var userToken : String
    private lateinit var progress : RelativeLayout
    private var followRightBool = false
    private var followData = arrayListOf<FollowData>()
    private var openOptionData = openOptionData()
    private var profileOpenBool = false
    private var blockBool = false
    companion object {
        var user_post_max = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)

        var buttonOverlap = false
        val ratingList = user_post_rating_list
        val mSwipeRefreshLayout = user_post_swipe_layout  // 새로고침 하는 스와이프리프레쉬 레이아웃
        val postEmptyTxt = userPost_empty_text

        val intent = intent

        user_post_max = true

        userToken = intent.getStringExtra(USER_TOKEN)
        userName = intent.getStringExtra(USER_NAME)
        userProfile = intent.getStringExtra(USER_PROFILE)

        adapter = HomeFragAdapter(this, my_post_data, my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo, userName, userProfile,userToken)

        ratingList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ratingList.adapter = adapter

        mSwipeRefreshLayout.visibility = View.GONE
        progressbar_User_info.visibility= View.VISIBLE

        //차단유저 확인
        blockBool = my_post_get_data.getBoolean(user_info.id+BLOCK_USER_LIST+userToken,false)

        //액션바 유저 이름
        actionbar_user_name.text = userName

        //나를 팔로우한 유저인지 확인
        firestore_.collection("USER_DATA")
                .document(userToken)
                .collection(FOLLOWINGLIST)
                .whereEqualTo("userIdToken", user_info.id)
                .get().addOnCompleteListener{

            it.result.mapTo(followData) { (it.toObject(FollowData::class.java)) }

        }
        //유저 게시물 공개 범위 확인
        firestore_.collection("USER_DATA")
                .document(userToken+ PROFILEOPEN_DATA)
                .get().addOnSuccessListener{
            try {
                openOptionData = it.toObject(openOptionData::class.java)
            }catch (e : IllegalStateException){}
        }

        //액션바 쪽지 버튼
        messageOption.setOnClickListener {

            if(!buttonOverlap) {
                buttonOverlap=true
                when {
                    messageOption.text == applicationContext.getString(R.string.message) -> {
                        //메세지 일경우
                        val intentChat = Intent(this, ChattingPage::class.java)

                        intentChat.putExtra(USER_NAME, userName)
                        intentChat.putExtra(USER_PROFILE, userProfile)
                        intentChat.putExtra(USER_TOKEN, userToken)

                        startActivity(intentChat)
                        buttonOverlap = false
                    }
                    messageOption.text == applicationContext.getString(R.string.block) -> {
                        //차단
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.userBlockComment))
                        builder.setMessage(userName+" " +getString(R.string.userBlockCommentSecondary))
                        builder.setPositiveButton(getString(R.string.userBlock)) { _, _ ->
                            progressbar_User_info.visibility= View.VISIBLE
                            firestore_.collection(BLOCK_USER_DATA)
                                    .document(user_info.id)
                                    .collection(BLOCK_USER_LIST)
                                    .document(userToken)
                                    .set(BlockUserData(userName,userProfile,userToken)).addOnSuccessListener {
                                messageOption.text = applicationContext.getString(R.string.unblock)
                                followOption.visibility = View.GONE
                                my_post_put_data.putBoolean(user_info.id + BLOCK_USER_LIST + userToken, true)
                                blockBool = true
                                my_post_put_data.commit()
                                buttonOverlap = false
                                progressbar_User_info.visibility= View.GONE
                                adapter = HomeFragAdapter(this, arrayListOf(), my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo, userName, userProfile,userToken)
                                ratingList.adapter = adapter
                            }
                        }
                        builder.setNegativeButton(getString(R.string.Cancel)) { _, _ ->

                        }
                        builder.show()

                    }
                    messageOption.text == applicationContext.getString(R.string.unblock) -> {
                        //차단 해제
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.userUnblockComment))
                        builder.setMessage(userName+" " +getString(R.string.userUnblockCommentSecondary))
                        builder.setPositiveButton(getString(R.string.userUnblock)) { _, _ ->
                            progressbar_User_info.visibility= View.VISIBLE
                            firestore_.collection(BLOCK_USER_DATA)
                                    .document(user_info.id)
                                    .collection(BLOCK_USER_LIST)
                                    .document(userToken)
                                    .delete().addOnSuccessListener {
                                messageOption.text = applicationContext.getString(R.string.block)
                                followOption.visibility = View.VISIBLE
                                followOption.text = applicationContext.getString(R.string.follow)
                                if(followRightBool)
                                    followOption.text = applicationContext.getString(R.string.followRight)
                                my_post_put_data.putBoolean(user_info.id + BLOCK_USER_LIST + userToken, false)
                                blockBool = false
                                my_post_put_data.commit()
                                buttonOverlap = false
                                mSwipeRefreshLayout.visibility = View.VISIBLE

                                when {
                                    openOptionData.allOpen -> {
                                        //전체공개
                                        profileOpenBool = true
                                    }
                                    openOptionData.followerOpen && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false) -> {
                                        //팔로워까지 공개
                                        profileOpenBool = true
                                    }
                                    openOptionData.followRightOpen && followData.size!=0 && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false)-> {
                                        //맞팔로워만 공개
                                        profileOpenBool = true
                                    }
                                    else ->{
                                        profileOpenBool = false
                                    }
                                }

                                adapter = if(!profileOpenBool)
                                    HomeFragAdapter(this, arrayListOf(), my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo, userName, userProfile,userToken)
                                else
                                    HomeFragAdapter(this, my_post_data, my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo,userName,userProfile,userToken)
                                ratingList.adapter = adapter
                                ratingList.setOnTouchListener { _, _ -> true }
                                postRefresh = false
                                //리프레쉬하는동안 스크롤 막기
                                if(!blockBool)
                                    MainActivity.firestore_.collection("post")
                                            .whereEqualTo("poster_token", userToken)
                                            .limit(pic_pic_string_data.INIT_DATA_NUM)
                                            .get()
                                            .addOnCompleteListener(postLoading)
                                else{
                                    messageOption.text = applicationContext.getString(R.string.unblock)
                                    followOption.visibility = View.GONE
                                    user_post_max = true
                                    adapter.notifyDataSetChanged()
                                    mSwipeRefreshLayout.visibility = View.VISIBLE
                                    progressbar_User_info.visibility= View.GONE
                                    ratingList.setOnTouchListener(null)
                                    mSwipeRefreshLayout.isRefreshing = false
                                    postRefresh = true
                                }
                            }
                        }
                        builder.setNegativeButton(getString(R.string.Cancel)) { _, _ ->

                        }
                        builder.show()
                    }
                }
            }
        }

        //액션바 팔로우 버튼
        followOption.setOnClickListener{
            //팔로우
            progressbar_User_info.visibility= View.VISIBLE
            if(!buttonOverlap){
                //중복 안되게
                buttonOverlap=true
                if(followOption.text == applicationContext.getString(R.string.follow) || followOption.text == applicationContext.getString(R.string.followRight)){
                    firestore_.collection("USER_DATA")
                            .document(userToken+ pic_pic_string_data.STATISTICS_DATA)
                            .get().addOnSuccessListener {
                        // 여기에 유저 팔로워 팔로잉 게시물갯수 문서 긁어와서 셋팅하도록
                        userInfo = it.toObject(statisticsData::class.java)

                        userInfo.followerNum++
                        statisticsInfo.followingNum++

                        //유저 기본 데이터 업데이트
                        firestore_.collection(USER_DATA)
                                .document(userToken+ pic_pic_string_data.STATISTICS_DATA)
                                .set(userInfo)
                        firestore_.collection(USER_DATA)
                                .document(user_info.id+ pic_pic_string_data.STATISTICS_DATA)
                                .set(statisticsInfo)

                        //유저 팔로워 데이터 업데이트
                        firestore_.collection(USER_DATA)
                                .document(userToken)
                                .collection(pic_pic_string_data.FOLLOWLIST)
                                .document(user_info.id)
                                .set(FollowData(user_info.dis_name,user_info.profile,user_info.id))

                        //내 팔로잉 데이터 업데이트
                        firestore_.collection(USER_DATA)
                                .document(user_info.id)
                                .collection(pic_pic_string_data.FOLLOWINGLIST)
                                .document(userToken)
                                .set(FollowData(userName,userProfile,userToken))

                        val trueTime =  try {
                            TrueTime.now().time
                        }catch (e:IllegalStateException){
                            System.currentTimeMillis()
                        }

                        val notifyData = NotificationData(user_info.profile, user_info.dis_name, "0", trueTime, user_info.id, false)

                        //유저토큰으로 노티피 데이터 저장
                        if(my_post_get_data.getLong(user_info.id+pic_pic_string_data.FOLLOW+userToken+"overlap",0L)+1000*60*60*10<trueTime) {
                            //유저차단목록에 포함되는지 확인
                            firestore_.collection(BLOCK_USER_DATA)
                                    .document(userToken)
                                    .collection(BLOCK_USER_LIST)
                                    .whereEqualTo(BLOCKLIST,user_info.id)
                                    .get().addOnCompleteListener {
                                val blockBool = arrayListOf<BlockUserData>()
                                it.result.mapTo(blockBool) { (it.toObject(BlockUserData::class.java)) }
                                if(blockBool.size==0){
                                    firestore_.collection(NOTIFICATION)
                                            .document(userToken)
                                            .collection(DATA)
                                            .document("" + (10000000000000 - trueTime))
                                            .set(notifyData)
                                    my_post_put_data.putLong(user_info.id+pic_pic_string_data.FOLLOW+userToken+"overlap",trueTime)
                                    my_post_put_data.commit()
                                }
                            }
                        }

                        //로컬에 저장
                        val followingNum = my_post_get_data.getInt(user_info.id+FOLLOWNUM,0) + 1
                        my_post_put_data.putInt(user_info.id+FOLLOWNUM,followingNum)
                        my_post_put_data.putBoolean(user_info.id+FOLLOW+userToken,true)
                        my_post_put_data.commit()


                        when {
                            openOptionData.allOpen -> {
                                //전체공개
                                profileOpenBool = true
                            }
                            openOptionData.followerOpen && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false) -> {
                                //팔로워까지 공개
                                profileOpenBool = true
                            }
                            openOptionData.followRightOpen && followData.size!=0 && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false)-> {
                                //맞팔로워만 공개
                                profileOpenBool = true
                            }
                            else ->{
                                profileOpenBool = false
                            }
                        }

                        adapter = if(!profileOpenBool)
                            HomeFragAdapter(this, arrayListOf(), my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo, userName, userProfile,userToken)
                        else
                            HomeFragAdapter(this, my_post_data, my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo,userName,userProfile,userToken)

                        ratingList.adapter = adapter
                        followOption.text = applicationContext.getString(R.string.followCancel)
                        messageOption.text = applicationContext.getString(R.string.message)
                        progressbar_User_info.visibility= View.GONE
                        buttonOverlap = false
                    }
                }else//팔로우 취소
                {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.userFollowComment))
                    builder.setMessage("$userName " +getString(R.string.userFollowCommentSecondary))
                    builder.setPositiveButton(getString(R.string.userFollow)) { _, _ ->
                        firestore_.collection(USER_DATA)
                                .document(userToken+ pic_pic_string_data.STATISTICS_DATA)
                                .get().addOnSuccessListener {
                            // 여기에 유저 팔로워 팔로잉 게시물갯수 문서 긁어와서 셋팅하도록
                            userInfo = it.toObject(statisticsData::class.java)

                            userInfo.followerNum--
                            statisticsInfo.followingNum--

                            //유저 기본 데이터 업데이트
                            firestore_.collection(USER_DATA)
                                    .document(userToken+ pic_pic_string_data.STATISTICS_DATA)
                                    .set(userInfo)
                            firestore_.collection(USER_DATA)
                                    .document(user_info.id+ pic_pic_string_data.STATISTICS_DATA)
                                    .set(statisticsInfo)

                            //유저 팔로워 데이터 업데이트
                            firestore_.collection(USER_DATA)
                                    .document(userToken)
                                    .collection(pic_pic_string_data.FOLLOWLIST)
                                    .document(user_info.id)
                                    .delete()

                            //내 팔로잉 데이터 업데이트
                            firestore_.collection(USER_DATA)
                                    .document(user_info.id)
                                    .collection(pic_pic_string_data.FOLLOWINGLIST)
                                    .document(userToken)
                                    .delete()

                            //로컬에 저장
                            val followingNum = my_post_get_data.getInt(user_info.id+FOLLOWNUM,1) - 1
                            my_post_put_data.putInt(user_info.id+FOLLOWNUM,followingNum)
                            my_post_put_data.putBoolean(user_info.id+FOLLOW+userToken,false)
                            my_post_put_data.commit()

                            when {
                                openOptionData.allOpen -> {
                                    //전체공개
                                    profileOpenBool = true
                                }
                                openOptionData.followerOpen && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false) -> {
                                    //팔로워까지 공개
                                    profileOpenBool = true
                                }
                                openOptionData.followRightOpen && followData.size!=0 && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false)-> {
                                    //맞팔로워만 공개
                                    profileOpenBool = true
                                }
                                else ->{
                                    profileOpenBool = false
                                }
                            }

                            adapter = if(!profileOpenBool)
                                HomeFragAdapter(this, arrayListOf(), my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo, userName, userProfile,userToken)
                            else
                                HomeFragAdapter(this, my_post_data, my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo,userName,userProfile,userToken)
                            ratingList.adapter = adapter
                            //맞팔or팔로우 옵션 텍스트 넣기
                            followOption.text = applicationContext.getString(R.string.follow)
                            if(followRightBool)
                                followOption.text = applicationContext.getString(R.string.followRight)
                            if(!blockBool){
                                //차단유저 아닐경우
                                messageOption.text = applicationContext.getString(R.string.block)
                            }else{
                                messageOption.text = applicationContext.getString(R.string.unblock)
                                followOption.visibility = View.GONE
                            }
                            progressbar_User_info.visibility= View.GONE
                            buttonOverlap = false
                        }

                    }
                    builder.setNegativeButton(getString(R.string.Cancel)) { _, _ ->

                    }
                    builder.show()
                }
            }
        }

        progress = progressbar_User_info


        postLoading = OnCompleteListener { it ->

            total_post_num_old = my_post_data.size.toLong()
            my_post_data.clear()
            my_post_rating_check.clear()
            my_post_comment_check.clear()

            try {
                it.result.mapTo(my_post_data) { (it.toObject(Post_Main_Data::class.java)) }
            }catch (e : kotlin.Exception){
                user_post_max = true
            }
            user_post_max = my_post_data.size%5 !=0 || my_post_data.size.toLong() == total_post_num_old

            //보여질 포스트갯수만큼 체크 리스트 자리 만들어주기
            for(i in 0 until my_post_data.size) {
                my_post_rating_check.add(pic_pic_string_data.RATING_NON)
                my_post_comment_check.add(pic_pic_string_data.COMMENT_NON)
            }
            total_post_num = my_post_data.size.toLong()
            if(postAddBot ==false) {
                if (total_post_num == total_post_num_old) {
                    try {
//                        Toast.makeText(context, getText(R.string.home_no_more_post), Toast.LENGTH_LONG).show()
                        view_post_num -= pic_pic_string_data.RATING_LIST_ADD
//                        rating_list.scrollToPosition(my_post_data.size-1)
                    }catch (e : IllegalStateException){}
                }
            }

            //내가 투표했는지 비교하고 투표 체크 표시 데이터 넣기
            for(i in 0 until my_post_data.size){
                val position = MainActivity.my_post_get_data.getInt(""+ my_post_data[i].save_time + MainActivity.user_info.id, pic_pic_string_data.RATING_NON)
                if(position != pic_pic_string_data.RATING_NON){
                    my_post_rating_check[i] = position
                }
                val commentPosition = MainActivity.my_post_get_data.getBoolean("comment"+ my_post_data[i].save_time+ MainActivity.user_info.id, pic_pic_string_data.COMMENT_NON)
                if(commentPosition != pic_pic_string_data.COMMENT_NON){
                    my_post_comment_check[i] = commentPosition
                }
                if(i == my_post_data.size-1){
                    postRefresh = true
                    postAddBot = true

                }
            }
            if(my_post_data.size==0){
                //검색 데이터 없을 경우
                MainActivity.firestore_.collection("USER_DATA")
                        .document(userToken+pic_pic_string_data.STATISTICS_DATA)
                        .get()
                        .addOnSuccessListener {
                            when {
                                userToken == user_info.id -> {
                                    //내 프로필 들어오면 액션바 옵션 안보이게
                                    followOption.visibility = View.GONE
                                    messageOption.visibility = View.GONE
                                }
                                my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false) -> {
                                    followOption.text = applicationContext.getString(R.string.followCancel)
                                    messageOption.text = applicationContext.getString(R.string.message)
                                }
                                else -> {
                                    if(followData.size==0){
                                        //유저가 나를 팔로워 안하고있는경우
                                        followOption.text = applicationContext.getString(R.string.follow)
                                        messageOption.text = applicationContext.getString(R.string.block)
                                        if(blockBool) {
                                            //차단유저
                                            messageOption.text = applicationContext.getString(R.string.unblock)
                                            followOption.visibility = View.GONE
                                        }
                                    }else{
                                        followRightBool = true
                                        followOption.text = applicationContext.getString(R.string.followRight)
                                        messageOption.text = applicationContext.getString(R.string.block)
                                        if(blockBool) {
                                            //차단유저
                                            messageOption.text = applicationContext.getString(R.string.unblock)
                                            followOption.visibility = View.GONE
                                        }
                                    }
                                }
                            }
                            // 여기에 유저 팔로워 팔로잉 게시물갯수 문서 긁어와서 셋팅하도록
                            userInfo = it.toObject(statisticsData::class.java)

                            postEmptyTxt.visibility = View.GONE
                            mSwipeRefreshLayout.visibility = View.VISIBLE
                            progressbar_User_info.visibility= View.GONE
                            ratingList.setOnTouchListener(null)
                            mSwipeRefreshLayout.isRefreshing = false
//                            adapter.notifyDataSetChanged()

                            when {
                                openOptionData.allOpen -> {
                                    //전체공개
                                    profileOpenBool = true
                                }
                                openOptionData.followerOpen && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false) -> {
                                    //팔로워까지 공개
                                    profileOpenBool = true
                                }
                                openOptionData.followRightOpen && followData.size!=0 && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false)-> {
                                    //맞팔로워만 공개
                                    profileOpenBool = true
                                }
                                else ->{
                                    profileOpenBool = false
                                }
                            }

                            adapter = if(!profileOpenBool){
                                HomeFragAdapter(this, arrayListOf(), my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo, userName, userProfile,userToken)
                            } else
                                HomeFragAdapter(this, my_post_data, my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo,userName,userProfile,userToken)
                            ratingList.adapter = adapter
                        }
            }
            else{
                //유저 정보 세팅(팔로워,잉 게시물갯수)
                MainActivity.firestore_.collection("USER_DATA")
                        .document(userToken+pic_pic_string_data.STATISTICS_DATA)
                        .get()
                        .addOnSuccessListener {
                            //내 프로필 들어오면 액션바 옵션 안보이게
                            when {
                                userToken == user_info.id -> {
                                    followOption.visibility = View.GONE
                                    messageOption.visibility = View.GONE
                                }
                                my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false) -> {
                                    followOption.text = applicationContext.getString(R.string.followCancel)
                                    messageOption.text = applicationContext.getString(R.string.message)
                                }
                                else -> {
                                    if(followData.size==0){
                                        followOption.text = applicationContext.getString(R.string.follow)
                                        messageOption.text = applicationContext.getString(R.string.block)
                                        if(blockBool) {
                                            //차단유저
                                            messageOption.text = applicationContext.getString(R.string.unblock)
                                            followOption.visibility = View.GONE
                                        }
                                    }else{
                                        followRightBool = true
                                        followOption.text = applicationContext.getString(R.string.followRight)
                                        messageOption.text = applicationContext.getString(R.string.block)
                                        if(blockBool) {
                                            //차단유저
                                            messageOption.text = applicationContext.getString(R.string.unblock)
                                            followOption.visibility = View.GONE
                                        }
                                    }
                                }
                            }
                            // 여기에 유저 팔로워 팔로잉 게시물갯수 문서 긁어와서 셋팅하도록
                            userInfo = it.toObject(statisticsData::class.java)

                            postEmptyTxt.visibility = View.GONE
                            mSwipeRefreshLayout.visibility = View.VISIBLE
                            progressbar_User_info.visibility= View.GONE
                            ratingList.setOnTouchListener(null)
                            mSwipeRefreshLayout.isRefreshing = false
                            postRefresh = true

                            when {
                                openOptionData.allOpen -> {
                                    //전체공개
                                    profileOpenBool = true
                                }
                                openOptionData.followerOpen && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false) -> {
                                    //팔로워까지 공개
                                    profileOpenBool = true
                                }
                                openOptionData.followRightOpen && followData.size!=0 && my_post_get_data.getBoolean(user_info.id+FOLLOW+userToken,false)-> {
                                    //맞팔로워만 공개
                                    profileOpenBool = true
                                }
                                else ->{
                                    profileOpenBool = false
                                }
                            }

                            adapter = if(!profileOpenBool)
                                HomeFragAdapter(this, arrayListOf(), my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo, userName, userProfile,userToken)
                            else
                                HomeFragAdapter(this, my_post_data, my_post_rating_check, my_post_comment_check, USERPOST, arrayListOf(),arrayListOf(), userInfo,userName,userProfile,userToken)
                            ratingList.adapter = adapter

                        }
            }
        }

        //차단유저 아닌경우 처음 포스트 가져오기
        if(!blockBool)
            MainActivity.firestore_.collection("post")
                    .whereEqualTo("poster_token", userToken)
                    .limit(pic_pic_string_data.INIT_DATA_NUM)
                    .get()
                    .addOnCompleteListener(postLoading)
        else{
            messageOption.text = applicationContext.getString(R.string.unblock)
            followOption.visibility = View.GONE
            mSwipeRefreshLayout.visibility = View.VISIBLE
            progressbar_User_info.visibility= View.GONE
            ratingList.setOnTouchListener(null)
            mSwipeRefreshLayout.isRefreshing = false
            postRefresh = true
            user_post_max = true
            adapter.notifyDataSetChanged()
        }

        //포스트 추가
        ViewHolder.postAddPostUserPost(object : ViewHolder.InterfaceAddPost {
            override fun interfaceAppPost() {
                if(postAddBot &&!user_post_max) {
                    try {
                        if(!blockBool) {
                            postAddBot = false
                            view_post_num += RATING_LIST_ADD
                            firestore_.collection("post")
                                    .whereEqualTo("poster_token", userToken)
                                    .limit(view_post_num).get()
                                    .addOnCompleteListener(postLoading)
                        }
                        else{
                            messageOption.text = applicationContext.getString(R.string.unblock)
                            followOption.visibility = View.GONE
                            user_post_max = true
                            adapter.notifyDataSetChanged()
                            mSwipeRefreshLayout.visibility = View.VISIBLE
                            progressbar_User_info.visibility= View.GONE
                            ratingList.setOnTouchListener(null)
                            mSwipeRefreshLayout.isRefreshing = false
                            postRefresh = true
                        }
                    } catch (e: UninitializedPropertyAccessException) {
                    }
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener {
            //리프레쉬할 코드(올려서 새로고침)
            ratingList.setOnTouchListener { _, _ -> true }
            postRefresh = false
            //리프레쉬하는동안 스크롤 막기
            if(!blockBool)
                MainActivity.firestore_.collection("post")
                        .whereEqualTo("poster_token", userToken)
                        .limit(pic_pic_string_data.INIT_DATA_NUM)
                        .get()
                        .addOnCompleteListener(postLoading)
            else{
                messageOption.text = applicationContext.getString(R.string.unblock)
                followOption.visibility = View.GONE
                user_post_max = true
                adapter.notifyDataSetChanged()
                mSwipeRefreshLayout.visibility = View.VISIBLE
                progressbar_User_info.visibility= View.GONE
                ratingList.setOnTouchListener(null)
                mSwipeRefreshLayout.isRefreshing = false
                postRefresh = true
            }
        }
        //포스트 삭제
        ViewHolder.postDeleteUserPost(object :ViewHolder.InterfaceDeletePost {
            override fun interfaceDeletePost(position: Int) {
                my_post_data.removeAt(position)
                //엠티뷰 비지블
                if(my_post_data.size==0)
                    postEmptyTxt.visibility = View.VISIBLE
                adapter.notifyDataSetChanged()
            }
        })
        //포스트선택시 평가페이지로 넘어가기
        ViewHolder.postClickUserpPost(object : ViewHolder.InterfaceSelectRetingPost {
            override fun interfaceDeleteImage(post_Main_Data: Post_Main_Data, rating_checked: Int) {
                if(!post_click_bool) {
                    post_click_bool = true

//
                    progress.visibility = View.VISIBLE

                    firestore_.collection(RATING_POST)
                            .document(post_Main_Data.poster_token)
                            .collection(RATING_DATA)
                            .document(""+(10000000000000-post_Main_Data.save_time))
                            .get().addOnCompleteListener({
                        val postRaterData = it.result.toObject(post_rater_data::class.java)

                        post_Main_Data.post_rater_num = postRaterData.rater_num

                        postClicked(post_Main_Data, postRaterData,rating_checked)
                    })
                }
            }
        })
    }

    fun postClicked(post_main_data : Post_Main_Data, post_rater_data : post_rater_data, rating_checked : Int){
        val intent = Intent(this, HomeRaingActivity::class.java)
        intent.putExtra(pic_pic_string_data.RATING_DATA,post_main_data)    //포스트 기본정보
        intent.putExtra(pic_pic_string_data.RATING_COMMENT_DATA, post_rater_data)  // 포스트 댓글, 하트 갯수
        intent.putExtra(pic_pic_string_data.RATING_RATING_BOOL, rating_checked)//평가했는지 체크
//        intent.putExtra(RATING_RATING_BOOL, true)  //TODO 테스트용
        startActivityForResult(intent, pic_pic_string_data.RATING_COMPLETE)
        progress.visibility = View.GONE
        post_click_bool = false
    }
    override fun onResume() {
        super.onResume()
        user_post_max = false
    }
}