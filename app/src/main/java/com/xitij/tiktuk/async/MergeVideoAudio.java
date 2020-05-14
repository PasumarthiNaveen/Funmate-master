package com.xitij.tiktuk.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.authoring.tracks.MP3TrackImpl;
import com.xitij.tiktuk.Utils.Variables;
import com.xitij.tiktuk.activity.PreviewVideoActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoAudio extends AsyncTask<String,String,String> {

    private ProgressDialog progressDialog;
    private Context context;

    private String audio,video,output;

    public MergeVideoAudio(Context context){
        this.context=context;
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    public String doInBackground(String... strings) {
        progressDialog.show();

        audio=strings[0];
        video=strings[1];
        output=strings[2];

        Log.d("resp",audio+"----"+video+"-----"+output);

        Thread thread = new Thread(runnable);
        thread.start();

        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }


    public void Go_To_preview_Activity(){
        Intent intent =new Intent(context,PreviewVideoActivity.class);
        intent.putExtra("path", Variables.root + "/output2.mp4");
        ((Activity)context).startActivityForResult(intent,260);
        //context.startActivity(intent);
    }



    public Track CropAudio(String videopath, Track fullAudio){
        try {

            IsoFile isoFile = new IsoFile(videopath);

            double lengthInSeconds = (double)
                    isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                    isoFile.getMovieBox().getMovieHeaderBox().getTimescale();


            Track audioTrack = (Track) fullAudio;


            double startTime1 = 0;
            double endTime1 = lengthInSeconds;


            long currentSample = 0;
            double currentTime = 0;
            double lastTime = -1;
            long startSample1 = -1;
            long endSample1 = -1;


            for (int i = 0; i < audioTrack.getSampleDurations().length; i++) {
                long delta = audioTrack.getSampleDurations()[i];


                if (currentTime > lastTime && currentTime <= startTime1) {
                    // current sample is still before the new starttime
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTime1) {
                    // current sample is after the new start time and still before the new endtime
                    endSample1 = currentSample;
                }

                lastTime = currentTime;
                currentTime += (double) delta / (double) audioTrack.getTrackMetaData().getTimescale();
                currentSample++;
            }

            CroppedTrack cropperAacTrack = new CroppedTrack(fullAudio, startSample1, endSample1);

            return cropperAacTrack;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullAudio;
    }



    public Runnable runnable =new Runnable() {
        @Override
        public void run() {

            try {

                Movie m = MovieCreator.build(video);

                List nuTracks = new ArrayList<>();

                for (Track t : m.getTracks()) {
                    if (!"soun".equals(t.getHandler())) {
                        nuTracks.add(t);
                    }
                }


                //todo try with mp3
                Track nuAudio = /*new MP3TrackImpl(new FileDataSourceImpl(audio));*/new AACTrackImpl(new FileDataSourceImpl(audio));

                Track crop_track= CropAudio(video,nuAudio);

                nuTracks.add(crop_track);

                m.setTracks(nuTracks);

                Container mp4file = new DefaultMp4Builder().build(m);

                FileChannel fc = new FileOutputStream(new File(output)).getChannel();
                mp4file.writeContainer(fc);
                fc.close();
                progressDialog.dismiss();
                Go_To_preview_Activity();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    };




}
