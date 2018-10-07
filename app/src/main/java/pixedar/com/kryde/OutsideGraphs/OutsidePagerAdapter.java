package pixedar.com.kryde.OutsideGraphs;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import pixedar.com.kryde.WeatherDataController;

public class OutsidePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    WeatherDataController weatherDataController;
    public  OutsidePagerAdapter(FragmentManager fm, int NumOfTabs, WeatherDataController weatherDataController ) {
        super(fm);
        this.weatherDataController = weatherDataController;
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                OutsideTempFragment outsideTempFragment = new OutsideTempFragment();
                outsideTempFragment.setListener(weatherDataController);
                return  outsideTempFragment;
            case 1:
                OutsideHumFragment outsideHumragment = new OutsideHumFragment();
                outsideHumragment.setListener(weatherDataController);
                return  outsideHumragment;
            case 2:
                OutsideWindFragment outsideWindFragment = new OutsideWindFragment();
                outsideWindFragment.setListener(weatherDataController);
                return outsideWindFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
