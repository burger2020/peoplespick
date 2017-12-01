package picturePick.picture.main_user.profile_rating

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.AppBarLayout
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.instacart.library.truetime.TrueTimeRx
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.soundcloud.android.crop.Crop
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_result_frag.*
import picturePick.picture.main_user.profile_rating.Auth.Auth_MainActivity
import picturePick.picture.main_user.profile_rating.BroadCast.AppRemove
import picturePick.picture.main_user.profile_rating.Option.Option_Activity
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Alarm_frag
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_Activity
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Search_frag
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Search_hostory_Viewholder
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.User_Info
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.AUTH_RESULT
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.CAMERA_CODE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.CAMERA_PERMISION
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.CUSTOM_PROFILE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.DATA_CHECK
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.EDIT_COMPLETE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILE_CHANGE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILE_STATE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_COMPLETE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.SharedPreferenceS
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.ratingPage_tagClick
import picturePick.picture.main_user.profile_rating.swipe_fragment.*
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(),View.OnClickListener {

    var edit_complate = false // edit 완료하고 돌아와서 홈 프래그먼트 commit 시 데이터값 넣어줄떄
    val REQ_PERMISSION = 1001
    val REQ_SIGHOUT = 10001
    val REQ_AUTH = 10002
    val swipe_Nonselect_Icon: ArrayList<Int> = ArrayList(listOf(R.mipmap.ic_swipe_home_nonselct, R.mipmap.ic_swipe_interast_nonselect
            , R.mipmap.ic_swipe_popular_nonselect, R.mipmap.ic_swipe_result_nonselect))
    val swipe_Select_Icon: List<Int> = listOf(R.mipmap.ic_swipe_home_select, R.mipmap.ic_swipe_interast_select
            , R.mipmap.ic_swipe_popular_select, R.mipmap.ic_swipe_result_select)


    // TODO fragment Destroy view 할때 인터페이스로 받아와 보고있던 데이터 저장 할 것들
    var home_Post_save_data : ArrayList<Post_Main_Data> = arrayListOf()
    var home_post_check_save : ArrayList<Int> = arrayListOf()
    var home_post_comment_check_save : ArrayList<Boolean> = arrayListOf()

    var my_Post_save_data : ArrayList<Post_Main_Data> = arrayListOf()
    var my_post_check_save : ArrayList<Int> = arrayListOf()
    var my_post_comment_check_save : ArrayList<Boolean> = arrayListOf()

    var search_post_save_data : ArrayList<Post_Main_Data> = arrayListOf()
    var search_post_check_save : ArrayList<Int> = arrayListOf()
    var search_post_comment_save : ArrayList<Boolean> = arrayListOf()
    var search_tag : String = ""
    var search_flag = 0

    var interest_post_save_data : ArrayList<Post_Main_Data> = arrayListOf()
    var interest_post_check_save : ArrayList<Int> = arrayListOf()
    var interest_post_comment_check_save : ArrayList<Boolean> = arrayListOf()


    var first_bool = false
    //검색창 액션바
    lateinit var search_actionbar_layout_ : LinearLayout
    lateinit var search_actionbar_layout_search_ : LinearLayout
    lateinit var search_tagName_ : TextView
    lateinit var Interest_delete_btn : ImageView

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private lateinit var bottomNavigation : BottomNavigationViewEx
    private var mViewPager: ViewPager? = null
    private var before_bottom_button = 0

    lateinit var tabLayout : TabLayout

    private var post_click_bool : Boolean = false // 포스트 클릭 중복 방지

    //TODO 파이어베이스 데이터베이스 참조값 생성
    //TODO 파이어베이스 저장소 참조값 생성
//        val storage : FirebaseStorage = FirebaseStorage.getInstance()

    lateinit var progress : RelativeLayout
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.actionbar_setting_image->{
                startActivityForResult(Intent(this,Option_Activity::class.java),REQ_SIGHOUT)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        //시작시 기본데이터 가져오기
//        if(!auth_bool) {
//            startActivityForResult(Intent(this, Auth_MainActivity::class.java), REQ_AUTH)
        if(!first_bool) {
            my_post_get_data = getSharedPreferences(SharedPreferenceS, MODE_PRIVATE)
            my_post_put_data = MainActivity.my_post_get_data.edit()


            //유저데이터 담기
            mAuth = FirebaseAuth.getInstance()
            user = mAuth.currentUser!!
            user_info = User_Info(user.email!!, user.displayName!!, user.phoneNumber.toString(), user.photoUrl.toString(), user.uid, user.providers!![0])


            storageRef.child("/PROFILE/" + user?.uid).metadata.addOnSuccessListener {
                my_profile_change_meta = it.getCustomMetadata(PROFILE_CHANGE)
                my_profile_state = it.getCustomMetadata(PROFILE_STATE)
            }

//               TODO 서버에서 기본적인 유저 데이터 가져오기(기기내부에 저장)
            if (!my_post_get_data.getBoolean(user_info.id + DATA_CHECK, false)) {
                //투표한 포스트 데이터 내부에 저장
                firestore_.collection("USER_DATA").document(user_info.id).get().addOnCompleteListener({
                    try {
                        for (doc in it.result.data) {
                            if ((doc.value is Boolean)) {
                                my_post_put_data.putBoolean(doc.key, (doc.value as Boolean))
                                my_post_put_data.putBoolean(user_info.id + "RATE_NON_FIRST", true)
                                my_post_put_data.commit()
                            } else {
                                my_post_put_data.putInt(doc.key, (doc.value as Long).toInt())
                                my_post_put_data.putBoolean(user_info.id + "RATE_NON_FIRST", true)
                                my_post_put_data.commit()
                            }
                        }
                    } catch (e: IllegalStateException) {
                        my_post_put_data.putBoolean(user_info.id + "RATE_NON_FIRST", false)
                        MainActivity.my_post_put_data.commit()
                        Log.d("TAG!!", "투표 게시물 없음!")
                    }
                })
                my_post_put_data.putBoolean(user_info.id + DATA_CHECK, true)
                MainActivity.my_post_put_data.commit()
            } else { // 데이터 남아있는경우
                Log.d("TAG!!", "투표 게시물 없음")
//                my_post_put_data.putBoolean(user_info.id+DATA_CHECK,false)
            }
            auth_bool = true
            first_bool=true
        }
    }

    companion object {
        lateinit var user : FirebaseUser
        lateinit var mAuth : FirebaseAuth
        lateinit var appBar : AppBarLayout
        val firestore_: FirebaseFirestore = FirebaseFirestore.getInstance()
        //        val storageRef : StorageReference = FirebaseStorage.getInstance("gs://picpic-c7dc0").reference // 도쿄서버
        val storage_instance =  FirebaseStorage.getInstance()
        val storageRef : StorageReference = storage_instance.reference//블래이즈 요금제
        var auth_bool = false
        var user_info = User_Info()

        //다른사람 프로필사진 업데이트 메타 데이터
        var profile_change_meta = System.currentTimeMillis().toString()
        var my_profile_change_meta = ""
        var my_profile_state = ""
        lateinit var my_post_get_data: SharedPreferences
        lateinit var my_post_put_data: SharedPreferences.Editor

        lateinit var mListener: interface_photo_list_edit_complate
        fun Create_Posts(mListener:interface_photo_list_edit_complate) { this.mListener = mListener }

        lateinit var mListener1: interface_search
        fun searchTag(mListener1:interface_search) { this.mListener1 = mListener1}

        lateinit var mListener2: profileSet
        fun ProfileSet(mListener2:profileSet) { this.mListener2 = mListener2}

    }
    //    인터페이스

//    글 만들기 완료후 액티비티로 복귀시 타임라인에 데이터전달하여 새로고침할때 사용
    interface interface_photo_list_edit_complate {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_photo_list_edit_complate(post_Main_Data: Post_Main_Data)
    }
    interface interface_search {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_search(tag : String,searching : Boolean)
    }
    interface profileSet {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun profileSet()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = tabs as TabLayout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.elevation = 0F
        }

        Network_state()

        setSupportActionBar(toolbar_main)
        actionbar_setting_image.setOnClickListener(this)

        progress = progressbar_Main

        Interest_delete_btn = InterestTagDelete

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        search_actionbar_layout_ = search_actionbar_layout
        search_actionbar_layout_search_ = search_actionbar_layout_search
        search_tagName_ = search_tagName
        //검색창 엔터키 돋보기로 변경 및 클릭 리스너
        actionbar_search_text.imeOptions = EditorInfo.IME_ACTION_SEARCH
        actionbar_search_text.setOnClickListener({
            mListener1.interface_search(actionbar_search_text.text.toString(),true)
            search_flag=0
        })
        actionbar_search_text.setOnEditorActionListener { view, action, event ->
            when(action){
                EditorInfo.IME_ACTION_SEARCH->{
                    //검색 tag 인터페이스 보내기
                    if(actionbar_search_text.text.toString() != "") {
                        search_flag = 1
                        imm.hideSoftInputFromWindow(actionbar_search_text.windowToken, 0)
                        mListener1.interface_search(actionbar_search_text.text.toString(), false)
                        search_actionbar_layout_search_.visibility = View.VISIBLE
                        search_actionbar_layout_.visibility = View.GONE
                        search_tagName_.text = "#" + actionbar_search_text.text.toString()
                    }
                    true
                }
            }
            false
        }
        actionbar_search_image.setOnClickListener({
            if(actionbar_search_text.text.toString() != "") {
                search_flag = 1
                imm.hideSoftInputFromWindow(actionbar_search_text.windowToken, 0)
                mListener1.interface_search(actionbar_search_text.text.toString(), false)
                search_actionbar_layout_search_.visibility = View.VISIBLE
                search_actionbar_layout_.visibility = View.GONE
                search_tagName_.text = "#" + actionbar_search_text.text.toString()
            }
        })
        //홈화면에서 태그 클릭시
        ViewHolder.tagClicked(object : ViewHolder.interface_tag_clicked{
            override fun interface_tag_clicked(tag: String) {
                TagClicked(tag)
            }
        })
        //평가페이지에서 태그 클릭시
        Home_raing_activity.tagClicked_(object :Home_raing_activity.interface_tag_clicked{
            override fun interface_tag_clicked(tag: String) {
                TagClicked(tag)
            }
        })
        //관심 사진 탭에서 관심 태그 클릭
        Interest_frag.interfaceInterestTagClicked(object : Interest_frag.interfaceInterestTagClicked{
            override fun interfaceInterestTagClicked(tag: String) {
                TagClicked(tag)
            }
        })
        //검색 기록에서 tag 클릭시
        Search_hostory_Viewholder.tagClick(object : Search_hostory_Viewholder.TagHistoryClickEvent {
            //태그 이름
            override fun interface_Tag_Clicked(tag: String) {
                search_flag =1
                mListener1.interface_search(tag,false)
                search_actionbar_layout_search_.visibility = View.VISIBLE
                search_actionbar_layout_.visibility = View.GONE
                search_tagName_.text = "#"+tag
            }
        })
        //search 프래그먼트에서 백키 눌렀을때
        search_backBtn.setOnClickListener({
            onBackPressed()
        })

        actionbar_shop_option.setOnClickListener({

        })


        //액션바 양쪽 공백 없애기
//        val parent = actionbar.parent as Toolbar
//        parent.setContentInsetsAbsolute(0, 0)
        bottomNavigation = bottom_Navigation as BottomNavigationViewEx

        bottomNavigation.run {
            enableShiftingMode(false)
            enableItemShiftingMode(false)
            enableAnimation(false)
            setTextVisibility(false)
            setIconSize(30f,30f)
        }

        val title_text : TextView = actionbar_title_text as TextView

        //        서버에 사용자 정보 저장하고 불러올때 설정들

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        thread{ // truetime 초기화
            TrueTimeRx.build()
                    .initializeRx("time.google.com")
                    .subscribeOn(Schedulers.io())
                    .subscribe({ date -> Log.v("TAG!", "TrueTime was initialized and we have a time: " + date) }) { throwable -> throwable.printStackTrace() }
        }
        mViewPager = home_menu_container
        mViewPager!!.adapter = mSectionsPagerAdapter
        tabLayout.setupWithViewPager(mViewPager)

        //탭 타이틀 아이콘으로 바꾸는 부분
        tabLayout.getTabAt(0)?.setIcon(R.mipmap.ic_swipe_home_select)
        tabLayout.getTabAt(1)?.setIcon(R.mipmap.ic_swipe_interast_nonselect)
        tabLayout.getTabAt(2)?.setIcon(R.mipmap.ic_swipe_popular_nonselect)
        tabLayout.getTabAt(3)?.setIcon(R.mipmap.ic_swipe_result_nonselect)

        appBar = appbar

        // 페이지 바뀔때 리스너
        page_Chage(mViewPager!!, tabLayout,title_text)
        tabLayout.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if(tab?.position!! == 0&&Home_frag.visible_homeFrag){
                    Home_frag.rating_list.scrollToPosition(0)
                }else if (tab?.position!! == 3 &&My_Result_frag.visible_myFrag){
                    My_Result_frag.rating_list.scrollToPosition(0)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {}
        })

        //바텀 내비게이션 클릭리스너
        bottom_Navigation_Click(bottomNavigation)

        before_bottom_button = R.id.bottom_menu_home

