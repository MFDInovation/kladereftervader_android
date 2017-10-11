package com.example.huaxie.kladervader;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * This view fragment represents a certain weather and displays a list of saved images for that particular weather.
 */

public class ClothesListWeatherFragment extends Fragment {

    public static final String WEATHER = "weather";
    public static final String DESCRIPTION = "description";
    public static final String IMAGELIST = "imagelist";
    public static final String SAVEDLIST = "savedlist";

    private String currentWeather;
    private ArrayList<Uri> adapterList;
    private ClothesListWeatherFragment.MyListViewAdapter mListAdapter;
    private ArrayList<String> uriList;
    private ListView mList;
    private String contentDesc;

    // Custom create method, needs a weather represented as string, the lists that are used for displaying
    // and managing images as well as the description of the clothes that you should wear
    public static ClothesListWeatherFragment createNew(String weather, ArrayList<Uri> imageList, ArrayList<String> savedList, String desc){
        ClothesListWeatherFragment res = new ClothesListWeatherFragment();
        Bundle args = new Bundle();
        args.putString(WEATHER,weather);
        args.putParcelableArrayList(IMAGELIST,imageList);
        args.putStringArrayList(SAVEDLIST,savedList);
        args.putString(DESCRIPTION,desc);
        res.setArguments(args);
        return res;
    }

    public ClothesListWeatherFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        currentWeather = getArguments().getString(WEATHER);
        adapterList = getArguments().getParcelableArrayList(IMAGELIST);
        uriList = getArguments().getStringArrayList(SAVEDLIST);
        contentDesc = getArguments().getString(DESCRIPTION);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        ViewGroup rootView = (ViewGroup) inflater
               .inflate(R.layout.clothes_list_weather_fragment, container, false);
        mList = (ListView) rootView.findViewById(R.id.picture_list_view);
        mList.setEmptyView(rootView.findViewById(R.id.empty));
        Collections.sort(adapterList,new Comparator<Uri>() {
            @Override
            public int compare(Uri s1, Uri s2) {  // Sort the images so that the standard picture is always first in the list.
                return s1.compareTo(s2);                        // This works since the standard pic is only a resource number and every other image is a media uri.
            }
        });
        mListAdapter = new MyListViewAdapter(getActivity(),R.id.picture_list_view,adapterList);
        mList.setAdapter(mListAdapter);
        TextView txt = (TextView) rootView.findViewById(R.id.empty);
        txt.setText(currentWeather);
        rootView.setContentDescription("Hantera bilder för " + currentWeather);
        return rootView;
    }

    // Adds a picture to the list view, called by the ClothesListActivity when a new picture has been added.
    public void updateListView(Uri u, String s){
        mListAdapter.add(u);
        uriList.add(s);
        mListAdapter.notifyDataSetChanged();
    }

    public String getWeather(){
        return currentWeather;
    }

    // The adapter that handles the ListView and the images within. Each element in the list is a
    // custom view that contains the imageview among other things.
    private class MyListViewAdapter extends ArrayAdapter<Uri> {
        MyListViewAdapter(Context context, int resourceId, ArrayList<Uri> adapterList) {
            super(context, resourceId, adapterList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View baseview = convertView;

            if(baseview == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                baseview = inflater.inflate(R.layout.picture_list_row, null);
            }
            final Uri uri = getItem(position);
            if(uri != null) {
                if(!uri.toString().equals("")) {
                    ZoomableImageView image = (ZoomableImageView) baseview.findViewById(R.id.picture_place_holder);
                    if (TextUtils.isDigitsOnly(uri.toString())) {  //Don't use filepaths, just load image directly. This is when the saved "uri" is actually a resource id. used for standard images.
                        BitmapWorkerTaskDemo clothesLoader = new BitmapWorkerTaskDemo(image, getContext());
                        clothesLoader.execute(Integer.parseInt(uri.toString()));
                        image.setContentDescription(contentDesc);
                        Log.d("bilder", uri.toString());
                        TextView title = (TextView) baseview.findViewById(R.id.row_title);
                        title.setText("Hantera bilder för:" + "\n" + currentWeather);
                        title.setContentDescription("Hantera bilder för " + currentWeather);
                        Button delete = (Button) baseview.findViewById(R.id.delete_button);
                        delete.setVisibility(View.INVISIBLE);
                    }
                    //Image taken from media folder, use filepaths and bitworker.
                    else {
                        image.setContentDescription(contentDesc);
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
                        assert cursor != null;
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imagePath = cursor.getString(columnIndex);
                        cursor.close();
                        BitmapWorkerTask ImageLoader = new BitmapWorkerTask(image);
                        ImageLoader.execute(imagePath);

                        // Now lets check how the image is oriented, if it has been taken as a portrait
                        // then make sure to rotate the image so its not put in a landscape orientation in the imageview itself.
                        // Also need to set the width to height since the rotation has caused width to become height
                        int rotate = getCameraPhotoOrientation(getContext(), uri, imagePath);
                        if (rotate == 90) {
                            image.setRotation(90);
                            int px = image.getLayoutParams().height;
                            if (px > 0) image.getLayoutParams().width = px;
                        } else if (rotate == 270) {
                            image.setRotation(-90);
                            int px = image.getLayoutParams().height;
                            if (px > 0) image.getLayoutParams().width = px;
                        }

                        TextView title = (TextView) baseview.findViewById(R.id.row_title);
                        title.setText(R.string.new_image);

                        Button delete = (Button) baseview.findViewById(R.id.delete_button);
                        delete.setText(R.string.delete);
                        delete.getBackground().setColorFilter(0xffe60000, PorterDuff.Mode.MULTIPLY);
                        delete.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                adapterList.remove(uri);
                                uriList.remove(uri.toString());
                                mListAdapter.remove(uri);
                                if (uriList.size() != 0) {
                                    saveDataListInSharedPreferences(uriList);//mainactivity can also read data
                                } else {
                                    deleteDataListInSharedPreferences();//should any delete one list
                                }
                            }
                        });
                    }
                }
            }
            return baseview;
        }

        private int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
            int rotate = 0;
            try {
                context.getContentResolver().notifyChange(imageUri, null);
                File imageFile = new File(imagePath);

                ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return rotate;
        }
    }

    // Save paths to images that have been added by the user
    private void saveDataListInSharedPreferences(ArrayList<String> newList){
        Set<String> myset = new HashSet<>(newList);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(currentWeather,myset);
        editor.apply();
    }

    // Delete a picture, cannot be called on the standard picture
    private void deleteDataListInSharedPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(currentWeather);
        editor.apply();
    }

}
