package com.example.kreatimont.smartbusschedule.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kreatimont.smartbusschedule.R;
import com.example.kreatimont.smartbusschedule.model.ScheduleItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.kreatimont.smartbusschedule.DateConverter.convertStringToDate;

public class MainActivity extends AppCompatActivity {

    private EditText textFrom;
    private EditText textTo;
    private Button sendButton;
    private LinearLayout inputFormLayout;
    private ProgressBar progressBar;

    private OkHttpClient httpClient;
    private Realm mRealm;

    private String enteredDateFrom, enteredDateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpClient = new OkHttpClient();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        mRealm = Realm.getInstance(config);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.clear_db_action) {
            mRealm.beginTransaction();
            mRealm.deleteAll();
            mRealm.commitTransaction();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFormWithProgressBar(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onBackPressed() {
        if(progressBar.getVisibility() == View.VISIBLE) {
            replaceFormWithProgressBar(false);
        } else {
            super.onBackPressed();
        }
    }

    private void initUI() {

        textFrom = (EditText)findViewById(R.id.editTextFrom);
        textTo = (EditText)findViewById(R.id.editTextTo);

        textFrom.addTextChangedListener(new PatternedTextWatcher("####-##-##"));
        textTo.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        sendButton = (Button)findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textTo != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textTo.getWindowToken(), 0);
                }
                handleEnteredDate();
            }
        });

        inputFormLayout = (LinearLayout)findViewById(R.id.input_form);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        replaceFormWithProgressBar(false);
    }

    private String createRequestUrl(String dateFrom, String dateTo) {
        return getString(R.string.request_template, dateFrom, dateTo);
    }

    private void handleEnteredDate() {

        RealmResults<ScheduleItem> realmResults = mRealm.where(ScheduleItem.class).findAllSorted("fromDate");

        enteredDateFrom = textFrom.getText().toString();
        enteredDateTo = textTo.getText().toString();

        Date fromDate = convertStringToDate(enteredDateFrom);
        Date toDate = convertStringToDate( enteredDateTo);

        if(fromDate != null && toDate != null) {

            if((realmResults.size() > 2)
                    && (realmResults.get(0).getFromDate().compareTo(fromDate) <= 0)
                    && (realmResults.get(realmResults.size() - 1).getFromDate().compareTo(toDate)) >= 0) {

                startListActivity(enteredDateFrom, enteredDateTo);
            } else {
                requestScheduleFromServer(createRequestUrl(enteredDateFrom, enteredDateTo));
            }
        } else {
            Toast.makeText(this, R.string.date_format_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void requestScheduleFromServer(String url) {

        Request request = new Request.Builder().url(url).build();

        replaceFormWithProgressBar(true);

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        replaceFormWithProgressBar(false);
                        Toast.makeText(MainActivity.this, R.string.load_error, Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(jsonObject.getBoolean("success")) {

                            JSONArray data = jsonObject.getJSONArray("data");
                            Type dataListType = new TypeToken<Collection<ScheduleItem>>() {}.getType();
                            final List<ScheduleItem> schedules = new Gson().fromJson(String.valueOf(data), dataListType);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mRealm.beginTransaction();
                                    mRealm.copyToRealmOrUpdate(schedules);
                                    mRealm.commitTransaction();
                                }
                            });

                            startListActivity(enteredDateFrom, enteredDateTo);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.read_error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, R.string.read_error, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void replaceFormWithProgressBar(boolean isVisible) {
        if(isVisible) {
            inputFormLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            inputFormLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void startListActivity(String dateFrom, String dateTo) {

        Intent intent = new Intent(MainActivity.this, ListActivity.class);

        intent.putExtra(ListActivity.EXTRA_DATE_FROM, dateFrom);
        intent.putExtra(ListActivity.EXTRA_DATE_TO, dateTo);

        startActivity(intent);
    }

}
