package com.xitij.tiktuk.Utils;

import android.os.Environment;

public class Variables {

    //todo try with mp3
    public static String SelectedAudio="SelectedAudio.aac";

    public static String root= Environment.getExternalStorageDirectory().toString();

    public static String outputfile=root + "/output.mp4";

    public static String output_filter_file=root + "/output-filtered.mp4";


    public static String file_provider_path="com.xitij.tiktuk.provider";

}
