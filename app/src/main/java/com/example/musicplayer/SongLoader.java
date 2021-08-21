package com.example.musicplayer;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.List;

public class SongLoader extends AsyncTaskLoader<List<Audio>> {

    private String mUrl;

    public SongLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public List<Audio> loadInBackground() {
        List<Audio> allAudioFromDevice = com.example.superhero.GetMusic.getAllAudioFromDevice(getContext());
        return allAudioFromDevice;
    }

}
