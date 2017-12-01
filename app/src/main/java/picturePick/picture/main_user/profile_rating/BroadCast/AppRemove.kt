package picturePick.picture.main_user.profile_rating.BroadCast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by main_user on 2017-11-30.
 */
class AppRemove : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action : String = intent?.action!!
        val packageName = intent.data.schemeSpecificPart

        if(action == Intent.ACTION_PACKAGE_REMOVED){
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "페키지 제거시발", Toast.LENGTH_LONG).show();
            if("picturePick.picture.main_user.profile_rating" == packageName) {
            }
        }
        else if(action == Intent.ACTION_PACKAGE_ADDED) {
        }
    }
}