package picturePick.picture.main_user.profile_rating.Auth

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.StorageMetadata
import kotlinx.android.synthetic.main.activity_auth__main.*
import picturePick.picture.main_user.profile_rating.MainActivity
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.my_post_put_data
import picturePick.picture.main_user.profile_rating.MainActivity.Companion.storageRef
import picturePick.picture.main_user.profile_rating.R
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.AUTH_DATA
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.AUTH_RESULT
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.NON_PROFILE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILE_CHANGE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.PROFILE_STATE
import picturePick.picture.main_user.profile_rating.data.pic_pic_string_data.Companion.SharedPreferenceS
import java.io.ByteArrayOutputStream


open class Auth_MainActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    var fragment : Fragment? = null
    companion object {
        lateinit var my_post_put_data: SharedPreferences.Editor
        lateinit var my_post_get_data: SharedPreferences
        lateinit var progress_bar : RelativeLayout
    }
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.sign_in_button->{
                signIn()
            }
            R.id.custom_sign_up->{
                fragment = Auth_frag_sign_up.newInstance(false)
                var fragmentManager: FragmentManager = supportFragmentManager
                var tramsaction = fragmentManager.beginTransaction()
                tramsaction?.replace(R.id.auth_sign_up_container, fragment)?.commit()
                auth_sign_in_container.visibility = View.GONE
                auth_sign_up_container.visibility = View.VISIBLE
            }
        }
    }

    private val RC_SIGN_IN = 9001

    private lateinit var mAuth :FirebaseAuth
    private lateinit var mGoogleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth__main)


        sign_in_button.setOnClickListener(this)
        custom_sign_up.setOnClickListener(this)

        progress_bar = progressbar_Auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        mAuth = FirebaseAuth.getInstance()

        //회원가입 완료시 형식 오류 체크하고 가입
        Auth_frag_sign_up.interface_sign_up_complete(object :Auth_frag_sign_up.interface_sign_up_complete{
            override fun interface_sign_up_complete(uemail: String, uname: String, upass: String) {
                //처음 가입시

                if (uemail == "non" || upass == "non") {
                    val user = mAuth.currentUser
                    val profileUpdates: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                            .setDisplayName(uname)  // 사용자 이름 업데이트
                            .setPhotoUri(Uri.parse("/PROFILE/"+user?.uid))
                            .build()
                    user?.updateProfile(profileUpdates)!!.addOnSuccessListener {
                        //첫 접속일경우닉네임 정하기
                        Log.d("result","!!!!!!!!!!!!!!")
                        my_post_put_data.putBoolean(user?.uid + AUTH_DATA, true)
                        my_post_put_data.commit()
                        progress_bar.visibility = View.GONE

                        //메타 데이터
                        val metadata : StorageMetadata = StorageMetadata.Builder()
                                .setCustomMetadata(PROFILE_STATE,NON_PROFILE)
                                .setCustomMetadata(PROFILE_CHANGE,System.currentTimeMillis().toString())
                                .build()
                        //기본 아이콘 저장
                        val icon = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_non_profile)
                        val baos = ByteArrayOutputStream()
                        icon.compress(Bitmap.CompressFormat.JPEG,100,baos)
                        val data = baos.toByteArray()
                        val uploadTask = storageRef.child("/PROFILE/"+user?.uid).putBytes(data,metadata)
                        uploadTask.addOnSuccessListener {
                            Log.d("successsuccess","success")
                        }
                        updateUI(user)
                    }.addOnFailureListener({
                    })
                }
                else{
                    mAuth.createUserWithEmailAndPassword(uemail, upass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            my_post_put_data.putBoolean(user?.uid + AUTH_DATA, true)
                            my_post_put_data.commit()
                            val profileUpdates: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                                    .setDisplayName(uname)  // 사용자 이름 업데이트
                                    .setPhotoUri(Uri.parse("/PROFILE/"+user?.uid))  // 사진 URI 업데이트(기본값)
                                    .build()

                            user?.updateProfile(profileUpdates)!!.addOnSuccessListener {
                                //메타 데이터
                                val metadata : StorageMetadata = StorageMetadata.Builder()
                                        .setCustomMetadata(PROFILE_STATE,NON_PROFILE)
                                        .setCustomMetadata(PROFILE_CHANGE,System.currentTimeMillis().toString())
                                        .build()
                                //기본 아이콘 저장
                                val icon = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_non_profile)
                                val baos = ByteArrayOutputStream()
                                icon.compress(Bitmap.CompressFormat.JPEG,100,baos)
                                val data = baos.toByteArray()
                                val uploadTask = storageRef.child("/PROFILE/"+user?.uid).putBytes(data,metadata)
                                uploadTask.addOnSuccessListener {
                                    Log.d("successsuccess","success")
                                }

                                updateUI(user)
                            }.addOnFailureListener({
                            })
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(applicationContext, getString(R.string.auth_custom_sign_up_email_dif), Toast.LENGTH_SHORT).show()
                            updateUI(null)
                            progress_bar.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }



    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    fun signOut() {
        mAuth.signOut()

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { updateUI(null) }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                progress_bar.visibility = View.VISIBLE
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    // 시작시 로그인 돼있는지 확인
    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser

        my_post_get_data = getSharedPreferences(SharedPreferenceS, MODE_PRIVATE)
        my_post_put_data = my_post_get_data.edit()


        Log.d("currentUser",""+currentUser?.uid)
        Log.d("currentUser",""+currentUser?.providers)
        Log.d("currentUser",""+currentUser?.metadata)
        Log.d("currentUser",""+currentUser?.displayName)
        Log.d("currentUser",""+currentUser?.photoUrl)
        Log.d("currentUser",""+currentUser?.email)
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?){
        if(user!=null) {
            //첫 접속일경우닉네임 정하기
            val auth = FirebaseAuth.getInstance().currentUser
            if(auth?.photoUrl.toString() == "/PROFILE/"+auth?.uid){
                progress_bar.visibility = View.GONE
                finish()
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                fragment = Auth_frag_sign_up.newInstance(true)
                var fragmentManager: FragmentManager = supportFragmentManager
                var tramsaction = fragmentManager.beginTransaction()
                tramsaction?.replace(R.id.auth_sign_up_container, fragment)?.commit()
                auth_sign_in_container.visibility = View.GONE
                auth_sign_up_container.visibility = View.VISIBLE
                progress_bar.visibility = View.GONE
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
