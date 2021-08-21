package com.example.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.musicplayer.MainActivity.songListFiltered;

public class SelectedItem extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    ImageButton play, pause, stop, next, previous;
    boolean isPlaying = false;
    SeekBar seekBar;
    Runnable runnable;
    Handler handler;
    int position;
    String songPath;
    int currentPosition;
    TextView currentSongName, currentAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_screen);

        Intent i = getIntent();
        position = i.getIntExtra("currentAudioPosition", 0);
        currentPosition = position;
        ArrayList<Audio> audioAll = (ArrayList<Audio>) songListFiltered;
        songPath = songListFiltered.get(currentPosition).getPath();
        Log.e(TAG, "songPath:" + songPath);
        currentSongName = (TextView) findViewById(R.id.nametextView);
        currentAlbumName = (TextView) findViewById(R.id.albumTextView);
        currentSongName.setText(songListFiltered.get(currentPosition).getName());
        currentAlbumName.setText(songListFiltered.get(currentPosition).getAlbum());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songPath));


        play = (ImageButton) findViewById(R.id.playButton);
        pause = (ImageButton) findViewById(R.id.pauseButton);
        stop = (ImageButton) findViewById(R.id.stopButton);
        next = (ImageButton) findViewById(R.id.next);
        previous = (ImageButton) findViewById(R.id.previous);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        handler = new Handler();
//        mediaPlayer = MediaPlayer.create(this, R.raw.sample);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress < mediaPlayer.getDuration()) {
                        mediaPlayer.seekTo(progress);
                        seekBar.setProgress(progress);
                    } else {
                        isPlaying = false;
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songListFiltered.get(currentPosition).getPath()));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songListFiltered.get(currentPosition).getPath()));
                    mediaPlayer.start();
                    isPlaying = true;
                    updateSeekBar();
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    isPlaying = false;
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mediaPlayer.stop();
                    isPlaying = false;
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mediaPlayer.stop();
                    isPlaying = false;
                    mediaPlayer.release();
                    if (currentPosition + 1 < songListFiltered.size()) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songListFiltered.get(currentPosition + 1).getPath()));
                        currentSongName.setText(songListFiltered.get(currentPosition + 1).getName());
                        currentAlbumName.setText(songListFiltered.get(currentPosition + 1).getAlbum());
                        currentPosition = currentPosition + 1;

                    } else {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songListFiltered.get(currentPosition).getPath()));
                        Toast.makeText(getApplicationContext(), "End of playlist", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    isPlaying = false;
                    if ((currentPosition - 1) != 0) {
                        currentSongName.setText(songListFiltered.get(currentPosition - 1).getName());
                        currentAlbumName.setText(songListFiltered.get(currentPosition - 1).getAlbum());
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songListFiltered.get(currentPosition - 1).getPath()));
                        currentPosition = currentPosition - 1;
                    } else {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songListFiltered.get(currentPosition).getPath()));
                        Toast.makeText(getApplicationContext(), "End of playlist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void updateSeekBar() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        seekBar.setProgress(currentPosition);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    updateSeekBar();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }

}




