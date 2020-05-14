package com.xitij.tiktuk.API;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File file;
    private UploadListener uploadListener;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public ProgressRequestBody(File file, UploadListener uploadListener) {
        this.file = file;
        this.uploadListener = uploadListener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("video/*");
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream inputStream = new FileInputStream(file);
        long uploaded = 0;
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = inputStream.read(buffer)) != -1) {
                uploaded += read;
                sink.write(buffer,0,read);
                handler.post(new ProgressUpdater(uploaded,fileLength));
            }
        }finally {
            inputStream.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long uploaded,fileLength;
        ProgressUpdater(long uploaded, long fileLength) {
            this.uploaded = uploaded;
            this.fileLength = fileLength;
        }

        @Override
        public void run() {
            try {
                int progress = (int) (100 * uploaded/fileLength);

                if (progress == 100) {
                    uploadListener.onFinish();
                }
                else {
                    uploadListener.onProgressUpdate(progress);
                }
            } catch (ArithmeticException e) {
                uploadListener.onError();
                e.printStackTrace();
            }
        }
    }
}
