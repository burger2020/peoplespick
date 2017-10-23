package com.example.main_user.profile_rating.bottom_menu_activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.ActionBar
import android.view.LayoutInflater
import android.widget.Toast

import com.example.main_user.profile_rating.R
import com.example.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_position
import com.example.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_uri
import com.example.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.now_pickPhoto_num
import kotlinx.android.synthetic.main.toolbar_edit.*
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.util.Log
import com.example.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.add_pickPhoto_num
import com.example.main_user.profile_rating.data.pic_pic_string_data
import kotlinx.android.synthetic.main.fragment_edit_frag_second.*


class Edit_Activity : AppCompatActivity() {

    companion object {
        var flow = 0;

        // 글 옵션은 나중에 sqlite로 저장해서 기본설정 할수있도록 해서 받아오는걸로 바꿔야함
        var edit_mosaic_bool = false
        var edit_capture_bool = false
        var edit_comment_bool = false
        var edit_invisible_bool = false
        var edit_comment = ""
    }

    var tabIndex = 1
    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState?.putInt("tabIndex", tabIndex)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_)

        if(savedInstanceState != null) {
            tabIndex = savedInstanceState.getInt("tabIndex");
        }

        val actionBar : ActionBar? = supportActionBar
        actionBar?.elevation = 0F
        actionBar?.setDisplayShowCustomEnabled(true);
        actionBar?.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar?.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar?.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionbar = inflater.inflate(R.layout.toolbar_edit, null)
        actionBar?.setCustomView(actionbar)

        var fragment: Fragment =  Edit_frag_first.newInstance()
        var fragmentManager: FragmentManager = supportFragmentManager
        var tramsaction = fragmentManager.beginTransaction()
        tramsaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        tramsaction?.replace(R.id.edit_container, fragment)?.commit()

        var click_photo_uri_init = arrayListOf<Uri>()
        var click_photo_position_init = arrayListOf<Int>()
        var now_pickphoto_num_init = 0
        var add_pickphoto_num_init = 0

        //close 메뉴 버튼 클릭
        edit_toolbar_close.setOnClickListener({
            if(flow==0){
                add_pickphoto_num_init = 0
                now_pickphoto_num_init = 0
                click_photo_position_init.clear()
                click_photo_uri_init.clear()
                click_photo_position.clear()
                click_photo_uri.clear()
                now_pickPhoto_num = 0
                finish()
            }else if(flow==1){
                fragment = Edit_frag_first.newInstance(click_photo_uri_init,click_photo_position_init,now_pickphoto_num_init, add_pickphoto_num_init) // add photo 추가해야함
                var fragmentManager: FragmentManager = supportFragmentManager
                var tramsaction = fragmentManager.beginTransaction()
                tramsaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                tramsaction?.replace(R.id.edit_container, fragment)?.commit()
                edit_toolbar_close.background = resources.getDrawable(R.mipmap.ic_edit_toolbar_close)
                edit_toolbar_next.text = getText(R.string.edit_tool_next)
                flow=0
            }
        })
        //다음 메뉴 버튼 클릭
        edit_toolbar_next.setOnClickListener({
            if(now_pickPhoto_num <2){ // 선택한 photo가 1개이하
                Toast.makeText(this,getString(R.string.edit_photo_next_false_comment),Toast.LENGTH_SHORT).show()
            } else if(now_pickPhoto_num >=2&&flow == 0){ // 2개 이상에 사진선택 frag
                click_photo_uri_init = click_photo_uri
                click_photo_position_init = click_photo_position
                now_pickphoto_num_init = now_pickPhoto_num
                add_pickphoto_num_init = 0 // 수정해야함
                fragment = Edit_frag_second.newInstance(click_photo_uri)
                var fragmentManager: FragmentManager = supportFragmentManager
                var tramsaction = fragmentManager.beginTransaction()
                tramsaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                tramsaction?.replace(R.id.edit_container, fragment)?.addToBackStack("first_frag")?.commit()
                flow = 1
                edit_toolbar_close.background = resources.getDrawable(R.mipmap.ic_edit_toolbar_backkey)
                edit_toolbar_next.text = getText(R.string.edit_tool_produce)
            } else if(flow ==1){ // frag 2 완료 작업
                val edit_comment :String = edit_photo_comment.text.toString()
                val intent = Intent()
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTOS,click_photo_uri)   //선택한 사진 uri들
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_NUM, now_pickPhoto_num)  // 선택한 사진 갯수
//                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTOS, now_pickPhoto_num+add_pickphoto_num)
                Log.d("edit_photo_comment.text1"," : "+ edit_comment)
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_COMMENT, edit_comment)  // 글 내용
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_PHOTO_REP, Edit_frag_second.select_photo_uri_rep)  // 대표사진uri?
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_MOSAIC, edit_mosaic_bool)  // 사진 옵션 모자이크
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_CAPTURE, edit_capture_bool)  //        캡쳐
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_COMMENT, edit_comment_bool)  //        댓글
                intent.putExtra(pic_pic_string_data.EDIT_COMPLETE_INVISIBLE, edit_invisible_bool)  //    친구에게 숨기기
                setResult(pic_pic_string_data.EDIT_COMPLETE,intent)
                click_photo_position.clear()
                click_photo_uri.clear()
                now_pickPhoto_num = 0
                add_pickPhoto_num=0
                flow = 0
                finish()
            }
        })
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.edit_backPress_title))
                .setMessage(getString(R.string.edit_backPress_comment))
                .setPositiveButton((getString(R.string.Yes)), { dialog, which ->
                    click_photo_position.clear()
                    click_photo_uri.clear()
                    now_pickPhoto_num = 0
                    add_pickPhoto_num=0
                    flow = 0
                    finish() })
                .setNegativeButton((getString(R.string.No)), null)
                .show()
    }
}
