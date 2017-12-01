package picturePick.picture.main_user.profile_rating.Option

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_option_.*
import picturePick.picture.main_user.profile_rating.R


class Option_Activity : AppCompatActivity(), View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mGoogleApiClient: GoogleApiClient


    override fun onClick(view: View?) {
        when(view?.id){
            R.id.sign_out_button->{//로그아웃 버튼
                signOut()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option_)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        mAuth = FirebaseAuth.getInstance()

        val sign_out_button = sign_out_button  //로그아웃 버튼

        sign_out_button.setOnClickListener(this)

    }

    fun signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback {
            Log.d("signOut","signOut")
            setResult(1818)
            finish()
        }
    }

}
