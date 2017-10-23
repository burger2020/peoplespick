package com.example.main_user.profile_rating.bottom_menu_activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ToggleButton
import com.bumptech.glide.Glide
import com.example.main_user.profile_rating.Dialog.Edit_Photo_SetRep_Dialog
import com.example.main_user.profile_rating.Dialog.ViewHolder
import com.example.main_user.profile_rating.R
import kotlinx.android.synthetic.main.fragment_edit_frag_second.*
import kotlinx.android.synthetic.main.fragment_edit_frag_second.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Edit_frag_second.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Edit_frag_second.newInstance] factory method to
 * create an instance of this fragment.
 */
class Edit_frag_second : Fragment(){
    companion object {
        var select_photo_uri = arrayListOf<Uri>()
        lateinit var select_photo_uri_rep : Uri
        fun newInstance(click_photo_uri : ArrayList<Uri>): Edit_frag_second {
            val fragment = Edit_frag_second()
            select_photo_uri = click_photo_uri
            select_photo_uri_rep = select_photo_uri[0]
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_edit_frag_second, container, false)
        val option_rep_photo = rootView.edit_option_rep_photo
        val dialog = Edit_Photo_SetRep_Dialog(context, select_photo_uri)
        val mosaic_togle =  rootView.edit_option_mosaic
        val capture_togle  = rootView.edit_option_capture
        val comment_togle = rootView.edit_option_comment
        val invisible_togle = rootView.edit_option_invisible
        option_rep_photo.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(context).load(select_photo_uri_rep.toString()).into(option_rep_photo)

        option_rep_photo.setOnClickListener({
            //대표사진 누르면 선택 사진목록 다이얼로그 띄우기
            dialog.show()
        })
        ViewHolder.ImageClicked(object : ViewHolder.interface_pick_rep_photo{
            //대표 사진 선택했을경우 다이얼로그 닫고 사진 변경(사진 클릭시 다이얼로그에서 인터페이스)
            override fun interface_pick_rep_photo(photo_url: Uri, photo_position: Int) {
                select_photo_uri_rep = photo_url
                Glide.with(context).load(select_photo_uri_rep.toString()).into(option_rep_photo)
                dialog.cancel()
            }
        })

        //토글버튼 체크확인및 변수 대입
        mosaic_togle.setOnClickListener({
            Edit_Activity.edit_mosaic_bool= mosaic_togle.isChecked
        })
        capture_togle.setOnClickListener({
            Edit_Activity.edit_capture_bool = capture_togle.isChecked
        })
        comment_togle.setOnClickListener({
            Edit_Activity.edit_comment_bool = comment_togle.isChecked
        })
        invisible_togle.setOnClickListener({
            Edit_Activity.edit_invisible_bool = invisible_togle.isChecked
        })


        return rootView
    }
}// Required empty public constructor