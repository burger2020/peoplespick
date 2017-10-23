package com.example.main_user.profile_rating.bottom_menu_activity

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.main_user.profile_rating.Dialog.Edit_Photo_Add_Dialog
import com.example.main_user.profile_rating.R
import com.example.main_user.profile_rating.data.User_Data


/**
 * Created by main_user on 2017-10-15.
 */
class Edit_adapter_gallary(val context:Context, val photo_url:ArrayList<Uri>, val check_:ArrayList<Boolean>,
                           click_photo_uri_init: ArrayList<Uri>, click_photo_position_init : ArrayList<Int>) : BaseAdapter(){


    companion object {
        var click_photo_uri: ArrayList<Uri> = arrayListOf()
        var click_photo_position : ArrayList<Int> = arrayListOf()
        var now_pickPhoto_num :Int = 0
        var add_pickPhoto_num :Int = 0
        lateinit var mListener: custom_interface
        fun buttonClicked(mListener:custom_interface) { this.mListener = mListener }
    }
    init {
        click_photo_uri = click_photo_uri_init
        click_photo_position = click_photo_position_init
    }

    interface custom_interface {
        fun custom_interface(photo_url: ArrayList<Uri>)
    }
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        val holder: ViewHolder
        var mainView : View? = null
        if(mainView == null){
            val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mainView  = inflater.inflate(R.layout.list_edit_gallary,parent,false)
            holder = ViewHolder()
            holder.imageView = mainView.findViewById(R.id.edit_photo)
            holder.framLayout = mainView.findViewById(R.id.edit_layout)
            holder.checkBox = mainView.findViewById(R.id.edit_checkbox)
            holder.framLayout?.layoutParams=AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,context.resources.displayMetrics.heightPixels/6)
            holder.imageView?.layoutParams=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT)
            holder.checkBox?.layoutParams=RelativeLayout.LayoutParams(context.resources.displayMetrics.heightPixels/18,context.resources.displayMetrics.heightPixels/18)
            holder.imageView?.scaleType = ImageView.ScaleType.CENTER_CROP

            //사진 선택 이벤트
            holder.imageView?.setOnClickListener({
                    if(check_[position]) { // 체크 해제(체크 돼있을시)
                        now_pickPhoto_num--
                        holder.checkBox?.setBackgroundResource(R.mipmap.ic_edit_checkbox_nonselect)
                        check_.set(position,false)
                        click_photo_uri.remove(photo_url[position])
                        click_photo_position.remove(position)
                    }
                    else if(!check_[position]&&User_Data.pick_photo_num>now_pickPhoto_num){  // 체크  (체그 안되어있을시 && 최대 선택 이하)
                        now_pickPhoto_num++
                        holder.checkBox?.setBackgroundResource(R.mipmap.ic_edit_checkbox_select)
                        click_photo_uri.add(0,photo_url[position])
                        click_photo_position.add(0,position)
                        check_.set(position,true)
                    }else if(!check_[position]&&User_Data.pick_photo_num<=now_pickPhoto_num){
                        val dialog = Edit_Photo_Add_Dialog(context,now_pickPhoto_num)
                        dialog.show()
                    }
                mListener.custom_interface(click_photo_uri)
            })
            mainView.tag = holder
        }else{
            holder = view?.tag as ViewHolder
        }
        Select_ViewHolder.ImageClicked(object : Select_ViewHolder.interface_delete_image {
            override fun interface_delete_image(photo_url: Uri, photo_position: Int) {
                now_pickPhoto_num--
                check_.set(photo_position,false)
                mListener.custom_interface(click_photo_uri)
                notifyDataSetChanged()
            }
        })
        //체크 확인하고 표시 ( 리사이클러뷰 화면 지워지면 저장안되있음)
        if(check_[position])
            holder.checkBox?.setBackgroundResource(R.mipmap.ic_edit_checkbox_select)
        else
            holder.checkBox?.setBackgroundResource(R.mipmap.ic_edit_checkbox_nonselect)
        Glide.with(context).load(photo_url[position].toString()).apply(RequestOptions().encodeQuality(30)).into(holder.imageView)

            return mainView
    }

    internal class ViewHolder{
        var imageView : ImageView?= null
        var checkBox : ImageView?= null
        var framLayout : RelativeLayout?= null
    }

    fun reset(){
        now_pickPhoto_num=0
    }
    override fun getItem(p0: Int): Any {
        return true
    }

    override fun getItemId(id: Int): Long {
        return 0
    }

    override fun getCount(): Int = photo_url.size
}