package com.example.main_user.profile_rating

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.BoringLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.main_user.profile_rating.bottom_menu_activity.Alarm_frag
import com.example.main_user.profile_rating.bottom_menu_activity.Edit_Activity
import com.example.main_user.profile_rating.bottom_menu_activity.Search_frag
import com.example.main_user.profile_rating.data.option_info_data
import com.example.main_user.profile_rating.data.pic_data
import com.example.main_user.profile_rating.data.pic_pic_string_data
import com.example.main_user.profile_rating.data.pic_pic_string_data.Companion.EDIT_COMPLETE_PHOTOS
import com.example.main_user.profile_rating.data.pic_pic_string_data.Companion.EDIT_COMPLETE_PHOTO_COMMENT
import com.example.main_user.profile_rating.data.pic_pic_string_data.Companion.EDIT_COMPLETE_PHOTO_NUM
import com.example.main_user.profile_rating.data.pic_pic_string_data.Companion.EDIT_COMPLETE_PHOTO_REP
import com.example.main_user.profile_rating.swipe_fragment.Home_frag
import com.example.main_user.profile_rating.swipe_fragment.Interest_frag
import com.example.main_user.profile_rating.swipe_fragment.My_Result_frag
import com.example.main_user.profile_rating.swipe_fragment.Popular_frag
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_main.*


class MainActivity : AppCompatActivity() {

    var edit_complate = false // edit 완료하고 돌아와서 홈 프래그먼트 commit 시 데이터값 넣어줄떄
    val REQ_PERMISSION = 1001
    val swipe_Nonselect_Icon: ArrayList<Int> = ArrayList(listOf(R.mipmap.ic_swipe_home_nonselct, R.mipmap.ic_swipe_interast_nonselect
            , R.mipmap.ic_swipe_popular_nonselect, R.mipmap.ic_swipe_result_nonselect))
    val swipe_Select_Icon: List<Int> = listOf(R.mipmap.ic_swipe_home_select, R.mipmap.ic_swipe_interast_select
            , R.mipmap.ic_swipe_popular_select, R.mipmap.ic_swipe_result_select)

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    lateinit var bottomNavigation : BottomNavigationView
    private var mViewPager: ViewPager? = null
    private var before_bottom_button = 0

    private val Photo_Database : DatabaseReference = FirebaseDatabase.getInstance().getReference()

    companion object {
    }
    init {
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val tabLayout = tabs as TabLayout
        tabLayout.elevation = 0F

//        toolbar_main.setTitle("")
//        setSupportActionBar(toolbar_main)

        val actionBar : ActionBar? = supportActionBar
        actionBar?.elevation = 0F
        actionBar?.setDisplayShowCustomEnabled(true);
        actionBar?.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar?.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar?.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionbar = inflater.inflate(R.layout.toolbar_main, null)
        actionBar?.setCustomView(actionbar)
        //액션바 양쪽 공백 없애기
        val parent = actionbar.getParent() as Toolbar
        parent.setContentInsetsAbsolute(0, 0)
        bottomNavigation = bottom_Navigation as BottomNavigationView
        val title_text : TextView = actionbar_title_text as TextView


        //        서버에 사용자 정보 저장하고 불러올때 설정들

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        mViewPager = container
        mViewPager!!.adapter = mSectionsPagerAdapter

        tabLayout.setupWithViewPager(mViewPager)

        //탭 타이틀 아이콘으로 바꾸는 부분
        tabLayout.getTabAt(0)?.setIcon(R.mipmap.ic_swipe_home_select)
        tabLayout.getTabAt(1)?.setIcon(R.mipmap.ic_swipe_interast_nonselect)
        tabLayout.getTabAt(2)?.setIcon(R.mipmap.ic_swipe_popular_nonselect)
        tabLayout.getTabAt(3)?.setIcon(R.mipmap.ic_swipe_result_nonselect)
        // 페이지 바뀔때 리스너
        page_Chage(mViewPager!!, tabLayout,title_text)
        //바텀 내비게이션 클릭리스너
        bottom_Navigation_Click(bottomNavigation)

        before_bottom_button = R.id.bottom_menu_home

//        fab 사용시
//        val fab = findViewById(R.id.fab) as FloatingActionButton
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
    }

