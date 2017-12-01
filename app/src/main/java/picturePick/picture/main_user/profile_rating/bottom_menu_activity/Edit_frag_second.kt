package picturePick.picture.main_user.profile_rating.bottom_menu_activity

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_edit_frag_second.view.*
import picturePick.picture.main_user.profile_rating.Dialog.Edit_Photo_SetRep_Dialog
import picturePick.picture.main_user.profile_rating.Dialog.Edit_rep_photo_ViewHolder
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.data.option_info_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data
import picturePick.picture.main_user.profile_rating.swipe_fragment.Post_Custom_act
import java.util.*


class Edit_frag_second : Fragment(){
    companion object {
        var select_photo_uri = arrayListOf<Uri>()
        var post_data = Post_Main_Data()
        var custom_bool:Boolean = false
        fun newInstance(click_photo_uri : ArrayList<Uri>): Edit_frag_second {
            val fragment = Edit_frag_second()
            select_photo_uri = click_photo_uri
            custom_bool = false
            return fragment
        }
        fun customInstance(post_data: Post_Main_Data,custom_bool:Boolean): Edit_frag_second {
            val fragment = Edit_frag_second()
            this.custom_bool = custom_bool
            this.post_data = post_data
            return fragment
        }
        var end_time : Int = 0 // 선택한 시간값 포지션

        lateinit var mListener: interface_custom_finish
        fun CustomFinish(mListener:interface_custom_finish) { this.mListener = mListener }
    }


    interface interface_custom_finish {
        // image delete 버튼 눌렀을경우 인터페이스로 사진리스트 adapter 로 넘김
        fun interface_custom_finish(post_Main_Data: Post_Main_Data)
    }

    //    lateinit var search_hashTag : ValueEventListener
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_edit_frag_second, container, false)
//        val option_rep_photo = rootView.edit_option_rep_photo
        var dialog : Dialog
        val mosaic_togle =  rootView.edit_option_mosaic
        val capture_togle  = rootView.edit_option_capture
        val comment_togle = rootView.edit_option_comment
//        val invisible_togle = rootView.edit_option_invisible
        val rating_time_select_spinner = rootView.rating_time_select
        val edit_photo_comment =rootView.edit_photo_comment
        val hash_btn  =rootView.hashTag_button
//        val storageRef : StorageReference = FirebaseStorage.getInstance("gs://pictures-pick/").reference

//        option_rep_photo.scaleType = ImageView.ScaleType.CENTER_CROP

        if(custom_bool==false) { // 일반 생성시
            var photo_uri = arrayListOf<String>()
            for(i in 0.. select_photo_uri.size-1)
                photo_uri.add(select_photo_uri[i].toString())

            dialog = Edit_Photo_SetRep_Dialog(context, photo_uri,custom_bool)

            rating_time_select_spinner.isEnabled = true

            edit_photo_comment.setText("")

            //스피너 선택
            rating_time_select_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    end_time = position
                }
            }
            mosaic_togle.isClickable = false
            capture_togle.isClickable = false
            comment_togle.isClickable = false
