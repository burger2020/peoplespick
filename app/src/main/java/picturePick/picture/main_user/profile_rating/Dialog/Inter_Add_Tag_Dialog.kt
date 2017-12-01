package picturePick.picture.main_user.profile_rating.Dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_edit_add_photo.*
import kotlinx.android.synthetic.main.dialog_inter_add_tag.*
import picturePick.picture.main_user.profile_rating.R

/**
 * Created by main_user on 2017-10-18.
 */
class Inter_Add_Tag_Dialog : Dialog {
    var add_num = 0
    constructor(context:Context):super(context){
//        this.add_num = add_num
    }

    companion object {
        lateinit var mListener: interface_add_interestTag
        fun AddInterestTag(mListener:interface_add_interestTag) { this.mListener = mListener }
    }

    interface interface_add_interestTag {
        fun interface_add_interestTag(addTag : String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_inter_add_tag)
        val dialog_layer = interest_dialog_layer
        val dialog_add_tag_btn = tag_add_btn
        val add_tag_text = add_tag_text
        dialog_layer.layoutParams = LinearLayout.LayoutParams(context.resources.displayMetrics.widthPixels / 3*2, context.resources.displayMetrics.heightPixels / 3)
        dialog_add_tag_btn.setOnClickListener({
            if(add_tag_text.text.toString().isNotEmpty())
            mListener.interface_add_interestTag(add_tag_text.text.toString())
            dismiss()
        })

    }
}