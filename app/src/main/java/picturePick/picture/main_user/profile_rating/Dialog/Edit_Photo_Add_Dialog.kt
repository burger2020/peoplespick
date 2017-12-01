package picturePick.picture.main_user.profile_rating.Dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_edit_add_photo.*
import picturePick.picture.main_user.profile_rating.R

/**
 * Created by main_user on 2017-10-18.
 */
class Edit_Photo_Add_Dialog : Dialog {
    var add_num = 0
    constructor(context:Context,add_num:Int):super(context){
        this.add_num = add_num
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_add_photo)
        val edit_Dalog_Layout:LinearLayout = edit_dialog_layout
        val edit_dialog_comment : TextView = edit_photo_dialog_comment
        edit_Dalog_Layout.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 3*2, context.resources.displayMetrics.heightPixels / 3)
        edit_dialog_comment.text = (add_num+1).toString()+edit_dialog_comment.text
    }
}