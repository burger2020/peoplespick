package picturePick.picture.main_user.profile_rating.bottom_menu_activity

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.GridView
import kotlinx.android.synthetic.main.fragment_edit_frag_first.view.*
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.buttonClicked
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_position
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.click_photo_uri
import picturePick.picture.main_user.profile_rating.bottom_menu_activity.Edit_adapter_gallary.Companion.now_pickPhoto_num
import picturePick.picture.main_user.profile_rating.data.User_Data


class Edit_frag_first : Fragment(){

    companion object {
        fun newInstance(click_photo_uri:ArrayList<Uri>,click_photo_position : ArrayList<Int>,now_pickphoto_num:Int,
                        add_pickphoto_num:Int): Edit_frag_first {
            val fragment = Edit_frag_first()
            val arg = Bundle()
            arg.putParcelableArrayList(User_Data.CLICK_PHOTO_URI,click_photo_uri)
            arg.putIntegerArrayList(User_Data.CLICK_PHOTO_POSITION,click_photo_position)
            arg.putInt(User_Data.NOW_PICKPHOTO_NUM,now_pickphoto_num)
            arg.putInt(User_Data.ADD_PICKPHOTO_NUM,add_pickphoto_num)
            fragment.arguments = arg
            return fragment
        }
        fun newInstance() : Edit_frag_first{
            val fragment = Edit_frag_first()
            return fragment
        }

    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setRetainInstance(true)
        val rootView = inflater!!.inflate(R.layout.fragment_edit_frag_first, container, false)


        val proj = arrayOf(Images.Media._ID, Images.Media.DATA, Images.Media.DISPLAY_NAME)
        var idx: MutableList<Int> = listOf(0,0,0) as MutableList<Int>
        val projection =arrayOf(MediaStore.Images.Media.DATA)
//        val resolver : ContentResolver = context.contentResolver
//        val cursor: Cursor = MediaStore.Images.Media.query(resolver, Images.Media.EXTERNAL_CONTENT_URI, proj)
        val cursor: Cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                projection, // DATA를 출력
                null,       // 모든 개체 출력
                null,
                null)      // 정렬 안 함

        var result : ArrayList<Uri> = arrayListOf(Uri.parse("0"))
        var check_ : ArrayList<Boolean> = arrayListOf(false)
        val dataColumnIndex = cursor.getColumnIndex(projection[0])

        //처음 켰을때 커서로 사진 가져오는 부분
        if(cursor!= null && cursor.moveToFirst()){
            idx.set(0, cursor.getColumnIndex(proj.get(0)))
            idx.set(1, cursor.getColumnIndex(proj.get(1)))
            idx.set(2, cursor.getColumnIndex(proj.get(2)))
            do { // 커서 돌면서 갤러리 사진 로드
                val filePath = cursor.getString(dataColumnIndex)
                val imageUri = Uri.parse(filePath)
                result.add(0,imageUri) // 이미지 경로 리스트에 스택
                check_.add(0,false)
//                val photoID = cursor.getInt(idx.get(0))
//                val photoPath = cursor.getString(idx.get(1))
//                val displayName = cursor.getString(idx.get(2))
//                if(displayName != null){
//                    var photo = PhotoData()
//                    photo.photoID = photoID
//                    photo.photoPath = photoPath
//                    photoList.add(0,photo)
//                }
            }while (cursor.moveToNext())
            cursor.close()
        }

        // 선택목록 어뎁터
        var select_adapter = Edit_adapter_select(context, click_photo_uri)
        // 갤러리 어뎁터
        var adapter = Edit_adapter_gallary(context, result,check_, click_photo_uri, click_photo_position)  // 어댑터에 데이터 연결
//        select_adapter.reset_Data()
        adapter.reset()
        // 빽으로 돌아왔을경우 데이터 유지
        if(arguments != null){

            click_photo_uri = arguments.getParcelableArrayList(User_Data.CLICK_PHOTO_URI)
            click_photo_position = arguments.getIntegerArrayList(User_Data.CLICK_PHOTO_POSITION)
            now_pickPhoto_num = arguments.getInt(User_Data.NOW_PICKPHOTO_NUM)
            val add_pickphoto_num = arguments.getIntegerArrayList(User_Data.ADD_PICKPHOTO_NUM)

            for(i in 0..click_photo_position.size-1) // 전에 했던 체크 설정
                check_[click_photo_position[i]] = true

            select_adapter = Edit_adapter_select(context,click_photo_uri)
            adapter = Edit_adapter_gallary(context,result,check_,click_photo_uri,click_photo_position)
        }

        // 선택 목록 리사이클러뷰
        val select_list = rootView.edit_select_images_list
        select_list.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        select_list.layoutParams= FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,context.resources.displayMetrics.heightPixels/4)
        select_list.adapter = select_adapter
        Edit_Activity.flow = 0
        val more_photo_image = rootView.edit_photo_more_image // 사진 3장이상일경우 화살표 표시할 이미지뷰

        var lastItemVisibleFlag = false
        select_list.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, ScrollState: Int) {
                if(now_pickPhoto_num>=3-1) {
                    if (ScrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag)
                    //화면이 바닥에 닿을때 처리
                        more_photo_image.visibility = View.GONE
                    else
                        more_photo_image.visibility = View.VISIBLE
                }
            }
            override fun onScrolled(recyclerView: RecyclerView?, firstVisibleItem: Int, visibleItemCount: Int) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastItemVisibleFlag = (now_pickPhoto_num > 0) && (firstVisibleItem + visibleItemCount >= now_pickPhoto_num)
            }
        })
        //그리드뷰에서 사진 선택했을경우(클릭,클릭해제) or delete버튼 눌러서 삭제시
        buttonClicked(object : Edit_adapter_gallary.custom_interface {
            override fun custom_interface(photo_url: ArrayList<Uri>) {
                select_adapter.updateData()
                if(photo_url.size>=3)
                    more_photo_image.visibility =View.VISIBLE
                else
                    more_photo_image.visibility =View.GONE
            }
        })

        // 갤러리 어뎁터 연결
        val gallery : GridView = rootView.edit_rating_photo_gallery
        gallery.adapter = adapter
//        var imageLoader = Edit_frag_imageLoader(resolver)
//        imageLoader.setListerner(adapter)

        return rootView
    }
}// Required empty public constructor