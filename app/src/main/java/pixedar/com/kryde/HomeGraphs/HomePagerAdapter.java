package pixedar.com.kryde.HomeGraphs;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import pixedar.com.kryde.WeatherDataController;

public class HomePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    WeatherDataController weatherDataController;
    public HomePagerAdapter(FragmentManager fm, int NumOfTabs, WeatherDataController weatherDataController ) {
        super(fm);
        this.weatherDataController = weatherDataController;
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                HomeTempFragment homeTempFragment = new HomeTempFragment();
                homeTempFragment.setListener(weatherDataController);
                return homeTempFragment;
            case 1:
                HomeHumFragment HomeHumFragment = new HomeHumFragment();
                HomeHumFragment.setListener(weatherDataController);
                return HomeHumFragment;
            case 2:
                HomePressFragment HomePressFragment = new HomePressFragment();
                HomePressFragment.setListener(weatherDataController);
                return HomePressFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

