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
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        mRealm = Realm.getDefaultInstance();

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
                    String responseData = response.body().string();
                    try {
                        parseAndSaveJsonData(new JSONObject(responseData));
                        startListActivity(enteredDateFrom, enteredDateTo);
                    } catch (JSONException e) {
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

    private void parseAndSaveJsonData(JSONObject jsonObject) {

        try {

            boolean isSuccess = jsonObject.has("success") && jsonObject.getBoolean("success");

            JSONArray dataArray = jsonObject.has("data") ? jsonObject.getJSONArray("data") : null;

            if(isSuccess && dataArray != null) {

                final RealmList<ScheduleItem> scheduleItemRealmList = new RealmList<>();

                for(int i = 0; i < dataArray.length(); i++) {

                    JSONObject data = dataArray.getJSONObject(i);

                    int id = data.has("id") ? data.getInt("id") : -1;
                    int busId = data.has("bus_id") ? data.getInt("bus_id") : -1;

                    int price = data.has("price") ? data.getInt("price") : -1;

                    Date fromDate = convertStringToDate(data.getString("from_date"));
                    Date toDate = convertStringToDate(data.getString("to_date"));

                    String fromTime = data.has("from_time") ? data.getString("from_time") : "none";
                    String toTime = data.has("to_time") ? data.getString("to_time") : "none";

                    String fromInfo = data.has("from_info") ? data.getString("from_info") : "none";
                    String toInfo = data.has("to_info") ? data.getString("to_info") : "none";
                    String info = data.has("info") ? data.getString("info") : "none";

                    JSONObject fromCity = data.has("from_city") ? data.getJSONObject("from_city") : null;
                    JSONObject toCity = data.has("to_city") ? data.getJSONObject("to_city") : null;

                    String fromCityName = fromCity.has("name") ? fromCity.getString("name") : "none";
                    String toCityName = toCity.has("name") ? toCity.getString("name") : "none";

                    int fromCityHighlight = fromCity.has("highlight") ? fromCity.getInt("highlight") : -1;
                    int toCityHighlight = toCity.has("highlight") ? toCity.getInt("highlight") : -1;

                    scheduleItemRealmList.add(new ScheduleItem(id, busId,
                            fromCityName, toCityName,
                            fromCityHighlight, toCityHighlight,
                            fromDate, toDate,
                            fromTime, toTime,
                            fromInfo, toInfo,
                            info, price));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRealm.beginTransaction();
                        mRealm.copyToRealmOrUpdate(scheduleItemRealmList);
                        mRealm.commitTransaction();
                    }
                });
            }

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, R.string.read_error, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void startListActivity(String dateFrom, String dateTo) {

        Intent intent = new Intent(MainActivity.this, ListActivity.class);

        intent.putExtra(ListActivity.EXTRA_DATE_FROM, dateFrom);
        intent.putExtra(ListActivity.EXTRA_DATE_TO, dateTo);

        startActivity(intent);
    }

    /*Date converters*/
    public static Date convertStringToDate(String string) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertDateToString(Date date) {
        return DateFormat.getDateInstance(SimpleDateFormat.LONG, new Locale("ru")).format(date);
    }
}
