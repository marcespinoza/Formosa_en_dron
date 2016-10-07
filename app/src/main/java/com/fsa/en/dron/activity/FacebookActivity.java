package com.fsa.en.dron.activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.facebook.share.widget.LikeView;
import com.fsa.en.dron.R;

import org.json.JSONObject;

import java.util.List;

import in.championswimmer.libsocialbuttons.buttons.BtnFacebook;
import in.championswimmer.libsocialbuttons.buttons.BtnInstagram;
import in.championswimmer.libsocialbuttons.buttons.BtnYoutube;
import in.championswimmer.libsocialbuttons.fabs.FABFacebook;
import in.championswimmer.libsocialbuttons.fabs.FABInstagram;
import in.championswimmer.libsocialbuttons.fabs.FABYoutube;

/**
 * Created by Marcelo on 26/09/2016.
 */
public class FacebookActivity extends AppCompatActivity {

    private LoginButton loginButton;
    public TextView nombre_perfil, texto2, texto1;
    private CallbackManager callbackManager;
    SharedPreferences sharedPref;
    AccessToken accessToken;
    FABFacebook facebook_button;
    FABInstagram instagram_button;
    FABYoutube youtube_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        accessToken = AccessToken.getCurrentAccessToken();
        setContentView(R.layout.facebook_activity);
        Typeface asenine = Typeface.createFromAsset(getAssets(), "fonts/asenine.ttf");
        Typeface copyviol = Typeface.createFromAsset(getAssets(), "fonts/Copyviol.ttf");
        LikeView likeView = (LikeView) findViewById(R.id.like_view);
        likeView.setObjectIdAndType( "https://www.facebook.com/formosaendron",
                LikeView.ObjectType.PAGE);
        sharedPref = getSharedPreferences("Facebook", Context.MODE_PRIVATE);
        facebook_button = (FABFacebook) findViewById(R.id.facebook_button);
        facebook_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookUrl = "https://www.facebook.com/formosaendron";
                try {
                    int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850) {
                        Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));;
                    } else {
                        // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/https://www.facebook.com/Formosaendron")));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // Facebook is not installed. Open the browser
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
                }
            }
        });
        instagram_button = (FABInstagram) findViewById(R.id.instagram_button);
        instagram_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scheme = "http://instagram.com/_u/formosa_en_dron";
                String path = "https://instagram.com/formosa_en_dron";
                String nomPackageInfo ="com.instagram.android";
                Intent intentAiguilleur;
                try {
                    getApplication().getPackageManager().getPackageInfo(nomPackageInfo, 0);
                    intentAiguilleur = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
                } catch (Exception e) {
                    intentAiguilleur = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                }
                startActivity(intentAiguilleur);
            }
        });
        youtube_button = (FABYoutube) findViewById(R.id.youtube_button);
        youtube_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                try {
                    intent =new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.google.android.youtube");
                    intent.setData(Uri.parse("https://www.youtube.com/channel/UC6LHlW46rDY2HaIXwWRqcHw"));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://www.youtube.com/channel/UC6LHlW46rDY2HaIXwWRqcHw"));
                    startActivity(intent);
                }
            }
        });
        texto1 = (TextView) findViewById(R.id.texto1);
        texto1.setTypeface(asenine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.toolbar_title, null);
        Typeface budget = Typeface.createFromAsset(getAssets(), "fonts/Budget.otf");
        Typeface typographica = Typeface.createFromAsset(getAssets(), "fonts/TypoGraphica.otf");
        TextView mToolbarCustomTitle = (TextView) v.findViewById(R.id.title);
        TextView mToolbarCustomSubTitle = (TextView) v.findViewById(R.id.subtitle);
        mToolbarCustomTitle.setText("Formosa");
        mToolbarCustomSubTitle.setText("en dron");
        mToolbarCustomTitle.setTypeface(typographica);
        mToolbarCustomSubTitle.setTypeface(budget);
        getSupportActionBar().setCustomView(v);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }


    public String getFacebookPageURL(Context context) {
        String url = "https://www.facebook.com/Formosaendron";
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;

            boolean activated =  packageManager.getApplicationInfo("com.facebook.katana", 0).enabled;
            if(activated){
                if ((versionCode >= 3002850)) {
                    return "fb://facewebmodal/f?href=" + "https://www.facebook.com/Formosaendron";
                } else {
                    return "fb://page/" + "976479322394792";
                }
            }else{
                return url;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return url;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("resuu",""+requestCode+resultCode+data);
        if(resultCode!=0){
        callbackManager.onActivityResult(requestCode, resultCode, data);}
    }

}