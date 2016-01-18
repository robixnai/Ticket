package br.com.cast.ticket.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.cast.ticket.R;
import br.com.cast.ticket.entity.User;
import br.com.cast.ticket.util.AppUtil;

/**
 * @author Charleston Anjos <charleston10>
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    /* related the api google login */
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;

    private static final int RC_SIGN_IN = 0;
    private static final int REQUEST_CODE_RESOLUTION = 89898;//Request code for auto Google Play Services error resolution.

    /* related the view */
    private ProgressDialog mProgressDialog;
    private SignInButton mBtnGoogleApi;
    private TextView mTvUser,mTvUserEmail;
    private EditText mEtUserEmail,mEtUserPassword;
    private Button mBtnSignIn, mBtnSignUp;

    /* others */
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //this.getActionBar().hide();

        getSupportActionBar().hide();

        mGoogleApiClient = buildApiClient();

        bindElements();
    }

    private void resolveSigInError(){
        if (mConnectionResult.hasResolution()) {
            try {
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void bindElements(){
        mBtnGoogleApi = (SignInButton) findViewById(R.id.btnGoogleSigIn);
        mEtUserEmail = (EditText) findViewById(R.id.etUserEmail);
        mEtUserPassword = (EditText) findViewById(R.id.etUserPassword);
        mBtnSignUp = (Button) findViewById(R.id.btnSignUp);
        mBtnSignIn = (Button) findViewById(R.id.btnSignIn);

        bindEvents();
    }

    private void bindEvents(){
        mBtnGoogleApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGoogleApiClient.isConnecting()) {

                    mGoogleApiClient.connect();
                    mBtnGoogleApi.setEnabled(false);

                    String title = getResources().getString(R.string.progress_authenticate);
                    String message = getResources().getString(R.string.progress_wait);

                    mProgressDialog = buildProgressDialog(title, message);
                }
            }
        });

        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<View> elements = new ArrayList<View>();

                elements.add(mEtUserEmail);
                elements.add(mEtUserPassword);

                boolean formValid = AppUtil.validForm(elements,LoginActivity.this);

                if(formValid==true){

                    User user = new User();
                    user.setFirstName("Pocket");
                    user.setEmail(mEtUserEmail.getText().toString());
                    user.setPassword(mEtUserPassword.getText().toString());

                    Intent intentMain = new Intent(LoginActivity.this,MainActivity.class);
                    intentMain.putExtra("user",user);
                    startActivity(intentMain);
                }
            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegisterUser = new Intent(LoginActivity.this, RegisterUserActivity.class);
                startActivity(intentRegisterUser);
            }
        });
    }

    private User getProfileGoogle(){
        User user = new User();

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                user.setFirstName(personName);
                user.setEmail(personEmail);
                user.setPassword(mEtUserPassword.getText().toString());

                //get image of user
                //new LoadProfileImage(mIvUser).execute(personPhotoUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    private GoogleApiClient buildApiClient(){
        return new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    private ProgressDialog buildProgressDialog(String title, String message){
        return ProgressDialog.show(LoginActivity.this, title, message, false, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mProgressDialog!=null) mProgressDialog.dismiss();

        final User user = getProfileGoogle();

        final Intent intentMain = new Intent(this,MainActivity.class);
        intentMain.putExtra("user",user);
        startActivity(intentMain);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            mProgressDialog.dismiss();
            mBtnGoogleApi.setEnabled(true);
        }else{
            // store mConnectionResult
            mConnectionResult = connectionResult;
            resolveSigInError();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    /*********** Private Classes ***********/

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Task for retrieving the token of the connected user
     */
    private class GetTokenTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... userAccount) {
            String token = null;
            try {
                Log.d(TAG, "Retrieving token for [" + userAccount[0] + "]");
                final String scope = "oauth2:" + Scopes.PROFILE;
                token = GoogleAuthUtil.getToken(getApplicationContext(), userAccount[0], scope, new Bundle());

            } catch (UserRecoverableAuthException e) {
                Log.w(TAG, "Error retrieving the token: " + e.getMessage());
                Log.d(TAG, "Trying to solve the problem...");
                startActivityForResult(e.getIntent(), REQUEST_CODE_RESOLUTION);
            } catch (IOException e) {
                Log.e(TAG, "Unrecoverable I/O exception: " + e.getMessage(), e);
            } catch (GoogleAuthException e) {
                Log.e(TAG, "Unrecoverable authentication exception: " + e.getMessage(), e);
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                Toast.makeText(getApplicationContext(), "Access token: " + token, Toast.LENGTH_SHORT).show();
                //mToken = token;
                Log.d(TAG, "We have a token!: " + token);
            } else {
                Log.d(TAG, "Can't retrieve any token");
            }
        }
    }
}
