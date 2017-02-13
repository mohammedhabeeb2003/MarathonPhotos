package com.pingala.marathonphotos;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WallpaperActivity extends AppCompatActivity {
    private RotateLoading newtonCradleLoading;
    private static String imageName;
    String imgUrl;
    Bitmap btm;
    static boolean download =false;
    private SubsamplingScaleImageView mPreviewView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        imgUrl = getIntent().getStringExtra("fullImage");
        imageName = getIntent().getStringExtra("imageName")+".jpg";
        mPreviewView = (SubsamplingScaleImageView) findViewById(R.id.imageView);
        newtonCradleLoading = (RotateLoading) findViewById(R.id.rotateloading);
        newtonCradleLoading.setLoadingColor(Color.RED);
        String msg = "Long Press To Download or Set Wallpaper";
        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),msg,4000);
        snack.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snack.dismiss();
            }
        });
        snack.show();
       new DownloadImageTask().execute();



    }
    class DownloadImageTask extends AsyncTask<Void,Void,Void>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            newtonCradleLoading.start();
        }
        @Override
        protected Void doInBackground(Void... params) {
            try  {
                URL url = new URL(getIntent().getStringExtra("fullImage"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                btm = myBitmap;
                SaveImage(btm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!imageName.isEmpty()){
                newtonCradleLoading.stop();
                mPreviewView.setImage(ImageSource.bitmap(btm));
                mPreviewView.setVisibility(View.VISIBLE);
                mPreviewView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Dialog alertDialog = new Dialog(WallpaperActivity.this);
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.setContentView(R.layout.tabs);
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.show();
                        Button btn_setAs= (Button) alertDialog.findViewById(R.id.btn_setAs);
                        Button btn_download= (Button) alertDialog.findViewById(R.id.btn_btn_Download);
                        btn_setAs.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WallpaperManager myWallpaperManager = WallpaperManager
                                        .getInstance(WallpaperActivity.this);

                                try {
                                    // Change the current system wallpaper
                                    myWallpaperManager.setBitmap(btm);

                                    // Show a toast message on successful change
                                    Toast.makeText(WallpaperActivity.this,
                                            "Wallpaper successfully changed", Toast.LENGTH_SHORT)
                                            .show();

                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                }
                                alertDialog.dismiss();
                            }
                        });
                        btn_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                  download = true;
                                Toast.makeText(WallpaperActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                        return false;
                    }
                });

            }
        }


    }
    private static File SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/Boom Shiva");
        myDir.mkdirs();


        File file = new File (myDir,imageName );
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(download==false) {
            SaveImage(btm).delete();
        }
    }

}
