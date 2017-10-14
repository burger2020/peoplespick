package com.example.main_user.profile_rating

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.main_user.profile_rating.swipe_fragment.Home_frag
import com.example.main_user.profile_rating.swipe_fragment.Interest_frag
import com.example.main_user.profile_rating.swipe_fragment.My_Result_frag
import com.example.main_user.profile_rating.swipe_fragment.Popular_frag
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val resor : Int = R.mipmap.ic_swipe_home_nonselct
    val swipe_Nonselect_Icon: ArrayList<Int> = ArrayList(listOf(R.mipmap.ic_swipe_home_nonselct, R.mipmap.ic_swipe_interast_nonselect
            , R.mipmap.ic_swipe_popular_nonselect, R.mipmap.ic_swipe_result_nonselect))
    val swipe_Select_Icon: List<Int> = listOf(R.mipmap.ic_swipe_home_select, R.mipmap.ic_swipe_interast_select
            , R.mipmap.ic_swipe_popular_select, R.mipmap.ic_swipe_result_select)

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private var mViewPager: ViewPager? = null
    companion object {
    }
    init {
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = toolbar as Toolbar
        val toolbar_text = toolbar_textView as TextView
        val bottomNavigation = bottom_Navigation as BottomNavigationView
        val tabLayout = tabs as TabLayout


        toolbar.setTitle("")
        setSupportActionBar(toolbar)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter

        tabLayout.setupWithViewPager(mViewPager)

        //탭 타이틀 아이콘으로 바꾸는 부분
        tabLayout.getTabAt(0)?.setIcon(R.mipmap.ic_swipe_home_select)
        tabLayout.getTabAt(1)?.setIcon(R.mipmap.ic_swipe_interast_nonselect)
        tabLayout.getTabAt(2)?.setIcon(R.mipmap.ic_swipe_popular_nonselect)
        tabLayout.getTabAt(3)?.setIcon(R.mipmap.ic_swipe_result_nonselect)
        // 페이지 바뀔때 리스너
        page_Chage(mViewPager!!, tabLayout,toolbar_text)


        // bottomnavigation 클릭 리스너
//        bottomNavigation.setOnNavigationItemSelectedListener {
//            when(it.itemId){
//            }
//        }

//        fab 사용시
//        val fab = findViewById(R.id.fab) as FloatingActionButton
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
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
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            //Fragment를 생성하여 return 한다
            when(position){
                0->{ return Home_frag.newInstance()
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
    fun set_Swipe_Icon(tabLayout: TabLayout,index:Int){
        for(i in 0..3) {
            Log.d("indexasdsadsadasda ","i: "+i)
            tabLayout.getTabAt(i)?.setIcon(swipe_Nonselect_Icon.get(i))
        }
        tabLayout.getTabAt(index)?.setIcon( swipe_Select_Icon.get(index))
    }
}
