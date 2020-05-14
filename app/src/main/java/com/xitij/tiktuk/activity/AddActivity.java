package com.xitij.tiktuk.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xitij.tiktuk.API.ApiClient;
import com.xitij.tiktuk.API.ApiInterface;
import com.xitij.tiktuk.API.ImageProgressRequestBody;
import com.xitij.tiktuk.API.ImageUploadListener;
import com.xitij.tiktuk.API.ProgressRequestBody;
import com.xitij.tiktuk.API.UploadListener;
import com.xitij.tiktuk.Fragment.LogInBSFragment;
import com.xitij.tiktuk.POJO.Auth;
import com.xitij.tiktuk.POJO.Upload.Multipart.UploadVideoResponse;
import com.xitij.tiktuk.POJO.Upload.Video.AddVideoRequest;
import com.xitij.tiktuk.POJO.Upload.Video.AddVideoResponse;
import com.xitij.tiktuk.POJO.Upload.Video.Data;
import com.xitij.tiktuk.POJO.Upload.Video.Request;
import com.xitij.tiktuk.R;
import com.xitij.tiktuk.Utils.PathFromUri;
import com.xitij.tiktuk.Utils.PrefManager;
import com.xitij.tiktuk.VideoCompress.CompressListener;
import com.xitij.tiktuk.VideoCompress.Compressor;
import com.xitij.tiktuk.VideoCompress.InitListener;
import com.xitij.tiktuk.VideoCompressor.VideoCompress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity implements UploadListener, ImageUploadListener {
    private static final int LOG_IN = 819;
    private static final int SELECT_VIDEO = 300;
    private static final int VIDEO_CAPTURE = 400;
    private static final int PAYMENT_REQUEST = 232;

    private static final String TAG = AddActivity.class.getSimpleName();
    private ImageView ivVideoThumnb;
    private ProgressBar progressBar,iprogressBar;
    private Button btnUpload,btnProccedPayment;
    private TextView tvProgress,itvProgress;
    private String videoPath,imagePath;
    private MenuItem menuClear;

    private FrameLayout flAdViews;

    private Compressor mCompressor;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);

        flAdViews = findViewById(R.id.fl_native_banner_ads);
        flAdViews.setVisibility(View.VISIBLE);

        //todo undo for ads
        //loadAds();
        interstitialAd = new InterstitialAd(this,getString(R.string.facebook_ads_interestial));
        interstitialAd.loadAd();

        LinearLayout layout = findViewById(R.id.rlProgress);
        layout.setVisibility(View.GONE);
        LinearLayout linearLayout = findViewById(R.id.linlay_video_upload);
        linearLayout.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.video_circularProgressbar);
        progressBar.setProgress(0);   // Main Progress
        progressBar.setSecondaryProgress(100); // Secondary Progress
        progressBar.setMax(100); // Maximum Progress

        iprogressBar = findViewById(R.id.image_circularProgressbar);
        iprogressBar.setProgress(0);   // Main Progress
        iprogressBar.setSecondaryProgress(100); // Secondary Progress
        iprogressBar.setMax(100); // Maximum Progress

        tvProgress = findViewById(R.id.tv_video_progress);
        tvProgress.setText("0 %");

        itvProgress = findViewById(R.id.tv_image_progress);
        itvProgress.setText("0 %");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        }

        ivVideoThumnb = findViewById(R.id.iv_upload_video);
        btnUpload = findViewById(R.id.btn_video_upload);

        btnProccedPayment = findViewById(R.id.btn_procced_payment);
        btnProccedPayment.setVisibility(View.GONE);

        ivVideoThumnb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (isUserLoggedIn()) {
                    //change 30may
                    //showUploadChooser();

                    Intent intent = new Intent(AddActivity.this,VideoRecorderActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

                }
                else {
                    showLogInFragment();
                }*/
                if (isUserLoggedIn()) {
                    //change 30may
                    showUploadChooser();



                }
                else {
                    showLogInFragment();
                }


            }
        });


        loadCompressor();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideoAPI(videoPath,imagePath);
                LinearLayout layout = findViewById(R.id.rlProgress);
                layout.setVisibility(View.VISIBLE);
                LinearLayout linearLayout = findViewById(R.id.linlay_video_upload);
                linearLayout.setVisibility(View.GONE);
            }
        });


        /**
         * Preload payment resources
         */
        Checkout.preload(getApplicationContext());

    }

    private void loadAds() {
        AdView adViewTop = new AdView(this, getString(R.string.facebook_ads_banner), AdSize.BANNER_HEIGHT_90);
        //AdSettings.addTestDevice("32a00db4-6a1b-4782-86f4-d041098b4316");
        FrameLayout.LayoutParams paramTop = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramTop.gravity = Gravity.TOP;
        //adViewTop.setLayoutParams(paramTop);
        //todo undo for ads
        //adViewTop.loadAd();
        //flAdViews.addView(adViewTop,paramTop);
        AdView adViewBottom = new AdView(this, getString(R.string.facebook_ads_banner), AdSize.BANNER_HEIGHT_90);
        //AdSettings.addTestDevice("32a00db4-6a1b-4782-86f4-d041098b4316");
        FrameLayout.LayoutParams paramBottom = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramBottom.gravity = Gravity.BOTTOM;
        //adViewBottom.setLayoutParams(paramBottom);
        //todo undo for ads
        //adViewBottom.loadAd();
        //flAdViews.addView(adViewBottom,paramBottom);
    }

    private void loadCompressor() {
        mCompressor = new Compressor(this);
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
                Log.v(TAG, "onLoadSuccess: success");
            }

            @Override
            public void onLoadFail(String reason) {
                Log.e(TAG, "onLoadFail() returned: " + reason);
            }
        });
    }

    private boolean isUserLoggedIn() {
        boolean logIn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isLoggedIn",false);

        return logIn;
    }

    private void showLogInFragment() {
        LogInBSFragment fragment = new LogInBSFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isProfileActivity",false);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    private void showUploadChooser() {
        //todo Compress video before uploading!
        //todo compressed video must play
        final BottomSheetDialog dialog = new BottomSheetDialog(AddActivity.this);

        View sheetView = getLayoutInflater().inflate(R.layout.layout_bd_add,null);

        LinearLayout camera = (LinearLayout) sheetView.findViewById(R.id.bottom_sheet_dialog_camera);
        LinearLayout gallary = (LinearLayout) sheetView.findViewById(R.id.bottom_sheet_dialog_gallary);

        gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select a Video "), SELECT_VIDEO);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(AddActivity.this,VideoRecorderActivity.class);
                startActivityForResult(intent,250);
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

                /*dialog.dismiss();
                ///File mediaFile = null;
                //mediaFile = new File(AddActivity.this.getFilesDir(),"/VID"+System.currentTimeMillis());

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                //fileUri = Uri.fromFile(mediaFile);

                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,180);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, VIDEO_CAPTURE);*/
            }
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 250) {

                Log.i(TAG, "onActivityResult: test activty result"+data.getStringExtra("video_path"));

                this.videoPath = data.getStringExtra("video_path");
                File thumbfile = new File(getExternalFilesDir(null),"temp.jpeg");
                try {
                    FileOutputStream stream = new FileOutputStream(thumbfile);

                    Bitmap bmThumbnail;
                    bmThumbnail = ThumbnailUtils.createVideoThumbnail(this.videoPath,
                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

                    if (bmThumbnail != null) {
                        bmThumbnail.compress(Bitmap.CompressFormat.JPEG,100,stream);

                        imagePath = thumbfile.getPath();
                    } else {
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                ivVideoThumnb.setVisibility(View.VISIBLE);
                ivVideoThumnb.setClickable(false);
                menuClear.setVisible(true);
                Picasso.get().load("file://"+imagePath).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivVideoThumnb);

                btnUpload.setVisibility(View.VISIBLE);
                flAdViews.setVisibility(View.GONE);

            }
            if (requestCode == SELECT_VIDEO) {
                Uri videoUri = data.getData();
                this.videoPath = PathFromUri.getRealPath(this,videoUri);
                Log.i(TAG, "onActivityResult: Videouri: "+videoUri.toString());
                Log.e(TAG, "onActivityResult: code req res "+ requestCode + resultCode );
                Log.i(TAG, "onActivityResult: "+ videoPath);

                Log.i(TAG, "onActivityResult: Size "+getFileSize(videoUri) + " Bytes");

                /*if (Build.VERSION.SDK_INT <= 26) {
                    this.videoPath = getPath(videoUri);
                    Log.i(TAG, "onActivityResult: VideoPath"+videoPath);
                }
                else {
                    File file = new File(videoUri.getPath());//create path from uri
                    this.videoPath = file.getAbsolutePath();
                    Log.i(TAG, "onActivityResult: VideoPath"+videoPath);
                }*/


                File thumbfile = new File(getExternalFilesDir(null),"temp.jpeg");
                try {
                    FileOutputStream stream = new FileOutputStream(thumbfile);
                    Bitmap bitmap = PathFromUri.createVideoThumbnail(this,videoUri);
                    if (bitmap!=null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

                        imagePath = thumbfile.getPath();
                    }
                    Log.e(TAG, "onActivityResult: Image Path "+ imagePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (this.imagePath.isEmpty()) {
                    this.imagePath = getThumbnailPathForLocalFile(videoUri);
                }

                Log.i(TAG, "onActivityResult: thumbPsth: "+imagePath);

                ivVideoThumnb.setVisibility(View.VISIBLE);
                ivVideoThumnb.setClickable(false);
                if (menuClear != null) {
                    menuClear.setVisible(true);
                }

                Picasso.get().load("file://"+imagePath).memoryPolicy(MemoryPolicy.NO_CACHE).into(ivVideoThumnb);

                /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(this, videoUri);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                String vw = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String vh = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String r = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
                int width = (int) (Double.parseDouble(vw)/2);
                int height = (int) (Double.parseDouble(vh)/2);
                int rotation = (int) (Double.parseDouble(r));
                if (rotation==0||rotation==180) {

                }
                Log.i(TAG, "onActivityResult: "+width+" "+height+ " rotation"+rotation);
                long timeInMillisec = Long.parseLong(time );

                retriever.release();*/

                btnUpload.setVisibility(View.VISIBLE);

                //fixme if you want to compress
                long size = Long.parseLong(getFileSize(videoUri));

                if (size>6000000) {
                    File file = new File(getExternalFilesDir(null),"temp.mp4");
                    String currentOutputVideoPath = file.getPath();
                    //todo uncomment this!
                    btnUpload.setVisibility(View.VISIBLE);
                    //showLargeFileDialog(this.videoPath,currentOutputVideoPath);
                    if (size>15000000) {
                        showLargeFileDialog(this.videoPath,currentOutputVideoPath,false);
                    } else {
                        showLargeFileDialog(this.videoPath,currentOutputVideoPath,true);
                    }
                }
                else {
                    btnUpload.setVisibility(View.VISIBLE);
                }

                /*File file = new File(getExternalFilesDir(null),"temp.mp4");
                String currentOutputVideoPath = file.getPath();
                btnUpload.setVisibility(View.VISIBLE);
                compress(videoPath,currentOutputVideoPath);
                //showLargeFileDialog(this.videoPath,currentOutputVideoPath);*/

                /*Button btnCompress = findViewById(R.id.btn_video_compress);
                btnCompress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //String cmd = "-y -i " + currentInputVideoPath + " -preset ultrafast -b 600k " + currentOutputVideoPath; almost 4 min
                        //String cmd = "-y -i " + currentInputVideoPath + " -c:v libx264 -crf 40 -preset ultrafast " + currentOutputVideoPath;

                        //execCommand(currentOutputVideoPath,cmd);

                        //compress(videoPath,currentOutputVideoPath);
                    }
                });*/

                //Hide ads
                flAdViews.setVisibility(View.GONE);

            }

            if (requestCode == PAYMENT_REQUEST) {
                if (data != null) {
                    if (data.getBooleanExtra("isSuccess",true)) {
                        showUploadVideoSuccess();
                    } else {
                        showProccedPayment();
                    }
                } else {
                    showUploadVideoSuccess();
                }
            }

        }
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                Log.i(TAG, "onActivityResult: Videouri: "+videoUri.toString());
                Log.e(TAG, "onActivityResult: code req res "+ requestCode + resultCode );
                Log.i(TAG, "onActivityResult: "+getPath(videoUri));
                Log.i(TAG, "onActivityResult: "+getFileSize(videoUri));

                ivVideoThumnb.setVisibility(View.VISIBLE);
                Picasso.get().load("file://"+imagePath).into(ivVideoThumnb);

                long size = Long.parseLong(getFileSize(videoUri));

                this.videoPath = getPath(videoUri);

                File thumbfile = new File(getExternalFilesDir(null),"temp.jpeg");
                try {
                    FileOutputStream stream = new FileOutputStream(thumbfile);
                    Bitmap bitmap = PathFromUri.createVideoThumbnail(this,videoUri);
                    if (bitmap!=null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

                        imagePath = thumbfile.getPath();
                    }
                    Log.e(TAG, "onActivityResult: Image Path "+ imagePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (this.imagePath.isEmpty()) {
                    this.imagePath = getThumbnailPathForLocalFile(videoUri);
                }

                if (size>6000000) {
                    File file = new File(getExternalFilesDir(null),"temp.mp4");
                    String currentOutputVideoPath = file.getPath();
                    if (size>15000000) {
                        showLargeFileDialog(this.videoPath,currentOutputVideoPath,false);
                    } else {
                        showLargeFileDialog(this.videoPath,currentOutputVideoPath,true);
                    }
                }
                else {
                    btnUpload.setVisibility(View.VISIBLE);
                }

                /*File file = new File(getExternalFilesDir(null),"temp.mp4");
                String currentOutputVideoPath = file.getPath();
                compress(videoPath,currentOutputVideoPath);
                //showLargeFileDialog(this.videoPath,currentOutputVideoPath);*/

                //Hide ads
                flAdViews.setVisibility(View.GONE);

            }
        }
        /*if (requestCode == LOG_IN) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    boolean isLogInSuccessful = data.getBooleanExtra("isLogInSuccessful",false);
                }
            }
        }*/
    }

    private String getFileSize(Uri videoUri) {
        Cursor cursor = null;

        try{
            String[] column = { MediaStore.Video.Media.SIZE };

            cursor = getContentResolver().query(videoUri, column, null, null, null);

            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void compress(final String iPath, String oPath, boolean isHigh) {
        AddActivity.this.videoPath = oPath;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.layout_alert_compressing,null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);

        final TextView tvProgress = view.findViewById(R.id.tv_compress_progress);

        VideoCompress.compressVideoLow(iPath, oPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: Started");
                btnUpload.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "onSuccess: ");
                File file = new File(videoPath);
                long length = file.length();
                if (length>600000000) {
                    ivVideoThumnb.setImageDrawable(getResources().getDrawable(R.drawable.ic_upload_video));
                    ivVideoThumnb.setClickable(true);
                    btnUpload.setVisibility(View.GONE);
                    imagePath = "";
                    //Show ads
                    flAdViews.setVisibility(View.VISIBLE);
                    Toast.makeText(AddActivity.this, "Compressed file is still large. Please choose another one!", Toast.LENGTH_LONG).show();
                }
                else {
                    execCommand(videoPath,dialog);
                    btnUpload.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFail() {
                btnUpload.setVisibility(View.GONE);
                dialog.dismiss();
                Toast.makeText(AddActivity.this, "Compressing failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(float percent) {
                String progress = Math.round(percent)+" %";
                tvProgress.setText(progress);
            }
        });

    }

    private void execCommand(String iPath, final AlertDialog dialog/*final String path, String cmd*/) {

        File file = new File(getExternalFilesDir(null),"streamable.mp4");
        String oPath = file.getPath();

        mCompressor = new Compressor(this);
        //" -vf scale="+width+":"+height+
        //String cmd = "-threads 4 -y -i " + iPath + " -strict -2 -vcodec libx264 -preset ultrafast -crf 28 -acodec copy -ac 2 " + oPath;
        //String cmd = "-y -i " + iPath + " -c:v libx264 -crf 40 -preset ultrafast " + oPath;
        //String cmd = "-y -i " + iPath + " -strict experimental -s 405x720 -b 500k -r 25 -vcodec mpeg4 -ab 48000 -ac 2 -ar 22050 " + oPath;
        //String cmd = "-threads 8 -y -i "+ iPath + "-c:v libx264 -bufsize 3968k -ac 1 -g 60 -c:a aac -b:a 128k -level 3.0 -crf 28 -preset ultrafast -strict -2 "+oPath;
        String cmd = "-y -i " + iPath +" -c copy -map 0 -movflags faststart "+oPath;
        this.videoPath = oPath;
        mCompressor.execCommand(cmd, new CompressListener() {
            @Override
            public void onExecSuccess(String message) {
                btnUpload.setVisibility(View.VISIBLE);
                Log.d(TAG, "onExecSuccess() returned: " + message);
            }

            @Override
            public void onExecFail(String reason) {
                btnUpload.setVisibility(View.GONE);
                Toast.makeText(AddActivity.this, "Compressing failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExecProgress(String message) {
                Log.i(TAG, "onExecProgress: "+message);
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                btnUpload.setVisibility(View.VISIBLE);
            }
        });

        /*mCompressor.execCommand(cmd, new CompressListener() {
            @Override
            public void onExecSuccess(String message) {
                Log.d(TAG, "onExecSuccess() returned: " + message);
                String result = "Before: "+getFileSize(path);
                Log.d(TAG, "onExecSuccess() returned: " + result);
            }

            @Override
            public void onExecFail(String reason) {
                Log.d(TAG, "onExecFail() returned: " + reason);
            }

            @Override
            public void onExecProgress(String message) {
                Log.d(TAG, "onExecProgress() returned: " + message);
            }
        });*/
    }

    private void uploadVideoAPI(String videoPath, String imagePath) {

        File videoFile = new File(videoPath);
        File imageFile = new File(imagePath);

        //todo remove Auth from here! :(

        menuClear.setVisible(false);



        ProgressRequestBody requestBody = new ProgressRequestBody(videoFile,this);
        ImageProgressRequestBody imageRequestBody = new ImageProgressRequestBody(imageFile,this);

        MultipartBody.Part videoBody = MultipartBody.Part.createFormData("video",videoFile.getName(),requestBody);
        MultipartBody.Part imageBody = MultipartBody.Part.createFormData("image",imageFile.getName(),imageRequestBody);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<UploadVideoResponse> responseCall = apiInterface.uploadVideo(videoBody,imageBody);
        responseCall.enqueue(new Callback<UploadVideoResponse>() {
            @Override
            public void onResponse(Call<UploadVideoResponse> call, Response<UploadVideoResponse> response) {
                Log.d(TAG, "onResponse() returned: " + response.code());
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        Log.i(TAG, "onResponse: success");
                        if (PrefManager.isPaidContest(AddActivity.this)) {

                            PrefManager.setTempVUrl(AddActivity.this,response.body().getData());
                            PrefManager.setTempTUrl(AddActivity.this,response.body().getThumbData());

                            Gson gson = new Gson();

                            String data = gson.toJson(response.body());

                            btnProccedPayment.setTag(data);
                            showUploadVideoSuccess();
//                            Intent intent = new Intent(AddActivity.this,TestActivity.class);
//                            intent.putExtra("data",data);
//                            startActivityForResult(intent,PAYMENT_REQUEST);
                        } else {
                            Toast.makeText(AddActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            String contestId = PreferenceManager.getDefaultSharedPreferences(AddActivity.this).getString("contest_id","1");
                            addVideoAPI(response.body().getData(),response.body().getThumbData(),contestId);
                        }
                    }
                    else {
                        Log.i(TAG, "onResponse: success 0 "+response.body().getMessage());
                    }
                }
                else {
                    Log.i(TAG, "onResponse: not succes");
                }
            }

            @Override
            public void onFailure(Call<UploadVideoResponse> call, Throwable t) {
                Log.i(TAG, "onFailure: failed");
                Toast.makeText(AddActivity.this, "Error"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addVideoAPI(String video_url, String thumb_url, String contestId) {

        AddVideoRequest videoRequest = new AddVideoRequest();

        Request request = new Request();

        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("id",-111);
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token","null");

        if (id == -111 || token.equals("null")) {
            Log.i(TAG, "addVideoAPI: returned");
            return;
        }

        Auth auth = new Auth();
        auth.setId(id);
        auth.setToken(token);

        Data data = new Data();
        data.setVideoUrl(video_url);
        data.setThumbUrl(thumb_url);
        data.setContId(contestId);
        data.setUserId(""+id);


        request.setData(data);

        videoRequest.setAuth(auth);
        videoRequest.setRequest(request);
        videoRequest.setService("addvideo");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<AddVideoResponse> responseCall = apiInterface.addVideo(videoRequest);
        responseCall.enqueue(new Callback<AddVideoResponse>() {
            @Override
            public void onResponse(Call<AddVideoResponse> call, Response<AddVideoResponse> response) {
                Log.i(TAG, "onResponse: AddVideo :"+response.code());
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        Log.d(TAG, "onResponse() returned: " + response.body().getMessage());

                        showUploadVideoSuccess();
                    }
                }
                else {
                    Toast.makeText(AddActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse: "+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<AddVideoResponse> call, Throwable t) {
                Log.i(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

    private String getPath(Uri videoUri) {
        String filePath = "";

        String[] column = { MediaStore.Video.Media.DATA };

        Cursor cursor = getContentResolver().query(videoUri, column, null, null, null);

        assert cursor != null;
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(0);
        }
        cursor.close();
        return filePath;
    }


    private String getThumbnailPathForLocalFile(Uri fileUri) {

        long fileId = getFileId(fileUri);

        MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(),
                fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

        Cursor thumbCursor = null;
        String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA };
        try {

            thumbCursor = getContentResolver().query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);
            String thumbPath = "";
            assert thumbCursor != null;
            if (thumbCursor.moveToFirst()) {
                thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            }


            thumbCursor.close();
            Log.i(TAG, "getThumbnailPathForLocalFile: "+thumbPath);
            return thumbPath;

        } finally {
        }
    }

    private long getFileId(Uri fileUri) {

        String[] mediaColumns = { MediaStore.Video.Media._ID };

        Cursor cursor = getContentResolver().query(fileUri, mediaColumns, null, null,
                null);

        int id = 0;
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            id = cursor.getInt(columnIndex);
        }

        cursor.close();;
        return id;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressBar.setProgress(percentage);
        tvProgress.setText(percentage+" %");
    }

    @Override
    public void onError() {
        Log.e(TAG, "onError: Error uploading Video");
    }

    @Override
    public void onFinish() {
        Log.i(TAG, "onFinish: finished");
    }

    @Override
    public void onUploadStart() {
        Log.e(TAG, "onUploadStart: started");
    }

    @Override
    public void onImageProgressUpdate(int percentage) {
        iprogressBar.setProgress(percentage);
        itvProgress.setText(percentage+" %");
    }

    @Override
    public void onIUploadError() {

    }

    @Override
    public void onIUploadFinish() {

    }

    @Override
    public void onIUploadStart() {

    }

    private String getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return "0 MB";
        } else {
            long size = f.length();
            return (size / 1024f) / 1024f + "MB";
        }
    }

    private void showLargeFileDialog(final String videoPath, final String currentOutputVideoPath, final boolean isHigh) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.layout_alert_compress,null);

        Button btnDismiss = view.findViewById(R.id.btn_compress_dismiss);
        Button btnOk= view.findViewById(R.id.btn_compress_ok);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivVideoThumnb.setImageDrawable(getResources().getDrawable(R.drawable.ic_upload_video));
                ivVideoThumnb.setClickable(true);
                btnUpload.setVisibility(View.GONE);
                imagePath = "";
                //Show ads
                flAdViews.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compress(videoPath,currentOutputVideoPath,isHigh);
                dialog.dismiss();
            }
        });
    }

    private void showProccedPayment() {
        LinearLayout layout = findViewById(R.id.rlProgress);
        layout.setVisibility(View.GONE);

        LinearLayout linearLayout = findViewById(R.id.linlay_video_upload);
        linearLayout.setVisibility(View.GONE);

        LinearLayout uploadDone = findViewById(R.id.linlay_upload_done);
        uploadDone.setVisibility(View.GONE);

        ivVideoThumnb.setVisibility(View.GONE);
        btnUpload.setVisibility(View.GONE);

        btnProccedPayment.setVisibility(View.VISIBLE);
        btnProccedPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = (String) v.getTag();

                Intent intent = new Intent(AddActivity.this,TestActivity.class);
                intent.putExtra("data",data);
                startActivityForResult(intent,PAYMENT_REQUEST);
            }
        });

    }

    private void showUploadVideoSuccess() {
        progressBar.setProgress(100);
        iprogressBar.setProgress(100);
        tvProgress.setText("Added");
        itvProgress.setText("Added");

        if (interstitialAd.isAdLoaded()) {
            interstitialAd.show();
        }
        Toast toast = Toast.makeText(AddActivity.this, "Uploaded Successfully.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                LinearLayout layout = findViewById(R.id.rlProgress);
                layout.setVisibility(View.GONE);

                LinearLayout linearLayout = findViewById(R.id.linlay_video_upload);
                linearLayout.setVisibility(View.GONE);

                LinearLayout uploadDone = findViewById(R.id.linlay_upload_done);
                uploadDone.setVisibility(View.VISIBLE);

                ivVideoThumnb.setVisibility(View.GONE);
                btnUpload.setVisibility(View.INVISIBLE);

                Button btnUploadAnother = findViewById(R.id.btn_upload_another);
                btnUploadAnother.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout linearLayout = findViewById(R.id.linlay_video_upload);
                        linearLayout.setVisibility(View.VISIBLE);

                        LinearLayout uploadDone = findViewById(R.id.linlay_upload_done);
                        uploadDone.setVisibility(View.GONE);
                        imagePath = "";
                        ivVideoThumnb.setVisibility(View.VISIBLE);
                        ivVideoThumnb.setClickable(true);
                        ivVideoThumnb.setImageDrawable(getResources().getDrawable(R.drawable.ic_upload_video));
                    }
                });

            }
        },2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upload_video_menu,menu);
        menuClear = menu.findItem(R.id.video_clear);
        menuClear.setVisible(false);

        return true;

    }

    @Override
    protected void onDestroy() {
        if (flAdViews != null) {
            AdView adViewTop = (AdView) flAdViews.getChildAt(0);
            AdView adViewBottom = (AdView) flAdViews.getChildAt(1);
            if (adViewTop != null) {
                adViewTop.destroy();
            }
            if (adViewBottom != null) {
                adViewBottom.destroy();
            }
        }
        interstitialAd.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.video_clear) {
            ivVideoThumnb.setImageDrawable(getResources().getDrawable(R.drawable.ic_upload_video));
            ivVideoThumnb.setClickable(true);
            btnUpload.setVisibility(View.GONE);
            imagePath = "";
            //Show ads
            flAdViews.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }
}