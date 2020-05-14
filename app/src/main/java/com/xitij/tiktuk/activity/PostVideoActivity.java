package com.xitij.tiktuk.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.Variables;

public class PostVideoActivity extends AppCompatActivity {
    ImageView video_thumbnail;

    String video_path;

    ProgressDialog progressDialog;

    //ServiceCallback serviceCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        video_path = Variables.output_filter_file;
        video_thumbnail = findViewById(R.id.video_thumbnail);


        // this will get the thumbnail of video and show them in imageview
        Bitmap bmThumbnail;
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(video_path,
                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        if (bmThumbnail != null) {
            video_thumbnail.setImageBitmap(bmThumbnail);
        } else {
        }




        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);




        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                onBackPressed();
            }
        });


        findViewById(R.id.post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){

                //progressDialog.show();
                //Start_Service();
                Intent intent = new Intent();
                intent.putExtra("video_path",video_path);
                setResult(RESULT_OK,intent);
                finish();

            }
        });



    }



    // this will start the service for uploading the video into database
    public void Start_Service(){

        /*serviceCallback=this;

        Upload_Service mService = new Upload_Service(serviceCallback);
        if (!Functions.isMyServiceRunning(this,mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("startservice");
            mServiceIntent.putExtra("uri",""+ Uri.fromFile(new File(video_path)));
            startService(mServiceIntent);


            Intent intent = new Intent(this, Upload_Service.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }
        else {
            Toast.makeText(this, "Please wait video already in uploading progress", Toast.LENGTH_LONG).show();
        }*/


    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }




    // when the video is uploading successfully it will restart the appliaction
    /*@Override
    public void ShowResponce(final String responce) {

        Toast.makeText(Post_Video_A.this, responce, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));
        finishAffinity();

    }*/


    // this is importance for binding the service to the activity
    /*Upload_Service mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Upload_Service.LocalBinder binder = (Upload_Service.LocalBinder) service;
            mService = binder.getService();

            mService.setCallbacks(Post_Video_A.this);



        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };*/


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Stop_Service();
    }


    // this function will stop the the ruuning service
    public void Stop_Service(){

        /*serviceCallback=this;

        Upload_Service mService = new Upload_Service(serviceCallback);
        if (Functions.isMyServiceRunning(this,mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("stopservice");
            startService(mServiceIntent);

        }*/


    }



}
