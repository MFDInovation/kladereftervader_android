package com.example.huaxie.kladervader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClothesListActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar myToolbar;
    private TextView addPictureButton;
    private String ImageDecode;
    private ImageView imageViewLoad;
    private final static int IMG_RESULT = 1024;
    protected static final int REQUEST_READ_EXTERNAL_STORAGE = 138;
    private ArrayList<Uri> adapterList;
    private MyListViewAdapter mListÁdapter;
    private ArrayList<String> UriList;
    private TextView returnButton;
    private String tempKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_list);

        tempKey = getIntent().getStringExtra("tempKey");
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        imageViewLoad = (ImageView)findViewById(R.id.imageViewLoad);
        addPictureButton = (TextView) findViewById(R.id.add_picture_button);
        addPictureButton.setOnClickListener(this);
        returnButton = (TextView)findViewById(R.id.return_button);
        returnButton.setOnClickListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> oldDataSet  = preferences.getStringSet(tempKey,null);
        if(oldDataSet != null){
            UriList = new ArrayList<String>(oldDataSet);
            adapterList = changeStringListToUri(UriList);
        }else {
            UriList = new ArrayList<String>();
            adapterList = new ArrayList<Uri>();
        }
        ListView myListView = (ListView) findViewById(R.id.picture_list_view);
        myListView.setEmptyView(findViewById(R.id.empty));
        mListÁdapter = new MyListViewAdapter(this,R.id.picture_list_view,adapterList);
        myListView.setAdapter(mListÁdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doWhenReadingAllowed();
                    break;
                } else {
                    String message = "Allow the reading access request to choose picture ";
                    Toast.makeText(this,message,Toast.LENGTH_LONG).show();
                    onActivityResult(requestCode, AppCompatActivity
                            .RESULT_CANCELED, null);
                    break;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMG_RESULT:
                switch (resultCode) {
                    case AppCompatActivity.RESULT_OK:
                        if(data != null){
                            Uri URI = data.getData();
                            adapterList.add(URI);
                            UriList.add(URI.toString());
                            saveDataListInSharedPreferences(UriList);//mainactivity can also read data
                            mListÁdapter.notifyDataSetChanged();
                        }
                        break;
                    case AppCompatActivity.RESULT_CANCELED:
                        break;
                }
                break;
        }
    }



    private void doWhenReadingAllowed(){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, IMG_RESULT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_picture_button:
                if(PermissionUtil.checkPermission(ClothesListActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE))
                { doWhenReadingAllowed();}
                break;
            case R.id.return_button:
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                mListÁdapter.clear();
                finish();
                break;
        }
    }

    private ArrayList<Uri> changeStringListToUri(ArrayList<String> mUriList){
        ArrayList<Uri> list = new ArrayList<Uri>();
        for (String s: mUriList) {
            Uri myUri = Uri.parse(s);
            list.add(myUri);
        }
        return list;
    }

    private void saveDataListInSharedPreferences(ArrayList<String> newList){
        Set<String> myset = new HashSet<String>(newList);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(tempKey,myset);
        editor.apply();
    }

    private void deleteDataListInSharedPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }


    private class MyListViewAdapter extends ArrayAdapter<Uri> {
        public MyListViewAdapter(Context context, int resourceId, ArrayList<Uri> adapterList) {
            super(context, resourceId, adapterList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View baseview = convertView;

            if(baseview == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                baseview = inflater.inflate(R.layout.picture_list_row, null);
            }
            final Uri uri = getItem(position);
            if(uri != null) {
                ImageView image = (ImageView)baseview.findViewById(R.id.picture_place_holder);
                String imagePath = BitmapWorkerTask.getPathFromImageUri(uri,getContext());
                BitmapWorkerTask ImageLoader = new BitmapWorkerTask(image);
                ImageLoader.execute(imagePath);
                TextView title = (TextView)baseview.findViewById(R.id.row_title);
                title.setText(R.string.title);
                TextView delete = (TextView)baseview.findViewById(R.id.delete_button);
                delete.setText(R.string.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        adapterList.remove(uri);
                        UriList.remove(uri.toString());
                        mListÁdapter.notifyDataSetChanged();
                        if(UriList.size()!= 0){
                            saveDataListInSharedPreferences(UriList);//mainactivity can also read data
                        }else {
                            deleteDataListInSharedPreferences();
                        }
                    }
                });
            }
            return baseview;
        }
    }
}
