package com.fsa.en.dron.activity;

/**
 * Created by Marcelo on 23/09/2016.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.fsa.en.dron.R;
import com.fsa.en.dron.model.Image;
import com.github.pierry.simpletoast.SimpleToast;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle;
    private int selectedPosition = 0;
    private BootstrapButton descargar, compartir;
    private NotificationManager mNotifyManager;
    private Builder mBuilder;
    int id = 1;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    String url_descarga = null;
    String id_imagen = null;
    Bitmap image_facebook;
    SimpleArcDialog mDialog;

    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        facebookSDKInitialize();
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        mDialog = new SimpleArcDialog(getActivity());
        compartir = (BootstrapButton) v.findViewById(R.id.compartir);
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAppInstalled()){
                prepareImage();}
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Aplicación de Facebook no encontrada", Toast.LENGTH_SHORT).show();

                }
            }
        });
        descargar = (BootstrapButton) v.findViewById(R.id.descargar);
        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                }
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + id_imagen + ".jpg");
                Intent toLaunch = new Intent();
                toLaunch.setAction(android.content.Intent.ACTION_VIEW);
                toLaunch.setDataAndType(Uri.fromFile(file), MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpeg"));
                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0,
                        toLaunch, 0);
                mNotifyManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(getActivity());
                mBuilder.setContentTitle("Descargando imagen")
                        .setContentText("Toca aquí al finalizar..")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.cloud);


                Picasso.with(getActivity().getApplicationContext()).load(url_descarga).into(target);

            }
        });
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        images = (ArrayList<Image>) getArguments().getSerializable("images");
        shareDialog = new ShareDialog(this);
        selectedPosition = getArguments().getInt("position");
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);
        return v;
    }

    public static final int PERMISSION_REQUEST_CODE = 99;

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(getActivity(), "Necesitas proporcionar permisos para almacenar las imagenes, por favor habilita esta opción en configuraciones de tu telefono", Toast.LENGTH_LONG).show();

            } else {

                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
            return false;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    public boolean isAppInstalled() {
        try {
            getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " de " + images.size());
        Image image = images.get(position);
        lblTitle.setText(image.getName());
        url_descarga = image.getUrl();
        id_imagen = image.getId();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_DeviceDefault_NoActionBar);
    }

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);

            Image image = images.get(position);

            Glide.with(getActivity()).load(image.getLarge())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    private void prepareImage (){
        Picasso.with(getActivity().getApplicationContext()).load(url_descarga).into(target_share);

    }



    private Target target_share = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            mDialog.dismiss();
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            shareDialog.show(content);
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
             int[] data = new int[1];
           data[0] = Color.parseColor("#ff039be5");
            ArcConfiguration configuration = new ArcConfiguration(getActivity());
            configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
            configuration.setText("Preparando imagen..");
            configuration.setColors(data);
            mDialog.setConfiguration(configuration);
            mDialog.show();
           }
    };

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(
                            Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + id_imagen + ".jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                        ostream.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            SimpleToast.ok(getActivity(), "Descarga completa", "{fa-check}");
            mBuilder.setContentText("Descarga completa");
            // Removes the progress bar
            mBuilder.setProgress(0, 0, false);
            mNotifyManager.notify(id, mBuilder.build());
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {mBuilder.setProgress(0, 0, false);
            mNotifyManager.notify(id, mBuilder.build());}
    };

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);}

}