    //프래그먼트 예
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)
            return rootView
        }

        companion object {
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    //페이지 세팅
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            //Fragment를 생성하여 return 한다
            when(position){
                0->{
                    return Home_frag.newInstance()
                }
                1->{ return Interest_frag.newInstance()
                }
                2->{ return Popular_frag.newInstance()
                }
                3->{ return My_Result_frag.newInstance()
                }
                else-> return null
            }
        }

        override fun getCount(): Int {
            // 노출시킬 페이지 갯수
            return 4
        }

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

    // 페이지 바뀔때 리스너
    fun page_Chage(viewpager:ViewPager,tabLayout: TabLayout,toolbar_text:TextView){
        viewpager!!.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{
                        set_Swipe_Icon(tabLayout,position)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            toolbar_text.setTextColor(getColor(R.color.colorMain))
                        else
                            toolbar_text.setTextColor(R.color.colorMain)
                        toolbar_text.setTypeface(null, Typeface.BOLD)
                        toolbar_text.setText(R.string.section_home)
                    }
                    1->{
                        set_Swipe_Icon(tabLayout,position)
                        toolbar_text.setText(R.string.section_interest)
                        toolbar_text.setTextColor(Color.BLACK)
                        toolbar_text.setTypeface(null,Typeface.NORMAL)
                    }
                    2->{
                        set_Swipe_Icon(tabLayout,position)
                        toolbar_text.setTextColor(Color.BLACK)
                        toolbar_text.setText(R.string.section_popular)
                        toolbar_text.setTypeface(null,Typeface.NORMAL)
                    }
                    3->{
                        set_Swipe_Icon(tabLayout,position)
                        toolbar_text.setTextColor(Color.BLACK)
                        toolbar_text.setText(R.string.section_result)
                        toolbar_text.setTypeface(null,Typeface.NORMAL)
                    }
                }
            }
        })
    }

    var fragment : Fragment? = null
    fun bottom_Navigation_Click(bn: BottomNavigationView) {
        bn.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var check_frag  = false
                if(item.itemId != R.id.bottom_menu_home) {
                    when (item.itemId) {
                        R.id.bottom_menu_search -> {
                            home_menu_container.visibility = View.GONE
                            bottom_menu_container.visibility = View.VISIBLE
                            fragment = Search_frag.newInstance()
                            before_bottom_button = item.itemId
                            check_frag = true
                        }
                        R.id.bottom_menu_new -> {
                            //권한 체크
                             check_frag =checkPermission()
                          }
                        R.id.bottom_menu_alarm -> {
                            before_bottom_button = item.itemId
                            home_menu_container.visibility = View.GONE
                            bottom_menu_container.visibility = View.VISIBLE
                            fragment = Alarm_frag.newInstance()
                            check_frag = true
                        }
                        else -> {
                        }
                    }
                    // 프래그먼트 전환해도 되는 상황인지 체크후 전환
                    if(check_frag) {
                        var fragmentManager: FragmentManager = supportFragmentManager
                        var tramsaction = fragmentManager.beginTransaction()
                        tramsaction?.replace(R.id.bottom_menu_container, fragment)?.commit()
                    }
                }else{ // 바탐메뉴 홈버튼 눌렀을때
                    before_bottom_button = item.itemId
                    home_menu_container.visibility = View.VISIBLE
                    bottom_menu_container.visibility = View.GONE
                }
                return true
            }
        })
    }
    fun checkPermission() : Boolean{

        val check = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (check != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQ_PERMISSION)

                return false
            } else {
                startActivityForResult(Intent(this, Edit_Activity::class.java),pic_pic_string_data.EDIT_COMPLETE)
                return false
            }
        }else{
            startActivity(Intent(this, Edit_Activity::class.java))
            return false
        }

    }
    fun set_Swipe_Icon(tabLayout: TabLayout,index:Int){
    //아이콘 세팅
        for (i in 0..3) {
            tabLayout.getTabAt(i)?.setIcon(swipe_Nonselect_Icon.get(i))
        }
        tabLayout.getTabAt(index)?.setIcon(swipe_Select_Icon.get(index))
    }

    fun showSetting(){
        //권한 확인 끝나고 정상 실행
//        fragment = Alarm_frag.newInstance()
//        var fragmentManager: FragmentManager = supportFragmentManager
//        var tramsaction = fragmentManager.beginTransaction()
//        tramsaction?.replace(R.id.bottom_menu_container, fragment)?.commit()

        startActivity(Intent(this,Edit_Activity::class.java))
    }

    override fun onResume() {
        super.onResume()
        //resume시 전에있던 프래그먼트 화면으로 이동
        bottomNavigation.selectedItemId = before_bottom_button
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            pic_pic_string_data.EDIT_COMPLETE->{
                // 바탐 네비에서 홈버튼 클릭
                bottomNavigation.selectedItemId = R.id.bottom_menu_home
                //옵션 데이터 객체로 받아오기
                val option_info=option_info_data(data?.getBooleanExtra(pic_pic_string_data.EDIT_COMPLETE_MOSAIC,false)!!,
                        data.getBooleanExtra(pic_pic_string_data.EDIT_COMPLETE_CAPTURE,false),
                        data.getBooleanExtra(pic_pic_string_data.EDIT_COMPLETE_COMMENT,false),
                        data.getBooleanExtra(pic_pic_string_data.EDIT_COMPLETE_INVISIBLE,false))
                //데이터베이스에 게시글 데이터 저장
                Log.d("edit_photo_comment.text2",""+option_info.mosaic_bool)
                Log.d("edit_photo_comment.text3",""+data.getStringExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_COMMENT)+"\n"+
                        data.getParcelableExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_REP)+
                        (""+data.getIntExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_NUM,0)))
                firebase_database_save_info(data.getParcelableArrayListExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTOS),
                        listOf(data.getParcelableExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_REP)),
                        data.getIntExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_NUM,0),
                        option_info,