//        fab 사용시
//        val fab = findViewById(R.id.fab) as FloatingActionButton
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        //TODO 홈 프래그먼트 뷰파괴시 데이터 저장하는 인터페이스
        Home_frag.main_frag_destroyed(object : Home_frag.interface_destroy_save_data{
            override fun interface_destroy_save_data(post_Main_Data: ArrayList<Post_Main_Data>, post_check_save: ArrayList<Int>, post_commnet_check: ArrayList<Boolean>) {
                home_post_comment_check_save = post_commnet_check
                home_Post_save_data = post_Main_Data
                home_post_check_save = post_check_save
            }
        })
        My_Result_frag.my_frag_destroyed(object : My_Result_frag.interface_my_frag_destroy_save_data{
            override fun interface_my_frag_destroy_save_data(post_Main_Data: ArrayList<Post_Main_Data>, post_check_save: ArrayList<Int>, my_post_comment_check: ArrayList<Boolean>) {
                my_Post_save_data = post_Main_Data
                my_post_check_save = post_check_save
                my_post_comment_check_save = my_post_comment_check
            }
        })
        Search_frag.search_frag_destroyed(object : Search_frag.interface_search_frag_destroy_save_data{
            override fun interface_search_frag_destroy_save_data(post_Main_Data: ArrayList<Post_Main_Data>, post_check_save: ArrayList<Int>, searching_tag: String,
                                                                 search_comment_check: ArrayList<Boolean>, search_flag: Int) {
                search_post_save_data = post_Main_Data
                search_post_check_save = post_check_save
                search_post_comment_save = search_comment_check
                search_tag = searching_tag
            }
        })
        Interest_frag.interest_frag_destroyed(object : Interest_frag.interface_interest_frag_destroy_save_data{
            override fun interface_interest_frag_destroy_save_data(post_Main_Data: ArrayList<Post_Main_Data>, post_check_save: ArrayList<Int>, interest_post_comment_check: ArrayList<Boolean>) {
                interest_post_save_data = post_Main_Data
                interest_post_check_save = post_check_save
                interest_post_comment_check_save = interest_post_comment_check
            }
        })
    }


    //페이지 세팅
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun instantiateItem(container: View?, position: Int): Any {
            val inflater = container?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


            val page = inflater.inflate(R.layout.activity_home_frag, null)

            page.setOnClickListener({
            })


            (container as ViewPager).addView(page, 0)
            return page
        }

        override fun getItem(position: Int): Fragment? =//Fragment를 생성하여 return 한다
                when(position){
                    0->{
                        Home_frag.newInstance(home_Post_save_data,home_post_check_save,home_post_comment_check_save)
                    }
                    1->{
                        Interest_frag.newInstance(interest_post_save_data,interest_post_check_save,interest_post_comment_check_save)
                    }
                    2->{
                        Popular_frag.newInstance()
                    }
                    3->{
                        My_Result_frag.newInstance(my_Post_save_data,my_post_check_save)
                    }
                    else-> null
                }

        override fun getCount(): Int =// 노출시킬 페이지 갯수
                4

        override fun getPageTitle(position: Int): CharSequence? {
            //탭의 text
            when (position) {
//                0 -> return "SECTION 1"
//                1 -> return "SECTION 2"
//                2 -> return "SECTION 3"
            }
            return null
        }
    }

    // 페이지 바뀔때 리스너 (뷰페이저)
    fun page_Chage(viewpager:ViewPager,tabLayout: TabLayout,toolbar_text:TextView){
        viewpager!!.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            @SuppressLint("ResourceAsColor")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{
                        toolbar_view ()
                        set_Swipe_Icon(tabLayout,position)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            toolbar_text.setTextColor(Color.WHITE)
                        else
                            toolbar_text.setTextColor(Color.WHITE)
                        toolbar_text.textSize = 25F
//                        toolbar_text.setTypeface(null, Typeface.BOLD)
                        toolbar_text.setText(R.string.section_home)
                        Interest_delete_btn.visibility = View.GONE
                    }
                    1->{
                        toolbar_view ()
                        set_Swipe_Icon(tabLayout,position)
                        toolbar_text.setText(R.string.section_interest)
                        toolbar_text.setTextColor(Color.WHITE)
                        toolbar_text.textSize = 20F
                        toolbar_text.setTypeface(null,Typeface.NORMAL)
                        Interest_delete_btn.visibility = View.VISIBLE
                    }
                    2->{
                        toolbar_view ()
                        toolbar_text.textSize = 20F
                        set_Swipe_Icon(tabLayout,position)
                        toolbar_text.setTextColor(Color.WHITE)
                        toolbar_text.setText(R.string.section_popular)
                        toolbar_text.setTypeface(null,Typeface.NORMAL)
                        Interest_delete_btn.visibility = View.GONE
                    }
                    3->{
                        toolbar_view ()
                        toolbar_text.textSize = 20F
                        set_Swipe_Icon(tabLayout,position)
                        toolbar_text.setTextColor(Color.WHITE)
                        toolbar_text.setText(R.string.section_result)
                        toolbar_text.setTypeface(null,Typeface.NORMAL)
                        Interest_delete_btn.visibility = View.GONE
                    }
                }
            }
        })
    }

    //툴바 올라가있는거 내리기
    fun toolbar_view (){
        val handler = Handler()
        handler.postDelayed({
            //지연후 동작
            appBar.setExpanded(true,true)
        }, //지연시간
                500)
    }

    var fragment : Fragment? = null
    //바탐 네비 아이콘 클릭 리스너
    fun bottom_Navigation_Click(bn: BottomNavigationView) {
        bn.setOnNavigationItemSelectedListener { item ->
            var check_frag  = false
            if(item.itemId != R.id.bottom_menu_home) {
                when (item.itemId) {
                    R.id.bottom_menu_search -> {
                        //서치 액션바 비지블
                        appBar.visibility = View.GONE
                        actionBar_Visible(1)
                        tabLayout.visibility = View.GONE
                        home_menu_container.visibility = View.GONE
                        bottom_menu_container.visibility = View.VISIBLE
                        before_bottom_button = item.itemId
                        if(search_flag!=2) {
                            fragment = Search_frag.newInstance(search_post_save_data, search_post_check_save, search_flag, search_post_comment_save)
                            check_frag = true
                        }
                    }
                    R.id.bottom_menu_new -> {
                        //권한 체크
                        check_frag =checkPermission()
                        appBar.visibility = View.VISIBLE
                    }
                    R.id.bottom_menu_alarm -> {
                        appBar.visibility = View.VISIBLE
                        before_bottom_button = item.itemId
                        tabLayout.visibility = View.GONE
                        home_menu_container.visibility = View.GONE
                        bottom_menu_container.visibility = View.VISIBLE
                        fragment = Alarm_frag.newInstance()
                        check_frag = true
                    }
                    else -> {
                    }
                }
                // 프래그먼트 전환해도 되는 상황인지 체크후 전환 ??
                if(check_frag) {
                    var fragmentManager: FragmentManager = supportFragmentManager
                    var tramsaction = fragmentManager.beginTransaction()
                    tramsaction?.replace(R.id.bottom_menu_container, fragment)?.commit()
                }
            }else{ // 바탐메뉴 홈버튼 눌렀을때
                actionBar_Visible(0)
                if(search_flag==2) {
                    search_flag = 0
                }
                appBar.visibility = View.VISIBLE
                before_bottom_button = item.itemId
                tabLayout.visibility = View.VISIBLE
                home_menu_container.visibility = View.VISIBLE
                bottom_menu_container.visibility = View.GONE
            }
            true
        }
    }
    fun actionBar_Visible(num : Int){
        //액션바 페이지에맞게 비지블 변경
        when(num){
            0->{
                Home_actionbar_layout.visibility = View.VISIBLE
                search_actionbar.visibility = View.GONE
            }
            1->{
                search_actionbar.visibility = View.VISIBLE
                Home_actionbar_layout.visibility = View.GONE
            }
        }
    }
    fun checkPermission() : Boolean{

        val check = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (check != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQ_PERMISSION)
                false
            } else {
                startActivityForResult(Intent(this, Edit_Activity::class.java),pic_pic_string_data.EDIT_COMPLETE)
                false
            }
        }else{
            startActivityForResult(Intent(this, Edit_Activity::class.java), pic_pic_string_data.EDIT_COMPLETE)
            false
        }
    }
    fun set_Swipe_Icon(tabLayout: TabLayout,index:Int){
        //아이콘 세팅
        for (i in 0..3) {
            tabLayout.getTabAt(i)?.setIcon(swipe_Nonselect_Icon[i])
        }
        tabLayout.getTabAt(index)?.setIcon(swipe_Select_Icon[index])
    }

    fun showSetting(){
        startActivityForResult(Intent(this, Edit_Activity::class.java),pic_pic_string_data.EDIT_COMPLETE)
    }

    //태그 클릭시 검색창으로 자동이동
    fun TagClicked(tag :String){
        appBar.visibility = View.GONE
        tabLayout.visibility = View.GONE
        search_flag = 2
        bottomNavigation.selectedItemId = R.id.bottom_menu_search
        actionBar_Visible(1)
        home_menu_container.visibility = View.GONE
        bottom_menu_container.visibility = View.VISIBLE
        fragment = Search_frag.searchInstance(search_post_save_data, search_post_check_save,search_post_comment_save, search_flag,tag,true)
        before_bottom_button = R.id.bottom_menu_search
        val fragmentManager: FragmentManager = supportFragmentManager
        val tramsaction = fragmentManager.beginTransaction()
        tramsaction?.replace(R.id.bottom_menu_container, fragment)?.commit()
        search_actionbar_layout_search_.visibility = View.VISIBLE
        search_actionbar_layout_.visibility = View.GONE
        search_tagName_.text = "#"+tag
//                mListener1.interface_search(tag,false)
    }

    override fun onResume() {
        super.onResume()
        //resume시 전에있던 프래그먼트 화면으로 이동
        bottomNavigation.selectedItemId = before_bottom_button
    }

    fun beginCrop(source : Uri){
        val destination = Uri.fromFile(File(cacheDir,"cropped"))
        Crop.of(source,destination).asSquare().start(this)
    }
    fun getRealPathFromURI(contextUri : Uri) : String{
        var column_index = 0
        val proj :Array<out String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contextUri,proj,null, null,null)
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        return cursor.getString(column_index)
    }

    fun handleCrop(resultCode: Int,result : Intent){
        when(resultCode){
            Activity.RESULT_OK->{
                //이미지 가져와서 프사 셋팅
                //기본 프로필 일경우 그냥 그대로 유지하고 아닐경우만 다시 넣어주기


                //w정방향 원본이미지 가져와서
//                val exif = ExifInterface(Crop.getOutput(result).toString())
//                val orientation : Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
//                val bitmap : Bitmap = rotate(
//                        MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, Uri.fromFile(File(Crop.getOutput(result).toString()))) as Bitmap,orientation)

                val bounds = BitmapFactory.Options()
                bounds.inJustDecodeBounds = true
                BitmapFactory.decodeFile(Crop.getOutput(result).toString(), bounds)
                my_profile_images.setImageURI(Crop.getOutput(result))
                val bitmaps = (my_profile_images.drawable as BitmapDrawable).bitmap
                my_profile_images.setImageResource(R.mipmap.ic_non_profile)
                var resize = (bounds.outWidth/500) + (bounds.outHeight/500)
                if(resize>6)
                    resize = 6
                else if(resize == 0 )
                    resize = 2
                val baos = ByteArrayOutputStream()

                bitmaps.compress(Bitmap.CompressFormat.JPEG,100/resize,baos)

                my_profile_change_meta = System.currentTimeMillis().toString()
                profile_change_meta = (System.currentTimeMillis()+1).toString()
                my_profile_state = CUSTOM_PROFILE
                val metadata : StorageMetadata = StorageMetadata.Builder()
                        .setCustomMetadata(PROFILE_STATE, CUSTOM_PROFILE)
                        .setCustomMetadata(PROFILE_CHANGE, my_profile_change_meta )
                        .build()

                //회전 정방향으로
                val data = baos.toByteArray()
//
//                storageRef.child("/PROFILE/"+user?.uid).delete().addOnSuccessListener {
                storageRef.child("/PROFILE/"+user?.uid).putBytes(data,metadata).addOnSuccessListener {
                    mListener2.profileSet()
                }
//                }
            }
            Crop.RESULT_ERROR->{
                //에러러
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode){
            ratingPage_tagClick->{
                TagClicked(data!!.getStringExtra("TAG"))
            }
        //로그아웃
            1818->{
                finish()
                startActivity(Intent(this,Auth_MainActivity::class.java))
            }
            AUTH_RESULT->{

            }
        }
        when(requestCode){
            Crop.REQUEST_PICK->{
                if(resultCode == Activity.RESULT_OK)
                    beginCrop(data?.data!!)
            }
            Crop.REQUEST_CROP->{
                //사진 크롭 후
                try {
                    handleCrop(resultCode, data!!)
                }catch (e:RuntimeException){}
            }
            CAMERA_CODE->{
                val destination = Uri.fromFile(File(cacheDir,"cropped"))
                Crop.of(data?.data,destination).asSquare().start(this)
            }
            EDIT_COMPLETE->{
                //포스트 생성 시
                if(resultCode==pic_pic_string_data.EDIT_COMPLETE) {
                    // 바탐 네비에서 홈버튼 클릭
                    bottomNavigation.selectedItemId = R.id.bottom_menu_home
                    //에디트 다됨 알림
                    edit_complate = true
                    mViewPager?.currentItem = 0
                }
            }
            REQ_SIGHOUT->{
                //로그아웃 해서 메뉴액티비티닫히면
                startActivity(Intent(this,Auth_MainActivity::class.java))
            }
            REQ_AUTH-> {
            }

            RATING_COMPLETE->{
                Log.d("RATING_COMPLETE","activity")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //권한 받기 result
        var notGranted = kotlin.arrayOfNulls<String>(permissions.size)
        when(requestCode){
            REQ_PERMISSION->{
                var index = 0
                for(i in 0 until permissions.size){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        val rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])
                        if(!rationale){
                            val dialogBuild= AlertDialog.Builder(this).setTitle("권한 설정").setMessage("이미지 선택하기위해 읽기 권한이 필요합니다.")
                                    .setCancelable(true).setPositiveButton("설정하러가기"){
                                dialog,whichButton -> showSetting()
                            }
                            dialogBuild.create().show()
                        }else{
                            notGranted[index++] = permissions[i]
                        }
                        if(notGranted.isNotEmpty()){
                            ActivityCompat.requestPermissions(this, notGranted,REQ_PERMISSION)
                        }
                    }
                }
            }
            CAMERA_PERMISION->{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_CODE)
            }
        }
    }

    override fun onBackPressed() {
//        if(rating_page_container.visibility == View.VISIBLE){
//            Log.d("ratingPage_visible","ratingPage_visible")
//            rating_page_container.visibility = View.GONE
//        }
//        else
        when(bottomNavigation.selectedItemId){
            R.id.bottom_menu_home->{
                super.onBackPressed()
            }
            R.id.bottom_menu_search->{
                when(search_flag){
                    0->{
                        bottomNavigation.selectedItemId = R.id.bottom_menu_home
                    }
                    1->{
                        search_flag=0
                        mListener1.interface_search("",false)
                        search_actionbar_layout_search.visibility = View.GONE
                        search_actionbar_layout.visibility = View.VISIBLE
                    }
                    2->{
                        //태그 클릭시 서치화면 이동
                        search_flag=0
                        mListener1.interface_search("",false)
                        search_actionbar_layout_search.visibility = View.GONE
                        search_actionbar_layout.visibility = View.VISIBLE
                        bottomNavigation.selectedItemId = R.id.bottom_menu_home
                    }
                }
            }
        }
    }

    //jpeg 변경시 회전 정방향으로
    private fun rotate(bitmap: Bitmap, orientation : Int): Bitmap {
        val matrix = Matrix()
        when(orientation){
            ExifInterface.ORIENTATION_NORMAL->{
                return bitmap
            }
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL->{
                matrix.setScale(-1F,1F)
            }
            ExifInterface.ORIENTATION_ROTATE_180->{
                matrix.setRotate(180F)
            }
            ExifInterface.ORIENTATION_FLIP_VERTICAL->{
                matrix.setRotate(180F)
                matrix.postScale(-1f,1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE->{
                matrix.setRotate(90F)
                matrix.postScale(-1F,1F)
            }
            ExifInterface.ORIENTATION_ROTATE_90->{
                matrix.setRotate(90F)
            }
            ExifInterface.ORIENTATION_TRANSVERSE->{
                matrix.setRotate(90F)
                matrix.postScale(-1F,1F)
            }
            ExifInterface.ORIENTATION_ROTATE_270->{
                matrix.setRotate(-90f)
            }
            else->{
                return bitmap
            }
        }
        return try{
            val btmRotated = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
            bitmap.recycle()
            btmRotated
        }catch (e : OutOfMemoryError){
            bitmap
        }
    }

    fun Network_state() {
        //인터넷 연결 확인
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        if (mobile.isConnected || wifi.isConnected) {
            // WIFI, 3G 어느곳에도 연결되지 않았을때
        } else {
            Toast.makeText(this,getString(R.string.network_connect_failed),Toast.LENGTH_LONG).show()
        }
    }
}
