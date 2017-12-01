package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_home_rating_scroll.*
import picturePick.picture.main_user.profile_rating.R
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


class Home_rating_scroll : AppCompatActivity(){

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var photo_url : ArrayList<String> = arrayListOf()
    private var now_position : Int = 0
    private var rate_bool : Boolean = false
    private var rate_result : ArrayList<Int> = arrayListOf()
    private var rate_position : Int = 0
    companion object {
        var heart_bools : ArrayList<Boolean> = arrayListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_rating_scroll)

        //status 바 없애기
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val intent : Intent = intent

        rate_bool = intent.getIntExtra(HOME_RATING_SCROLL_RATE_BOOL,HOME_RATING_NON) != HOME_RATING_NON
        now_position = intent.getIntExtra(HOME_RATING_SCROLL_POSITION,0)
        photo_url.addAll(intent.getStringArrayListExtra(HOME_RATING_SCROLL_PHOTOS))
        rate_result.addAll(intent.getIntegerArrayListExtra(HOME_RATING_SCROLL_RATE_RESULT))
        rate_position = intent.getIntExtra(HOME_RATING_SCROLL_RATE_BOOL,HOME_RATING_NON)
        val capture_bool = intent.getBooleanExtra(HOME_RATING_SCROLL_CAPTURE_BOOL,false)

        for(i in 0 until photo_url.size){
            heart_bools.add(intent.getBooleanExtra(HOME_RATING_SCROLL_HEART_BOOL+i,false))
        }

        //캡쳐 막기
        if(capture_bool)
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager,photo_url.size)
        container.adapter = mSectionsPagerAdapter

        container.currentItem = now_position  // 첫포지션 설정
        //TODO 화면돌아갈때 포지션값 가져오기
        container.setOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                now_position = position
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home_rating_scroll, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    inner class SectionsPagerAdapter(fm: FragmentManager,val page_num : Int) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment? {
            when(position){
                0->{
                    return PlaceholderFragment.newInstance(position,photo_url[position],heart_bools[position],rate_bool,rate_result[position],rate_position)
                }
                1->{
                    return PlaceholderFragment2.newInstance(position,photo_url[position],heart_bools[position],rate_bool,rate_result[position],rate_position)
                }
                2->{
                    return PlaceholderFragment3.newInstance(position,photo_url[position],heart_bools[position],rate_bool,rate_result[position],rate_position)
                }
                3->{
                    return PlaceholderFragment4.newInstance(position,photo_url[position],heart_bools[position],rate_bool,rate_result[position],rate_position)
                }
                4->{
                    return PlaceholderFragment5.newInstance(position,photo_url[position],heart_bools[position],rate_bool,rate_result[position],rate_position)
                }
                5->{
                    return PlaceholderFragment6.newInstance(position,photo_url[position],heart_bools[position],rate_bool,rate_result[position],rate_position)
                }
                else->{
                    return null
                }
            }
        }
        override fun getCount(): Int = page_num
    }

    override fun onDestroy() {
        photo_url.clear()
        heart_bools.clear()
        super.onDestroy()
    }

    override fun onBackPressed() {
        val intent = Intent()
        Log.d("!@#",""+ heart_bools)
        for (i in 0 until heart_bools.size) {
            intent.putExtra(HOME_RATING_SCROLL_HEART_RESULT + i, heart_bools[i])
            if(heart_bools[i]){
                intent.putExtra(HOME_RATING_SCROLL_HEART_RESULT_BOOL, true)
            }
        }
        setResult(1040,intent)
        finish()
    }
}
