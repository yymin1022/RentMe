package com.yong.rentme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SplashActivity extends AppCompatActivity {
    boolean isLogined;

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    Button btnLoginFacebook;
    SignInButton btnLoginGoogle;
    GoogleSignInOptions googleSignInOptions;
    FrameLayout layoutNotLogined;
    ImageView imageLogo;
    LinearLayout layoutLogined;
    LinearLayout layoutNotLoginedButton;
    SharedPreferences prefs;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btnLoginFacebook = findViewById(R.id.btn_splash_login_facebook);
        btnLoginGoogle = findViewById(R.id.btn_splash_login_google);
        imageLogo = findViewById(R.id.image_splash_logo);
        layoutLogined = findViewById(R.id.layout_splash_logined);
        layoutNotLogined = findViewById(R.id.layout_splash_notlogined);
        layoutNotLoginedButton = findViewById(R.id.layout_splash_notlogined_btn);

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        ed = prefs.edit();
        isLogined = prefs.getBoolean("isLogined", false);

        firebaseAuth = FirebaseAuth.getInstance();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        if(isLogined){
            layoutLogined.setVisibility(View.VISIBLE);
            layoutNotLogined.setVisibility(View.INVISIBLE);

            googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        }else{
            layoutLogined.setVisibility(View.INVISIBLE);
            layoutNotLogined.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageLogo.animate().translationY(-1*imageLogo.getHeight()/5).setDuration(1000).start();
                    layoutNotLoginedButton.animate().translationY(layoutNotLoginedButton.getHeight()*4/5).alpha(1f).setDuration(1000).start();
                }
            }, 1500);

            View.OnClickListener btnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId()){
                        case R.id.btn_splash_login_facebook:
                            break;
                        case R.id.btn_splash_login_google:
                            googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
                            break;
                    }
                }
            };
            btnLoginFacebook.setOnClickListener(btnClickListener);
            btnLoginGoogle.setOnClickListener(btnClickListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null) {
                    firebaseAuthWithGoogle(account);
                }
            }
            catch (Exception e){
                Log.e("ERROR", e.toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    layoutLogined.setVisibility(View.VISIBLE);
                    layoutNotLogined.setVisibility(View.INVISIBLE);

                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userName = firebaseUser.getDisplayName();
                    ed.putBoolean("isLogined", true);
                    ed.apply();

                    Toast.makeText(getApplicationContext(), "로그인애 성공하였습니다.\n" + userName + "님, 환영합니다!", Toast.LENGTH_LONG).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 1500);
                }else{
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}