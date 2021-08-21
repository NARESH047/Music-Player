package com.example.musicplayer;

import android.Manifest;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Audio>>, Serializable {
    SongAdapter adapter;
    private TextView mEmptyStateTextView;
    private ListView songListView;
    Parcelable state;
    ArrayList<Audio> audioAll;
    String searchStr;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static List<Audio> songListFiltered;
    public static List<Audio> songList;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        songListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyView);
        songListView.setEmptyView(mEmptyStateTextView);
        runTimePermission();

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);

    }

    private void runTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE );
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(1, null, this);
            } else {
                mEmptyStateTextView.setText("Please grant permission to access files and restart app");
            }
        }
    }

    @Override
    public Loader<List<Audio>> onCreateLoader(int id, Bundle args) {
        return new SongLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Audio>> loader, List<Audio> Audio) {
        audioAll = (ArrayList<Audio>) Audio;
        mEmptyStateTextView.setText("No song found");
        View layoutBottom = findViewById(R.id.layoutBottom);
        layoutBottom.setVisibility(View.VISIBLE);
        ProgressBar Loading = (ProgressBar) findViewById(R.id.loading_spinner);
        if(Loading != null) {
            Loading.setVisibility(GONE);
        }
        if(adapter!=null){
            adapter = null;
        }
        adapter = new SongAdapter(audioAll, this);
        songListView.setAdapter(adapter);
        if(state!=null){
            songListView.onRestoreInstanceState(state);
        }

    }


    @Override
    public void onLoaderReset(Loader<List<Audio>> loader) {
        adapter = new SongAdapter(audioAll, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }


    public class SongAdapter extends BaseAdapter implements Filterable{
        private List<Audio> songList;
        private Context context;

        public SongAdapter(List<Audio> songs, Context context) {
            songList = songs;
            songListFiltered = songs;
            this.context = context;
        }

        @Override
        public int getCount() {
            return songListFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.list_item, null);

            TextView songNumView = (TextView) view.findViewById(R.id.songNum);
            songNumView.setText(String.valueOf(songListFiltered.get(position).getId()));
            GradientDrawable numCircle = (GradientDrawable) songNumView.getBackground();
            int numCircleColor = ContextCompat.getColor(MainActivity.this, getNumCircleColor(songListFiltered.get(position).getId()));
            numCircle.setColor(numCircleColor);


            TextView name = (TextView) view.findViewById(R.id.name);

            name.setText(songListFiltered.get(position).getName());

            TextView artist = (TextView) view.findViewById(R.id.artist);
            artist.setText(songListFiltered.get(position).getArtist());

            TextView albumTextView = (TextView) view.findViewById(R.id.album);
            String album = songListFiltered.get(position).getAlbum();
            if (album == null || (album.length() == 0)) {
                albumTextView.setText("Unknown");
            } else {
                albumTextView.setText(album);
            }



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent itemIntent = new Intent(MainActivity.this, SelectedItem.class);
                    itemIntent.putExtra("currentAudioPosition", position);
                    startActivity(itemIntent);
                }
            });

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if(constraint == null || constraint.length() == 0) {
                        filterResults.count = songList.size();
                        filterResults.values = songList;
                    }else{
                        searchStr = constraint.toString().toLowerCase();
                        List<Audio> resultData = new ArrayList<>();
                        for(Audio audioModel:songList){
                            if(audioModel.getName().toLowerCase().contains(searchStr)){
                                resultData.add(audioModel);
                                filterResults.count = resultData.size();
                                filterResults.values = resultData;
                            }
                        }
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    ListView listView = findViewById(R.id.list);
                    TextView textView = findViewById(R.id.emptyView);

                    if(results.count>0) {
                        if(listView.getVisibility() == GONE){
                            listView.setVisibility(View.VISIBLE);
                        }
                        if(textView.getVisibility() == View.VISIBLE){
                                textView.setVisibility(GONE);
                        }
                        songListFiltered = (List<Audio>) results.values;
                        notifyDataSetChanged();
                    } else {
                        listView.setVisibility(GONE);
                        textView.setVisibility(View.VISIBLE);
                    }

                }
            };
            return filter;
        }



        private int getNumCircleColor (int num) {
            int numColorResourceId = 0;
            int numFloor = num;
            if(numFloor == 0 || numFloor%6 ==0 ) {
                numColorResourceId = R.color.songNum1;
            }
            else if(numFloor%5 ==0) {
                numColorResourceId = R.color.songNum2;
            }
            else if(numFloor%4 ==0) {
                numColorResourceId = R.color.songNum3;
            }
            else if(numFloor%3 ==0) {
                numColorResourceId = R.color.songNum4;
            }
            else if(numFloor%2 ==0) {
                numColorResourceId = R.color.songNum5;
            }
            else {
                numColorResourceId = R.color.songNum6;
            }

            return numColorResourceId;
        }



    }

    @Override
    protected void onPause() {
        state = songListView.onSaveInstanceState();
        super.onPause();
    }

}


