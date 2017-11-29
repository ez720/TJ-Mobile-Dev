package edu.tjhsst.facebookproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private CallbackManager mCBM;
    private LoginButton mLoginButton;
    private TextView mNameView;
    private TextView mEmailView;
    private TextView mBirthdayView;
    private ImageView mPicView;
    private ImageView mCView;

    private void displayInfo(JSONObject jsonObject){
        try {
            String welcomeText = "Welcome " + jsonObject.getString("name") + "!";
            mNameView.setText(welcomeText);
            String emailText;
            if(!jsonObject.isNull("email")){
                emailText = "Email: " + jsonObject.getString("email");
            }
            else{
                emailText = "No email found";
            }
            mEmailView.setText(emailText);
            String user_id = jsonObject.getString("id");
            /*JSONObject town = jsonObject.getJSONObject("hometown");
            String hometown = town.getString("name");
            String birthdayText = "Hometown: " + hometown;
            mBirthdayView.setText(birthdayText);
            JSONObject JOSource = jsonObject.optJSONObject("cover");
            String coverPhoto = JOSource.getString("source");
            Picasso.with(MainActivity.this)
                    .load(coverPhoto)
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(mCView);*/
            Picasso.with(MainActivity.this)
                    .load("https://graph.facebook.com/" + user_id + "/picture?type=large")
                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(mPicView);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNameView = (TextView)findViewById(R.id.nameView);
        mEmailView = (TextView)findViewById(R.id.emailView);
        mBirthdayView = (TextView)findViewById(R.id.birthdayView);
        mLoginButton = (LoginButton)findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("public_profile");
        mLoginButton.setReadPermissions("email");
        mPicView = (ImageView) findViewById(R.id.pfpView);
        mCView = (ImageView) findViewById(R.id.coverView);
        mCBM = CallbackManager.Factory.create();
        mLoginButton.registerCallback(mCBM, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                displayInfo(object);
                            }
                    });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,id");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                mNameView.setText(R.string.cancelled);
                mEmailView.setText("");
                mPicView.setImageBitmap(null);
            }

            @Override
            public void onError(FacebookException e) {
                mNameView.setText(R.string.cancelled);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent d) {
        mCBM.onActivityResult(requestCode, resultCode, d);
    }
}
