package com.example.kreatimont.smartbusschedule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kreatimont.smartbusschedule.R;
import com.example.kreatimont.smartbusschedule.adapter.RecyclerItemClickListener;
import com.example.kreatimont.smartbusschedule.adapter.RecyclerViewAdapter;
import com.example.kreatimont.smartbusschedule.model.ScheduleItem;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.example.kreatimont.smartbusschedule.DateConverter.convertStringToDate;

public class ListActivity extends AppCompatActivity {

    public static final String EXTRA_DATE_FROM = "date_from";
    public static final String EXTRA_DATE_TO = "date_to";

    private Realm mRealm;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private Date currentDateFrom = null;
    private Date currentDateTo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();

        currentDateFrom = convertStringToDate(intent.getStringExtra(EXTRA_DATE_FROM));
        currentDateTo = convertStringToDate(intent.getStringExtra(EXTRA_DATE_TO));

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        mRealm = Realm.getInstance(config);

        initUI();
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
        TextView emptyStub = (TextView) findViewById(R.id.empty_list_stub);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent  intent = new Intent(ListActivity.this, DetailedActivity.class);
                intent.putExtra(DetailedActivity.EXTRA_ID,
                        mRealm.where(ScheduleItem.class)
                                .between("fromDate", currentDateFrom, currentDateTo)
                                .findAllSorted("fromDate").get(position).getId());
                startActivity(intent);
            }
        }));

        RealmResults<ScheduleItem> realmResults =
                mRealm.where(ScheduleItem.class).between("fromDate", currentDateFrom, currentDateTo).findAllSorted("fromDate");

        if(realmResults.size() == 0) {
            emptyStub.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(realmResults, this);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
