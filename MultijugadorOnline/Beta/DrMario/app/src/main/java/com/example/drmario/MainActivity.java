package com.example.drmario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.drmario.logica.ConexionFireBase;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private Button single, multi;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.single = findViewById(R.id.Single);
        this.single.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ingresarLevels();
               // ingresarJuego();
            }
        });

        this.multi = findViewById(R.id.Multi);
        this.multi.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                ingresarLobby();
            }
        });

        this.callbackManager = CallbackManager.Factory.create();
        this.loginButton = (LoginButton) findViewById(R.id.login_button);
        this.loginButton.setPermissions(Arrays.asList(EMAIL));
        this.loginButton.registerCallback(this.callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handlerFaceBookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        this.mAuth = FirebaseAuth.getInstance();
        ConexionFireBase.getmDatabase().removeValue();
    }

    private void ingresarLobby() {
        Intent intent = new Intent(this, Lobby.class);
        startActivity(intent);
    }

    private void ingresarJuego() {
        Intent intent = new Intent(this, Juego.class);
        startActivity(intent);
    }

    private void ingresarLevels() {
        Intent intent = new Intent(this, Level.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handlerFaceBookAccessToken(AccessToken accessToken) {
        //Log.d(TAG, "handleFacebookAccessToken:" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        this.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                            //Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}