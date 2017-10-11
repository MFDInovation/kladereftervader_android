package com.example.huaxie.kladervader;

/*
This activity is used when a user wants to add or delete their own pictures corresponding to
the different weather conditions and temperature. It needs permission to read the smartphones storage
and uses ClothesListWeatherFragments to visualize the currently added pictures as well as the standard picture.
 */

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ClothesListActivity extends AppCompatActivity implements View.OnClickListener{

    private final static int IMG_RESULT = 1024;
    protected static final int REQUEST_READ_EXTERNAL_STORAGE = 138;
    private static final int WEATHER_CONDITIONS = 16;
    private ArrayList<Uri> adapterList;
    private ArrayList<String> UriList;
    private Clothing.TempStatus tempKey;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int currentPage;
    private ClothesListWeatherFragment currentFrag;
    private Button b1,b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_list);

        DisplayMetrics dpMet = this.getResources().getDisplayMetrics();
        int height = dpMet.heightPixels;

        EnumSet tempSet = EnumSet.allOf(Clothing.TempStatus.class);
        ArrayList<Clothing.TempStatus> weatherList = new ArrayList<>(tempSet);

        tempKey = (Clothing.TempStatus) getIntent().getSerializableExtra("tempKey");
        if(tempKey == null){
            tempKey = Clothing.TempStatus.hett; //Default to hett so we don't get a null pointer.
        }
        for(Clothing.TempStatus c : weatherList){
            if(c.getName().equals(tempKey.getName())) {
                currentPage = weatherList.indexOf(c);
            }
        }
        b1 = (Button) findViewById(R.id.clothesLeftButton);
        b2 = (Button) findViewById(R.id.clothesRightButton);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button addPictureButton = (Button) findViewById(R.id.add_picture_button);
        addPictureButton.setOnClickListener(this);
        addPictureButton.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);
        Button returnButton = (Button) findViewById(R.id.return_button);
        returnButton.setOnClickListener(this);
        returnButton.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);

        Button helpButton = (Button) findViewById(R.id.clothesHelpButton);
        helpButton.setOnClickListener(this);
        helpButton.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);
        helpButton.setText("?");

        if(height < 1000 && height > 500){
            addPictureButton.setWidth(150);
            helpButton.setWidth(70);
            returnButton.setWidth(150);
        }
        else if(height < 500){
            addPictureButton.setWidth(70);
            helpButton.setWidth(35);
            returnButton.setWidth(70);
        }


        mPager = (ViewPager) findViewById(R.id.myViewPager);
        mPagerAdapter = new ChangeWeatherAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(1);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // When a certain fragment is selected, we need to save a reference to the item as well as instantiate it.
            // The reference is later used when adding new pictures and reloading the list. Also reload the activities own lists.
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                setWeatherStatus(Clothing.TempStatus.values()[position]);
                reloadLists();
                currentFrag = (ClothesListWeatherFragment) mPagerAdapter.instantiateItem(mPager,position);

                if(position == 0) b1.setVisibility(View.INVISIBLE);
                else if(position == WEATHER_CONDITIONS-1) b2.setVisibility(View.INVISIBLE);
                else{
                    b1.setVisibility(View.VISIBLE);
                    b2.setVisibility(View.VISIBLE);
                }

                Log.d("Was here",tempKey.getName()+position+" "+currentFrag.getWeather());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // init viewpage navigation buttons.
        b1.setText("<");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage>0) mPager.setCurrentItem(currentPage-1);
            }
        });
        b1.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);

        b2.setText(">");
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPage<WEATHER_CONDITIONS) mPager.setCurrentItem(currentPage+1);
            }
        });
        b2.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(currentPage);
            }
        }, 100);

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
                            reloadLists();
                            adapterList.add(URI);
                            UriList.add(URI.toString());
                            saveDataListInSharedPreferences(UriList);//mainactivity can also read data
                            currentFrag.updateListView(URI,URI.toString());
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
                finish();
                break;
            case R.id.clothesHelpButton:
                final Dialog dialog = new Dialog(this);

                dialog.setContentView(R.layout.help_dialog);
                dialog.setTitle("Help dialog");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final TextView editText = (TextView) dialog.findViewById(R.id.helpText);
                editText.setText(R.string.help_text_egnaBilder);
                editText.setContentDescription(getString(R.string.help_text_main));
                Button btn = (Button) dialog.findViewById(R.id.close);
                btn.getBackground().setColorFilter(0xFF003399, PorterDuff.Mode.MULTIPLY);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }});
                dialog.show();
                break;
            default:
                break;
        }
    }

    // Converts the saved strings to uris
    private ArrayList<Uri> changeStringListToUri(ArrayList<String> mUriList){
        ArrayList<Uri> list = new ArrayList<>();
        for (String s: mUriList) {
            Uri myUri = Uri.parse(s);
            list.add(myUri);
        }
        return list;
    }

    private void saveDataListInSharedPreferences(ArrayList<String> newList){
        Set<String> myset = new HashSet<>(newList);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(tempKey.getName(),myset);
        editor.apply();
    }

    private void setWeatherStatus(Clothing.TempStatus s){
        tempKey = s;
    }

    private void reloadLists(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> oldDataSet  = preferences.getStringSet(tempKey.getName(),null);
        if(oldDataSet != null){
            UriList = new ArrayList<>(oldDataSet);
            adapterList = changeStringListToUri(UriList);
        }else {
            UriList = new ArrayList<>();
            adapterList = new ArrayList<>();
        }
    }

    // Since the viewpager preloads pages, we need to make sure the fragments gets the lists that are
    // associated with the particular weather that the fragment is representing
    private ArrayList<Uri> getSpecificWeatherList(String s){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> oldDataSet  = preferences.getStringSet(s,null);
        ArrayList<Uri> u;
        if(oldDataSet != null){
            ArrayList<String> savedList = new ArrayList<>(oldDataSet);
            u = changeStringListToUri(savedList);
        }else {
            u = new ArrayList<>();
        }
        return u;
    }


    private ArrayList<String> getSpecificSavedList(String s){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> oldDataSet  = preferences.getStringSet(s,null);
        ArrayList<String> li;
        if(oldDataSet != null){
             li = new ArrayList<>(oldDataSet);
        }else {
             li = new ArrayList<>();
        }
        return li;
    }


    private class ChangeWeatherAdapter extends FragmentStatePagerAdapter {

        public ChangeWeatherAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String tempDesc = "";
            switch (tempKey){
                case mycketKallt: tempDesc=getString(R.string.mycketKallt); break;
                case kallt: tempDesc=getString(R.string.kallt); break;
                case mycketKalltSno: tempDesc=getString(R.string.mycketKallt); break;
                case kalltSno: tempDesc=getString(R.string.kallt); break;
                case nollgradigtMinus: tempDesc=getString(R.string.nollgradigtMinus); break;
                case nollgradigtMinusRegn: tempDesc=getString(R.string.nollgradigtMinusRegn); break;
                case nollgradigtPlus: tempDesc=getString(R.string.nollgradigtPlus); break;
                case nollgradigtPlusRegn: tempDesc=getString(R.string.nollgradigtPlusRegn); break;
                case kyligt: tempDesc=getString(R.string.kyligt); break;
                case kyligtRegn: tempDesc=getString(R.string.kyligtRegn); break;
                case varmt: tempDesc=getString(R.string.varmt); break;
                case varmtRegn: tempDesc=getString(R.string.varmtRegn); break;
                case varmare: tempDesc=getString(R.string.varmt); break;
                case varmareRegn: tempDesc=getString(R.string.varmtRegn); break;
                case hett: tempDesc=getString(R.string.hett); break;
                case hettRegn: tempDesc=getString(R.string.hettRegn); break;
            }

            return ClothesListWeatherFragment.createNew(Clothing.TempStatus.values()[position].getName(),
                    getSpecificWeatherList(Clothing.TempStatus.values()[position].getName()),
                    getSpecificSavedList(Clothing.TempStatus.values()[position].getName()), tempDesc);
        }

        @Override
        public int getCount() {
            return WEATHER_CONDITIONS;
        }

    }

}
