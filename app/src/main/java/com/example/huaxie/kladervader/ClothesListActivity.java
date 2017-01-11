package com.example.huaxie.kladervader;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_list);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        imageViewLoad = (ImageView)findViewById(R.id.imageViewLoad);
        addPictureButton = (TextView) findViewById(R.id.add_picture_button);
        addPictureButton.setOnClickListener(this);
        returnButton = (TextView)findViewById(R.id.return_button);
        returnButton.setOnClickListener(this);
        if(savedInstanceState != null) //have saved list data
        {
            UriList = savedInstanceState.getStringArrayList("UriList");
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
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mListÁdapter.notifyDataSetChanged();
                                }
                            });
                            /*imageViewLoad.setImageBitmap(BitmapFactory
                                    .decodeFile(ImageDecode));
                            imageViewLoad.invalidate();*/
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
                intent.putExtra("UriList",UriList);
                setResult(RESULT_OK,intent);
                mListÁdapter.clear();
                finish();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("UriList",UriList);
        super.onSaveInstanceState(outState);
    }

    private ArrayList<Uri> changeStringListToUri(ArrayList<String> mUriList){
        ArrayList<Uri> list = new ArrayList<Uri>();
        for (String s: mUriList) {
            Uri myUri = Uri.parse(s);
            list.add(myUri);
        }
        return list;
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
            Uri uri = getItem(position);
            if(uri != null) {
                ImageView image = (ImageView)baseview.findViewById(R.id.picture_place_holder);
                image.setImageURI(uri);
                TextView title = (TextView)baseview.findViewById(R.id.row_title);
                title.setText(R.string.title);
                TextView delete = (TextView)baseview.findViewById(R.id.delete_button);
                delete.setText(R.string.delete);
            }
            return baseview;
        }




    }
}