//                        data.get(pic_pic_string_data.EDIT_COMPLETE_PHOTO_COMMENT))
                        "asd")
                edit_complate = true
            }
        }
    }

//    data class option_info_data(val mosaic_bool:Boolean, val captuer_bool:Boolean,val comment_bool:Boolean ,val invisible_bool:Boolean)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //권한 받기 result
        var notGranted = kotlin.arrayOfNulls<String>(permissions.size)
        when(requestCode){
            REQ_PERMISSION->{
                var index = 0
                for(i in 0..permissions.size-1){
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
        }
    }
    fun firebase_database_save_info(select_photos:List<Uri>, rep_photo:List<Uri>, photo_num:Int, option_data : option_info_data, comment:String){
        val data_times = System.currentTimeMillis()
        Log.d("edit_photo_comment.text3",""+data_times)
//        Photo_Database.child(pic_pic_string_data.DATABASE_PHOTO_ARRAY).child(""+data_times).setValue(select_photos)
//        Photo_Database.child(pic_pic_string_data.DATABASE_PHOTO_REP).child(data_times.toString()).setValue(rep_photo)
        Photo_Database.child(pic_pic_string_data.DATABASE_PHOTO_NUM).child(data_times.toString()).setValue(photo_num)
        Photo_Database.child(pic_pic_string_data.DATABASE_PHOTO_OPTION).child(data_times.toString()).setValue(option_data)
        Photo_Database.child(pic_pic_string_data.DATABASE_PHOTO_COMMENT).child(data_times.toString()).setValue(comment)
    }
}
