package picturePick.picture.main_user.profile_rating.bottom_menu_activity

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.instacart.library.truetime.TrueTime
import kotlinx.android.synthetic.main.activity_edit_.*
import kotlinx.android.synthetic.main.fragment_edit_frag_second.*
import kotlinx.android.synthetic.main.toolbar_edit.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.firestore_
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.user_info
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.add_pickPhoto_num
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_position
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_uri
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.now_pickPhoto_num
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_frag_second.Companion.end_time
import picturePick.picture.main_user.profile_rating.data.*
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.RATING_POST
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.TAG_LIST
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread


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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_)

        if(savedInstanceState != null) {
            tabIndex = savedInstanceState.getInt("tabIndex")
        }

        //TODO 사진 업로드때 쓰는놈
        var uploadTask : UploadTask

        val actionBar : ActionBar? = supportActionBar
        actionBar?.elevation = 0F
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(false)            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar?.setDisplayShowTitleEnabled(false)        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar?.setDisplayShowHomeEnabled(false)            //홈 아이콘을 숨김처리합니다.
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val actionbar = inflater.inflate(R.layout.toolbar_edit, null)
        actionBar?.customView = actionbar

        var fragment: Fragment =  Edit_frag_first.newInstance()
        var fragmentManager: FragmentManager = supportFragmentManager
        var tramsaction = fragmentManager.beginTransaction()
        tramsaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        tramsaction?.replace(R.id.edit_container, fragment)?.commit()

        val edit_toolbar_close = edit_toolbar_close
        val progressbar = progressbar_view
        val edit_toolbar_next = edit_toolbar_next
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
                edit_toolbar_close.background = resources.getDrawable(R.mipmap.ic_edit_toolbar_back)
                edit_toolbar_next.text = getText(R.string.edit_tool_produce)
            } else if(flow ==1){ // frag 2 완료 작업
                //내용 입력창 포커싱으로 키보드 올라온거 내리기
                val imm :InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(edit_photo_comment.windowToken,0)

                progressbar.visibility = View.VISIBLE
                flow =2

                val edit_comment :String = edit_photo_comment.text.toString()

                //등록시간 구하기
                val data_time = TrueTime.now()
                val data_times : Long = data_time.time

                //평가시간 인덱스마다 시간추가
                var cal  : Calendar = Calendar.getInstance()
                cal.time = data_time
                when(end_time){
                    0->{ cal.add(Calendar.HOUR,1) }
                    1->{ cal.add(Calendar.HOUR,3) }
                    2->{ cal.add(Calendar.HOUR,6) }
                    3->{ cal.add(Calendar.HOUR,12) }
                    4->{ cal.add(Calendar.DATE,1) }
                    5->{ cal.add(Calendar.DATE,2) }
                    6->{ cal.add(Calendar.DATE,3) }
                }

                // 평가 끝나는 날짜,시간
                val rating_end_time = cal.time

                var photo_uri : ArrayList<String> = arrayListOf()
                //사진 결과 수 저장 ( 사진 갯수만큼 0 으로 초기화해서 저장)
                var post_rating_result = arrayListOf<Int>()

                val option_info_data = option_info_data(edit_mosaic_bool, edit_capture_bool, edit_comment_bool, edit_invisible_bool)
                //파이어베이스 저장소에 비교 사진들 저장
                val storage_uri: ArrayList<String> = arrayListOf()

                //태그 글자 추출
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
                    firestore_.collection(TAG_LIST).document(Tag_data[i]).get().addOnCompleteListener({
                        try {
                            val a = it.result[Tag_data[i]] as Long
                            firestore_.collection(TAG_LIST).document(Tag_data[i]).set(mapOf(Tag_data[i] to a+1))
                        }catch (e : IllegalStateException){
                            firestore_.collection(TAG_LIST).document(Tag_data[i]).set(mapOf(Tag_data[i] to 1))
                        }
                    })
                }

                //uri 스티링으로 형변환
                (0 until click_photo_uri.size).mapTo(photo_uri) { click_photo_uri[it].toString()}

                for(i in 0 until photo_uri.size) {
                    val bounds = BitmapFactory.Options()
                    bounds.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(photo_uri[i], bounds)

                    var resize = (bounds.outWidth/500) + (bounds.outHeight/500)
                    if(resize>5)
                        resize = 5
                    else if(resize == 0 )
                        resize = 1
//                    resize = 1
                    post_rating_result.add(0)
                    val baos = ByteArrayOutputStream()

                    //회전 정방향으로
                    val exif = ExifInterface(photo_uri[i])
                    val orientation : Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                    val bitmap : Bitmap =
                            rotate(MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, Uri.fromFile(File(photo_uri[i]))) as Bitmap ,orientation)
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100/resize,baos)
                    val data = baos.toByteArray()
                    val imagesRef = storageRef.child("Rating_Image/"+data_times+"_"+i)  // 경로 정하기
