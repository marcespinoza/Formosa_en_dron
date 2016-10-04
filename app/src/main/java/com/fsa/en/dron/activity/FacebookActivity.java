package com.fsa.en.dron.activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.fsa.en.dron.R;

import org.json.JSONObject;

/**
 * Created by Marcelo on 26/09/2016.
 */
public class FacebookActivity extends AppCompatActivity {

    private LoginButton loginButton;
    public TextView nombre_perfil;
    private CallbackManager callbackManager;
    SharedPreferences sharedPref;
    AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessToken = AccessToken.getCurrentAccessToken();
        setContentView(R.layout.facebook_activity);
        sharedPref = getSharedPreferences("Facebook",Context.MODE_PRIVATE);

        loginButton = (LoginButton)findViewById(R.id.login_button);
        nombre_perfil = (TextView) findViewById(R.id.nombre_perfil);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (response.getError() != null) {
                            Log.e("datos", "Error in Response " + response);
                        } else {
                            String name = object.optString("name");
                            Log.e("datos", "Json Object Data " + object + " Email id " + name);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("name", name);
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), FacebookActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                // Picasso.with(getApplicationContext()).load("https://graph.facebook.com/10208510106537493/picture?type=normal").into(target);
                Bundle bundle = new Bundle();
                bundle.putString("fields", "id,name,picture.width(120).height(120)");
                graphRequest.setParameters(bundle);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
        loginButton.setReadPermissions("public_profile");
        checkToken();
    }
    private void checkToken(){
    if (AccessToken.getCurrentAccessToken() == null) {
        nombre_perfil.setText("Logueate con tu cuenta");
    } else {
        nombre_perfil.setText(sharedPref.getString("name", "Sin datos"));
    }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
