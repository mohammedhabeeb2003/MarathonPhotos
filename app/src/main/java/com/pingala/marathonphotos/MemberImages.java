package com.pingala.marathonphotos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberImages extends AppCompatActivity {

  /*  GridView mGridView;*/
    FirebaseDatabase database;
    DatabaseReference mRef;
    String memberImagesLink;
    StaggeredGridLayoutAdapter adapter;
    StaggeredGridLayoutManager mLayoutManager;
    private RecyclerView recyclerViewMain;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_images);
       memberImagesLink = getIntent().getStringExtra("ImagesLink");
        recyclerViewMain = (RecyclerView) findViewById(R.id.my_recycler_view);
        Log.e("ImageLink",""+memberImagesLink);
        if (checkAndRequestPermissions()) {

            new ImageTask().execute();
        }



    }

    class ImageTask extends AsyncTask<Void,Void,Void>{
        ArrayList<Images> mImageList;
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pd = new ProgressDialog(MemberImages.this);
            pd.setMessage("Please Wait....");
            pd.show();
*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            database = FirebaseDatabase.getInstance();
            mRef = database.getReferenceFromUrl(getIntent().getStringExtra("ImagesLink"));
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mImageList = new ArrayList();
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String thumbNail = data.child("thumbNail").getValue(String.class);
                        String imgUrl = data.child("url").getValue(String.class);
                        Log.e("FullImage",""+imgUrl);
                        Images im = new Images();
                        im.setImg(thumbNail);
                        im.setImgUrl(imgUrl);
                        mImageList.add(im);
                    }
                    recyclerViewMain.setHasFixedSize(true);
                    mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
                    recyclerViewMain.setLayoutManager(mLayoutManager);
                    adapter = new StaggeredGridLayoutAdapter(MemberImages.this, mImageList);
                    recyclerViewMain.setAdapter(adapter);

                    Log.e("List",""+mImageList.size()+mImageList.get(0).getImgUrl());
                    /*pd.dismiss();*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    private boolean checkAndRequestPermissions() {
        int gpspermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this, android.Manifest.permission.SET_WALLPAPER);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (gpspermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SET_WALLPAPER);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.SET_WALLPAPER, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && perms.get(android.Manifest.permission.SET_WALLPAPER) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        restartActivity();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SET_WALLPAPER)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");

                        }
                    }
                }
            }
        }

    }
    //If Permission Denied this AlertDialog will Open
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg) {
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }
    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }

}
