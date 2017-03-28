package com.gpaddy.hungdh.myvideoads;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private String urlVideo = "https://www.dropbox.com/s/rcsmlw60oomf1ba/larva.mp4?dl=1";
    private String pathVideo = "";

    private LinearLayout llPlay;
    private VideoView vvPlay;

    private Button btnPlay;

    private long startTime = 0, endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llPlay = (LinearLayout) findViewById(R.id.llPlay);
        vvPlay = (VideoView) findViewById(R.id.vvPlay);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPlay.setEnabled(false);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });

        startTime = System.currentTimeMillis();
        new AsyncLoadVideo().execute(urlVideo);
    }

    private void playVideo() {
        if (!pathVideo.equals("")) {
            llPlay.setVisibility(View.VISIBLE);
            vvPlay.setVideoPath(pathVideo);
            vvPlay.start();
        }
    }

    private class AsyncLoadVideo extends AsyncTask<String, Void, Boolean> {

        private String path = "";

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                File downloadingMediaFile = new File(MainActivity.this.getCacheDir(), "downloadingMedia.mp4");
                path = downloadingMediaFile.getPath();

                // download the file
                InputStream input = new BufferedInputStream(connection.getInputStream());
                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[8192];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                pathVideo = path;

                btnPlay.setEnabled(true);
                btnPlay.setText("Enable");
                endTime = System.currentTimeMillis();

                Toast.makeText(MainActivity.this, "Distance: " + (endTime - startTime) / 1000, Toast.LENGTH_LONG).show();
            }
        }
    }
}
