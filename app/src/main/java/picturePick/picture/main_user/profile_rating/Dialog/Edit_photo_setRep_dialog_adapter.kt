package picturePick.picture.main_user.profile_rating.Dialog

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
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.list_edit_dialog_rep.view.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_frag_second

/**
 * Created by main_user on 2017-10-22.
 */

class Edit_rep_photo_ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val rep_photo= itemView.rep_photo_list_layer
    val rep_photo_list_img = itemView.rep_photo_list_image

    fun bindHolder(context:Context, rep_photo_list: ArrayList<String>,custom_bool:Boolean,position: Int) {

        rep_photo.layoutParams= AbsListView.LayoutParams(context.resources.displayMetrics.heightPixels/4,context.resources.displayMetrics.heightPixels/3)
        rep_photo_list_img.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.heightPixels/4,context.resources.displayMetrics.heightPixels/3)
        rep_photo_list_img.scaleType = ImageView.ScaleType.CENTER_CROP
        if(!custom_bool)
            Glide.with(context)
                    .load(rep_photo_list[position])
                    .into(rep_photo_list_img)
        else
            Glide.with(context)
//                    .load(storageRef.child(rep_photo_list[position]))
                    .load(rep_photo_list[position])
                    .into(rep_photo_list_img)
    }
}

class Edit_photo_setRep_dialog_adapter(val context: Context, val rep_photo_list: ArrayList<String>,val custom_bool:Boolean) : RecyclerView.Adapter<Edit_rep_photo_ViewHolder>() {
    override fun getItemCount(): Int = rep_photo_list.size

    override fun onBindViewHolder(holderEditrepphoto: Edit_rep_photo_ViewHolder?, position: Int) {
        holderEditrepphoto?.bindHolder(context, rep_photo_list,custom_bool,position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Edit_rep_photo_ViewHolder {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.list_edit_dialog_rep,parent,false)
        return Edit_rep_photo_ViewHolder(mainView)
    }
}