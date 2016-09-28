package com.fsa.en.dron.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.fsa.en.dron.R;
import com.fsa.en.dron.adapter.GalleryAdapter;
import com.fsa.en.dron.app.AppController;
import com.fsa.en.dron.model.Image;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private static final String endpoint = "https://api.flickr.com/services/rest/?method=flickr.people.getPhotos&api_key=b5c03d489108e01418d256c898bca5b0&user_id=123786701@N07&format=json&nojsoncallback=1";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar.setBarBackgroundColor(R.color.material_light_blue_800);
        bottomNavigationBar.setActiveColor(R.color.material_grey_900);
        bottomNavigationBar.setInActiveColor(R.color.material_blue_grey_200);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.compose, "Escribime"))
                .addItem(new BottomNavigationItem(R.drawable.like, "Facebook"))
                .addItem(new BottomNavigationItem(R.drawable.share, "Cuéntale a un amigo"))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:

                        break;
                    case 1:
                        Intent intent = new Intent(getApplication(), FacebookActivity.class);
                        startActivity(intent);
                        break;
                    case 2:

                        break;

                }
            }

            @Override
            public void onTabUnselected(int position) {

            }
            @Override
            public void onTabReselected(int position) {
                switch (position) {
                    case 0:

                        break;
                    case 1:
                        Intent intent = new Intent(getApplication(), FacebookActivity.class);
                        startActivity(intent);
                        break;
                    case 2:

                        break;

                }
            }
        });
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        fetchImages();
    }

    private void fetchImages() {

        pDialog.setMessage("Levantando vuelo...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,endpoint,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                        JSONArray array = null;

                        try {
                            JSONObject user = response.getJSONObject("photos");
                            array = user.getJSONArray("photo");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        images.clear();
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject object = array.getJSONObject(i);
                                Image image = new Image();
                                //image.setName(object.getString("name"));
                                Log.i("objec", "ob" + object.getString("id"));

                                // JSONObject url = object.getJSONObject("url");
                                image.setSmall("https://farm2.staticflickr.com/"+object.getString("server")+"/"+object.getString("id")+"_"+object.getString("secret")+".jpg");
                                image.setMedium("https://farm2.staticflickr.com/" + object.getString("server") + "/" + object.getString("id") + "_" + object.getString("secret") + ".jpg");
                                image.setLarge("https://farm2.staticflickr.com/" + object.getString("server") + "/" + object.getString("id") + "_" + object.getString("secret") + ".jpg");
                                image.setUrl("https://farm2.staticflickr.com/"+object.getString("server")+"/"+object.getString("id")+"_"+object.getString("secret")+".jpg");

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }
}