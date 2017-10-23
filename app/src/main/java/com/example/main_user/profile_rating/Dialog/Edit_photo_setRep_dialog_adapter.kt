package com.example.main_user.profile_rating.Dialog

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.main_user.profile_rating.R
import kotlinx.android.synthetic.main.list_edit_dialog_rep.view.*

/**
 * Created by main_user on 2017-10-22.
 */

class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val rep_photo= itemView.rep_photo_list_layer
    val rep_photo_list_img = itemView.rep_photo_list_image

    companion object {
        lateinit var mListener: ViewHolder.interface_pick_rep_photo
        fun ImageClicked(mListener: ViewHolder.interface_pick_rep_photo) { this.mListener = mListener }
    }
    interface interface_pick_rep_photo {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_pick_rep_photo(photo_url: Uri,photo_position:Int)
    }

    fun bindHolder(context:Context, rep_photo_list: ArrayList<Uri>,position: Int) {

        rep_photo.layoutParams= AbsListView.LayoutParams(context.resources.displayMetrics.heightPixels/4,context.resources.displayMetrics.heightPixels/3)
        rep_photo_list_img.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.heightPixels/4,context.resources.displayMetrics.heightPixels/3)
        rep_photo_list_img.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(context).load(rep_photo_list[position].toString()).into(rep_photo_list_img)

        rep_photo_list_img.setOnClickListener({
            mListener.interface_pick_rep_photo(rep_photo_list[position],position)
        })
    }
}

class Edit_photo_setRep_dialog_adapter(val context: Context, val rep_photo_list: ArrayList<Uri>) : RecyclerView.Adapter<ViewHolder>() {
    override fun getItemCount(): Int = rep_photo_list.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindHolder(context, rep_photo_list,position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.list_edit_dialog_rep,parent,false)
        return ViewHolder(mainView)
    }
}