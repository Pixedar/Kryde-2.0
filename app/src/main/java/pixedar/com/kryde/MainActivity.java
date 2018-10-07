package pixedar.com.kryde;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkPremissions();


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Led"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);


        final MainPagerAdapter adapter = new MainPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults){
             if(grantResults[0] ==PackageManager.PERMISSION_GRANTED ){
          //        Bluetooth bluetooth = new Bluetooth(this);
         //        bluetooth.saveData();
                 finish();
                 startActivity(getIntent());
             }
    }



     void checkPremissions() {

        int hasStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> permissions = new ArrayList<String>();

        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }else{
            try {
                restoreData();
            } catch (IOException e) {
                e.printStackTrace();
            }
   //        Bluetooth bluetooth = new Bluetooth(this);
       //     bluetooth.saveData();
        }
        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), 111);
        }

    }
    private  void restoreData() throws IOException {
        File file1= new File(Environment.getExternalStorageDirectory() + "/WeatherData/","current.txt");
        file1.createNewFile();
        BufferedReader inputStreamA =null;
        inputStreamA = new BufferedReader(new FileReader(file1));
        String data = "";
        while ((data = inputStreamA.readLine()) != null) {
            Parser.restoreCurrent(data);
        }
        inputStreamA.close();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Parser.delay = sharedPref.getInt("delay", 0);
        Log.d("GG", String.valueOf(Parser.delay));
    }

    @Override
    protected void onStop() {
        Bluetooth.disconnect();
        super.onStop();
    }

}
