package picturePick.picture.main_user.profile_rating.swipe_fragment

import android.content.Context
import android.support.annotation.NonNull
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.Post_Main_Data
import picturePick.picture.main_user.profile_rating.swipe_fragment.DemoGrid.DemoAdapter
import picturePick.picture.main_user.profile_rating.swipe_fragment.DemoGrid.DemoItem
import java.util.*

class Home_frag_grid_adapter : ArrayAdapter<DemoItem>, DemoAdapter {

    private val layoutInflater: LayoutInflater
    //상속받은 클래스에 콘텍스트가 있어서? (getter 생성하려는데)
    @get:JvmName("getContext_") private val context : Context
    private val post_data : ArrayList<Post_Main_Data>
    private val items : List<DemoItem>
    private val list_position : Int
    private val mosaic_option : Boolean


    constructor(context: Context, post_data: ArrayList<Post_Main_Data>,list_position:Int, items: List<DemoItem>) : super(context, 0, items) {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.post_data = post_data
        this.list_position = list_position
        this.items = items
        mosaic_option = false
    }
    constructor(context: Context, post_data: ArrayList<Post_Main_Data>,list_position:Int,mosaic : Boolean, items: List<DemoItem>) : super(context, 0, items) {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.post_data = post_data
        this.list_position = list_position
        this.items = items
        mosaic_option = mosaic
    }

    constructor(context: Context) : super(context, 0) {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.post_data = arrayListOf()
        this.list_position = 0
        this.items = listOf()
        mosaic_option = false
    }

    override fun getView(position: Int, convertView: View?, @NonNull parent: ViewGroup): View? {
        var mainView: View? = null
        val holder : ViewHolders

        val isRegular = getItemViewType(position) == 0

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

        try {
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
//                    .load(post_data[list_position].photo_url[position])
                        .override(300,300)
                        .thumbnail(0.1F)
                        .into(holder.home_photo_list)
//            Glide.with(context)
//                    .load(post_data[list_position].photo_url[position])
//                    .apply(RequestOptions.overrideOf(300,300))
//                    .thumbnail(0.1F)
//                    .into(holder.home_photo_list)
        }catch (e : IndexOutOfBoundsException){
        }
        //사이즈별
        if (isRegular) {
        } else {
        }


        return mainView
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
    internal class ViewHolders{
        var home_photo_list : ImageView?= null

    }


    override fun getViewTypeCount(): Int = 2

    override fun getItemViewType(position: Int): Int = if (position % 2 == 0) 1 else 0

    override fun appendItems(newItems: List<DemoItem>) {
        addAll(newItems)
        notifyDataSetChanged()
    }

    override fun setItems(moreItems: List<DemoItem>) {
        clear()
        appendItems(moreItems)
    }
}