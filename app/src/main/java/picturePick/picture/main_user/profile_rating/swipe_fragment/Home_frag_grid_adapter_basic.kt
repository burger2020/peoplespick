package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.NonNull
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.swipe_fragment.DemoGrid.DemoAdapter
import picturePick.picture.main_user.profile_rating.swipe_fragment.DemoGrid.DemoItem
import java.util.*

class Home_frag_grid_adapter_basic : BaseAdapter {

    var test_time = 0L
    private val layoutInflater: LayoutInflater
    //상속받은 클래스에 콘텍스트가 있어서? (getter 생성하려는데)
    @get:JvmName("getContext_") private val context : Context
    private val post_data : ArrayList<Post_Main_Data>
    private val list_position : Int
    private val mosaic_option : Boolean


    constructor(context: Context, post_data: ArrayList<Post_Main_Data>,list_position:Int,mosaic : Boolean) : super() {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.post_data = post_data
        this.list_position = list_position
        mosaic_option = mosaic
    }

    constructor(context: Context) : super() {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.post_data = arrayListOf()
        this.list_position = 0
        mosaic_option = false
    }

    @SuppressLint("LongLogTag")
    override fun getView(position: Int, convertView: View?, @NonNull parent: ViewGroup): View? {
        var mainView: View? = null
        val holder : ViewHolders

        if (mainView == null) {
            val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mainView = inflater.inflate(R.layout.list_home_recycle_glid_list, parent, false)
            holder = ViewHolders()

            holder.home_photo_list = mainView.findViewById(R.id.home_photo_list)
            holder.home_photo_list?.scaleType = ImageView.ScaleType.CENTER_CROP

//            DownloadImageTask(position).execute(post_data[list_position].photo_url[position])
        } else {
            holder = convertView?.tag as ViewHolders
        }

        if(count == 3 || count == 2)
            holder.home_photo_list?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.resources.displayMetrics.widthPixels /3*2)
        else if(count==4|| count==6){
            holder.home_photo_list?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.resources.displayMetrics.widthPixels /2)
        }
        try {
            test_time = System.currentTimeMillis()
//            Log.d("System.currentTimeMillis()",""+System.currentTimeMillis())
            if(mosaic_option)
                Glide.with(context)
                        //파이어베이스 경로
                        .using(FirebaseImageLoader())
                        .load(MainActivity.storageRef.child(post_data[list_position].photo_url[position]))
                        //URI
//                        .load(post_data[list_position].photo_url[position])
                        .override(50,50)
                        .thumbnail(0.1F)
                        .into(holder.home_photo_list)
            else
                Glide.with(context)
                        //파이어베이스 경로
                        .using(FirebaseImageLoader())
                        .load(MainActivity.storageRef.child(post_data[list_position].photo_url[position]))
                        //URI
//                        .load(post_data[list_position].photo_url[position])
//                        .listener(requestListener1)
                        .override(300,300)
                        .thumbnail(0.2F)
                        .into(holder.home_photo_list)
//            Glide.with(context)
//                    .load(post_data[list_position].photo_url[position])
//                    .apply(RequestOptions.overrideOf(300,300))
//                    .thumbnail(0.1F)
//                    .into(holder.home_photo_list)
        }catch (e : IndexOutOfBoundsException){
        }


        return mainView
    }


//    val requestListener = object : RequestListener<String, GlideDrawable> {
//        override fun onException(e: Exception, model: String, target: Target<GlideDrawable>, isFirstResource: Boolean): Boolean {
//            // 예외사항 처리
//            return false
//        }
//
//        @SuppressLint("LongLogTag")
//        override fun onResourceReady(resouorce: GlideDrawable, model: String, target: Target<GlideDrawable>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
//            // 이미지 로드 완료됬을 때 처리
//            Log.d("System.currentTimeMillis()",""+(System.currentTimeMillis() - test_time))
//            return false
//        }
//    }
//    val requestListener1 = object : RequestListener<StorageReference, GlideDrawable> {
//        override fun onException(e: java.lang.Exception?, model: StorageReference?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
//            return false
//        }
//
//        @SuppressLint("LongLogTag")
//        override fun onResourceReady(resource: GlideDrawable?, model: StorageReference?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
//            Log.d("System.currentTimeMillis()",""+(System.currentTimeMillis() - test_time))
//            return false
//        }
//    }
    internal class ViewHolders{
        var home_photo_list : ImageView?= null
    }


    override fun getItem(p0: Int): Any = true

    override fun getItemId(p0: Int): Long =0

    override fun getCount(): Int {
        return if(post_data[list_position].photo_url.size !=5)
            post_data[list_position].photo_url.size
        else
            3
    }


    //    private inner class DownloadImageTask(position : Int) : AsyncTask<String, Void, Bitmap>() {
//
//        val position : Int = position
//        override fun doInBackground(vararg urls: String): Bitmap? {
//            val urldisplay = urls[0]
//            var mIcon11: Bitmap? = null
//            try {
//                val `in` = java.net.URL(urldisplay).openStream()
//                mIcon11 = BitmapFactory.decodeStream(`in`)
//            } catch (e: Exception) {
//                Log.e("Error", e.message)
//                e.printStackTrace()
//            }
//            return mIcon11
//        }
//
//        override fun onPostExecute(result: Bitmap) {
//            val stream = ByteArrayOutputStream()
//            result.compress(Bitmap.CompressFormat.PNG,100,stream)
//
//            photo_bitmap.entries(mapOf(post_data[list_position].photo_url[position] to stream.toByteArray()))
//
//            Log.d("??????????????","??????????????"+photo_bitmap)
//        }
//    }
}