//                    val imagesRef = storageRef.child("Test")  // 경로 정하기
//                    storage_uri.add("Rating_Image/"+data_times+"_"+i)
                    uploadTask = imagesRef.putBytes(data)
                    uploadTask.addOnSuccessListener {
                        //이게 젤 빠른듯
                        //URI
//                        storage_uri.add(it.downloadUrl.toString())
                        //파이어베이스 경로
                        storage_uri.add(it.storage.path)
                        if(storage_uri.size==photo_uri.size){
                            //TODO 포스트 생성시 포함 데이터
                            val poster_name = user_info.dis_name
                            val Post_Main_Data = Post_Main_Data(
                                    storage_uri.size,storage_uri,edit_comment,option_info_data ,
                                    data_times,rating_end_time.time,poster_name,0,user_info.profile,0, user_info.id,Tag_data)
                            //데이터베이스에 저장
                            //                                내 게시물 id 저장하는건데 orderByChild 로 찾으면 필요없을듯
/*
                                //shared 에 저장할것 들
                                MainActivity.my_post_put_data.putLong(my_post_num.toString()+POST_ID,data_times)
                                MainActivity.my_post_put_data.putInt(MY_POST_NUM_CHECK,(my_post_num+1))
                                MainActivity.my_post_put_data.commit()
*/
                            //파이어 스토어
                            firestore_.collection(RATING_POST).document(""+(10000000000000-data_times)).set(
                                    post_rater_data(0,arrayListOf(comment_data("","","",0,"")),post_rating_result))
                            firestore_.collection("post").document(""+(10000000000000-data_times)).set(Post_Main_Data).addOnSuccessListener({
                                firestore_.collection("post").document(""+(10000000000000-data_times)).update(Tag_map as Map<String, Any>)
                                MainActivity.mListener.interface_photo_list_edit_complate(Post_Main_Data)
                            })
                            setResult(pic_pic_string_data.EDIT_COMPLETE)
                            click_photo_position.clear()
                            click_photo_uri.clear()
                            now_pickPhoto_num = 0
                            add_pickPhoto_num=0
                            flow = 0
                            progressbar.visibility = View.GONE
                            finish()
                        }
                    }
                }
            }
        })
    }
    private fun rotate(bitmap: Bitmap, orientation : Int): Bitmap {
        val matrix = Matrix()
        when(orientation){
            ExifInterface.ORIENTATION_NORMAL->{
                return bitmap
            }
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL->{
                matrix.setScale(-1F,1F)
            }
            ExifInterface.ORIENTATION_ROTATE_180->{
                matrix.setRotate(180F)
            }
            ExifInterface.ORIENTATION_FLIP_VERTICAL->{
                matrix.setRotate(180F)
                matrix.postScale(-1f,1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE->{
                matrix.setRotate(90F)
                matrix.postScale(-1F,1F)
            }
            ExifInterface.ORIENTATION_ROTATE_90->{
                matrix.setRotate(90F)
            }
            ExifInterface.ORIENTATION_TRANSVERSE->{
                matrix.setRotate(90F)
                matrix.postScale(-1F,1F)
            }
            ExifInterface.ORIENTATION_ROTATE_270->{
                matrix.setRotate(-90f)
            }
            else->{
                return bitmap
            }
        }
        return try{
            val btmRotated = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
            bitmap.recycle()
            btmRotated
        }catch (e : OutOfMemoryError){
            bitmap
        }
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
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
