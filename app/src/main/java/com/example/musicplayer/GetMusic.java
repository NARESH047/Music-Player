package com.example.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class GetMusic {

    public static List<Audio> getAllAudioFromDevice(Context context) {
        List<Audio> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        int currentId = 1;
//        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Audio audio = new Audio();
                String path = cursor.getString(0);
                if (path != null && (path.endsWith(".aac")
                        || path.endsWith(".mp3")
                        || path.endsWith(".mp4")
                        || path.endsWith(".wav")
                        || path.endsWith(".ogg")
                        || path.endsWith(".ac3")
                        || path.endsWith(".m4a"))) {
//                    String fullpath = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.DATA));
//                    String album_name = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
//                    int album_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//                    String artist_name = cursor.getString(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                    int artist_id = cursor.getInt(cursor
//                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
                    String songName = cursor.getString(1);
                    String artist = cursor.getString(3);
                    String album = cursor.getString(2);
                    audio.setPath(path);
                    audio.setName(songName);
                    audio.setArtist(artist);
                    audio.setAlbum(album);
                    audio.setId(currentId);
                    currentId = currentId+1;
                    tempAudioList.add(audio);
                }
            }
            cursor.close();
        }

        return tempAudioList;
    }
}
