package com.example.main_user.profile_rating.bottom_menu_activity

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.main_user.profile_rating.R
import com.example.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_position
import com.example.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_uri
import kotlinx.android.synthetic.main.list_edit_select_image.view.*

/**
 * Created by main_user on 2017-10-16.
 */

class Select_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val select_image = itemView.edit_select_image
    val select_image_delete = itemView.edit_select_image_delete

    companion object {
        lateinit var mListener: interface_delete_image
        fun ImageClicked(mListener:interface_delete_image) { this.mListener = mListener }
    }

    interface interface_delete_image {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_delete_image(photo_url: Uri,photo_position:Int)
    }
    fun bindHolder(context:Context, photo_url:ArrayList<Uri>,position: Int) {

        select_image.layoutParams = FrameLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 3, context.resources.displayMetrics.heightPixels / 4) as ViewGroup.LayoutParams?
        select_image.scaleType = ImageView.ScaleType.CENTER_CROP

        // 선택 이미지 출력
        Glide.with(context).load(photo_url[position].toString()).into(select_image)
        select_image_delete.setOnClickListener({ // X버튼 누르면 사진 선택 해제
            mListener.interface_delete_image(photo_url[position], click_photo_position[position]) //인터페이스 넘기기
            click_photo_uri.removeAt(position)
            click_photo_position.removeAt(position)
        })
    }
}
class Edit_adapter_select(val context:Context, val click_photo_urls: ArrayList<Uri>) : RecyclerView.Adapter<Select_ViewHolder>() {
    //    var result_photo: ArrayList<Uri> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Select_ViewHolder {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.list_edit_select_image,parent,false)
        return Select_ViewHolder(mainView)
    }

    override fun onBindViewHolder(holder: Select_ViewHolder?, position: Int) {
//        result_photo.addAll(click_photo_urls)
//        result_photo.add(Uri.parse("0"))
        holder?.bindHolder(context, click_photo_urls,position)
    }

    override fun getItemCount(): Int = click_photo_urls.size

    fun updateData(){ // 추가, 삭제시 업데이트
//        click_photo_urls.clear()
//        click_photo_urls.add(Uri.parse("0"))  // 숫자 및 추가 카드
//        result_photo.clear()
//        click_photo_urls.addAll(click_photo_uri)
        notifyDataSetChanged()
    }

    fun reset_Data(){
//        Log.d("@@@@@@@@@@@@",""+click_photo_uri)
//        click_photo_urls.clear()
//        Log.d("@@@@@@@@@@@@",""+click_photo_uri)
//        click_photo_urls.add(Uri.parse("0"))  // 숫자 및 추가 카드
//        result_photo.clear()
    }
}