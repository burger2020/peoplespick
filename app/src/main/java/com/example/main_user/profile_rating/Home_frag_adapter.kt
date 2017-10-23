//package com.example.main_user.profile_rating
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import kotlinx.android.synthetic.main.list_home_recycle.view.*
//
///**
// * Created by main_user on 2017-10-14.
// */
//class Select_ViewHolder(view: View) : RecyclerView.Select_ViewHolder(view){
//
//    val rating_photo = view.rating_photo
//    val photo_num_txt = view.photo_num_txt
//    val rater_num_txt = view. rater_num_txt
//    val rating_comment_txt = view.rating_comment_txt
//    val recomment_num_txt = view.recomment_num_txt
//
//    fun bindHolder(context: Context, data:WeatherForecast, delClick : View.OnClickListener?) {
//        descript.text = data.current[0].description
//        weatherIcon.loadUrl(data.iconUrl + data.current[0].icon + ".png")
//        currentTemp.text = String.format("%s \\u2103", data.current.main.temp)
//
//        val format: String = "%s \\u2103 / %s \\u2103"
//        highRowTemp.text = String.format(format, data.current.main.temp_min, data.current.main.temp_max)
//        cityName.text = data.current.cityName
//        cloudy.text = String.format("%s %%", data.current.clouds.all)
//        humidity.text = String.format("%s %%", data.current.main.humidity)
//        wind.text = data.current.wind.speed
//        forecast.SetView(data.week.list, data.iconUrl)
//        delbtn.setOnClickListener(delClick)
//        delbtn.tag = data.current.api_id
//    }
//}
//fun ImageView.loadUrl(url:String){
//    Picasso.with(context).load(url).into(this)
//}
//
//class WeatherListViewAdapter(val context: Context, val data:ArrayList<WeatherForecast>): RecyclerView.Adapter<Select_ViewHolder>(){
//    var mWeatherData = ArrayList<WeatherForecast>(data)
//    var delBtnClickListener: View. OnClickListener?=null
//
//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): Select_ViewHolder {
//        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val mainView = inflater.inflate(R.layout.layout_card,parent,false)
//        return Select_ViewHolder(mainView)
//    }
//
//    override fun onBindViewHolder(holder: Select_ViewHolder?, position: Int) {
//        val data = mWeatherData[position]
//        holder?.bindHolder(context,data,delBtnClickListener)
//    }
//
//    fun setDeleteClickListener(onClick : (View) ->Unit){
//        delBtnClickListener = object : View.OnClickListener{
//            override fun onClick(view: View?) {
//                onClick(view)
//            }
//        }
//    }
//    fun updateData(newData:ArrayList<WeatherForecast>){
//        mWeatherData.clear()
//        mWeatherData.addAll(newData)
//        notifyDataSetChanged()
//    }
//
//
//    fun removeData(api_id:String){
//        for(i in mWeatherData){
//            if(i.current.api_id.equals(api_id)){
//                mWeatherData.remove(i)
//                break
//            }
//        }
//        notifyDataSetChanged()
//    }
//    override fun getItemCount(): Int = mWeatherData.size
//}