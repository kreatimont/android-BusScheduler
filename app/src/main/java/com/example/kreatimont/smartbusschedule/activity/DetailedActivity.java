package com.example.kreatimont.smartbusschedule.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kreatimont.smartbusschedule.R;
import com.example.kreatimont.smartbusschedule.model.ScheduleItem;

import io.realm.Realm;

public class DetailedActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "extra_id";

    private TextView fromCity, toCity, fromDate, toDate, fromTime, toTime, fromInfo, toInfo, info, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        initUI();

        int scheduleId = getIntent().getIntExtra(EXTRA_ID, 0);

        Realm realm = Realm.getDefaultInstance();
        try {
            ScheduleItem scheduleItem = realm.where(ScheduleItem.class).equalTo("id", scheduleId).findFirst();
            setUpData(scheduleItem);
        } finally {
            realm.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {

        fromCity = (TextView)findViewById(R.id.detailed_from_city);
        toCity = (TextView)findViewById(R.id.detailed_to_city);

        fromDate = (TextView) findViewById(R.id.detailed_from_date);
        toDate = (TextView) findViewById(R.id.detailed_to_date);

        fromTime = (TextView)findViewById(R.id.detailed_from_time);
        toTime = (TextView)findViewById(R.id.detailed_to_time);

        fromInfo = (TextView)findViewById(R.id.detailed_from_info);
        toInfo = (TextView)findViewById(R.id.detailed_to_info);

        info = (TextView)findViewById(R.id.detailed_info);
        price = (TextView)findViewById(R.id.detailed_price);

    }

    private void setUpData(ScheduleItem scheduleItem) {

        fromCity.setText(scheduleItem.getFromCityString());
        toCity.setText(scheduleItem.getToCityString());

        fromDate.setText(MainActivity.convertDateToString(scheduleItem.getFromDate()));
        toDate.setText(MainActivity.convertDateToString(scheduleItem.getToDate()));

        fromTime.setText(scheduleItem.getFromTime());
        toTime.setText(scheduleItem.getToTime());

        fromInfo.setText(scheduleItem.getFromInfo());
        toInfo.setText(scheduleItem.getToInfo());

        info.setText(scheduleItem.getInfo());
        price.setText(getString(R.string.price_template, scheduleItem.getPrice()));

    }

}