//            invisible_togle.isClickable = false
            Edit_Activity.edit_capture_bool = false
            Edit_Activity.edit_mosaic_bool = false
            Edit_Activity.edit_comment_bool = false
            Edit_Activity.edit_invisible_bool = false

        }
        else {    //수정하기로 들어왔을경우

            dialog = Edit_Photo_SetRep_Dialog(context, post_data.photo_url,custom_bool)


            //기존 내용 에디트택스트에 넣기
            edit_photo_comment.setText(post_data.comment)

            // 토글버튼 기존 설정으로 셋
            mosaic_togle.isChecked = false
            capture_togle.isChecked = false
            comment_togle.isChecked = false
//            invisible_togle.isChecked = false
            if(post_data.option_info_data.captuer_bool)
                capture_togle.isChecked= true
            if(post_data.option_info_data.mosaic_bool)
                mosaic_togle.isChecked= true
            if(post_data.option_info_data.invisible_bool)
//                invisible_togle.isChecked= true
//            if(post_data.option_info_data.comment_bool)
                comment_togle.isChecked= true

        }


        //수정 완료시
        Post_Custom_act.customComplete(object  : Post_Custom_act.interface_custom_post_set{
            override fun interface_custom_post_set(post_Main_Data: Post_Main_Data) {
                post_Main_Data.option_info_data = option_info_data(mosaic_togle.isChecked,capture_togle.isChecked,comment_togle.isChecked,false/*invisible_togle.isChecked*/)
                //생성자에 수정할 필드만 데이터 넣어서 객체 생성(수정안할건 안건드림)
                post_Main_Data.comment = edit_photo_comment.text.toString()
                //태그 글자 추출
                val edit_comment :String = post_Main_Data.comment
                var tag_unit = ""
                var tag_bool = false
                val Tag_data : ArrayList<String> = arrayListOf()
                val Tag_map : HashMap<String,String> = hashMapOf()
                val Tag__list_map : HashMap<String,Int> = hashMapOf()
                for(i in 0 until edit_comment.length){
                    if(edit_comment[i] == '#'){
                        tag_bool = true
                    }else if(tag_bool&&(edit_comment[i] == ' ')){
                        tag_bool = false
                        if(tag_unit.isNotEmpty()) {
                            Tag_data.add(tag_unit)
                            Tag_map.put(tag_unit, tag_unit)
                            Tag__list_map.put(tag_unit, 0)
                            tag_unit = ""
                        }
                    }else if(tag_bool&&(i == edit_comment.length-1)){
                        tag_bool = false
                        tag_unit += edit_comment[i]
                        Tag_data.add(tag_unit)
                        Tag_map.put(tag_unit, tag_unit)
                        Tag__list_map.put(tag_unit, 0)
                        tag_unit = ""
                    }else if(tag_bool&&edit_comment[i] != ' '){
                        tag_unit +=edit_comment[i]
                    }
                }
                //서버에 태그 카운팅
                for(i in 0 until Tag_data.size){
                    firestore_.collection(pic_pic_string_data.TAG_LIST).document(Tag_data[i]).get().addOnCompleteListener({
                        try {
                            val a = it.result[Tag_data[i]] as Long
                            firestore_.collection(pic_pic_string_data.TAG_LIST).document(Tag_data[i]).set(mapOf(Tag_data[i] to a+1))
                        }catch (e : IllegalStateException){
                            firestore_.collection(pic_pic_string_data.TAG_LIST).document(Tag_data[i]).set(mapOf(Tag_data[i] to 1))
                        }
                    })
                }

                //수정할 필드 객체만든거 map형식으로 변형
                firestore_.collection("post").document(""+(10000000000000-post_Main_Data.save_time)).update(
                        "comment",post_Main_Data.comment,
                        "option_info_data.mosaic_bool",post_Main_Data.option_info_data.mosaic_bool,
                        "option_info_data.invisible_bool",post_Main_Data.option_info_data.invisible_bool,
                        "option_info_data.comment_bool",post_Main_Data.option_info_data.comment_bool,
                        "option_info_data.captuer_bool",post_Main_Data.option_info_data.captuer_bool,
                        "tag",Tag_data
                ).addOnCompleteListener({
                    firestore_.collection("post").document(""+(10000000000000-post_Main_Data.save_time)).update(Tag_map as Map<String, Any>)
                    mListener.interface_custom_finish(post_Main_Data)
                })

//                MainActivity.Photo_Database.child("post").child(""+post_Main_Data.save_time).updateChildren(map).addOnCompleteListener({
//                    ((activity) as Post_Custom_act).update_finish(post_Main_Data)
//                })
            }
        })

        capture_togle.setOnClickListener({
            Edit_Activity.edit_capture_bool = capture_togle.isChecked
        })
        comment_togle.setOnClickListener({
            Edit_Activity.edit_comment_bool = comment_togle.isChecked
        })
//        invisible_togle.setOnClickListener({
//            Edit_Activity.edit_invisible_bool = invisible_togle.isChecked
//        })

        hash_btn.setOnClickListener({
            edit_photo_comment.setText(edit_photo_comment.text.toString() +" #")
            edit_photo_comment.setSelection(edit_photo_comment.length())
        })

        edit_photo_comment.addTextChangedListener(object : TextWatcher{
            var pre_text :String = ""
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                pre_text= char.toString()
            }

            override fun onTextChanged(char: CharSequence?, start: Int, before: Int, count: Int) {
                var hashTag_char = ""
                //헤쉬태그 검색(단어 찾기)
                if (edit_photo_comment.lineCount >= 5) {
                    edit_photo_comment.setText(pre_text)
                    edit_photo_comment.setSelection(edit_photo_comment.length())
                }else if(char?.length != 0 && count<2)
                    if(count == 1) {
                        for(i in start downTo 0) {
                            if (edit_photo_comment.text.toString()[i] == ' ' || edit_photo_comment.text.toString()[i] == '#') {
                                if(edit_photo_comment.text.toString()[i] == '#') {
                                    searchTag("asd")
                                    break
                                }
                            } else {
                                hashTag_char = edit_photo_comment.text.toString()[i] + hashTag_char
                            }
                        }
                    }
                    else{
                        for(i in start-1 downTo 0)
                            if(edit_photo_comment.text.toString()[i]==' '||edit_photo_comment.text.toString()[i]=='#'){
                                if(edit_photo_comment.text.toString()[i] == '#') {
                                    searchTag("asd")
                                    break
                                }
                            }
                            else {
                                hashTag_char = edit_photo_comment.text.toString()[i]+hashTag_char
                            }
                    }
            }
        })

//        search_hashTag = object :ValueEventListener{
//            override fun onDataChange(dataSnapshot: DataSnapshot?) {
//                 Log.d("dataSnapshot_TAG_Search",""+dataSnapshot)
////                dataSnapshot!!.children.forEach{snap->
////                    snap.getValue<Post_Main_Data>(picturePick.picture.main_user.profile_rating.data.Post_Main_Data::class.java)?.let{
////                        post_Main_Data.add(0,it)
////                    }
////                }
//            }
//            override fun onCancelled(p0: DatabaseError?) {}
//        }
        return rootView
    }
    //태그검색
    fun searchTag(tag : String){
//        MainActivity.Photo_Database.child("HASH_TAG").startAt("김").endAt("김"+"\uf8ff").addListenerForSingleValueEvent(search_hashTag)
//        firestore_.collection("TAG_LIST").startAt("김").endAt("김"+"\uf8ff")
    }
}// Required empty public constructor