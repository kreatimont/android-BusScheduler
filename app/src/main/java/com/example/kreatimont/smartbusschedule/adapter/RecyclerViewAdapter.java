package com.example.kreatimont.smartbusschedule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kreatimont.smartbusschedule.R;
import com.example.kreatimont.smartbusschedule.activity.MainActivity;
import com.example.kreatimont.smartbusschedule.model.ScheduleItem;

import io.realm.RealmResults;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private RealmResults<ScheduleItem> scheduleItems;

    public RecyclerViewAdapter(RealmResults<ScheduleItem> realmList, Context context) {
        this.scheduleItems = realmList;
        this.context = context;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.bind(scheduleItems.get(position));
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fromCity, toCity, fromDate, toDate, fromTime, toTime, price, pathTime;

        ViewHolder(View itemView) {
            super(itemView);
            fromCity = (TextView) itemView.findViewById(R.id.from_city);
            toCity = (TextView) itemView.findViewById(R.id.to_city);

            fromDate = (TextView)itemView.findViewById(R.id.from_date);
            toDate = (TextView)itemView.findViewById(R.id.to_date);

            fromTime = (TextView) itemView.findViewById(R.id.from_time);
            toTime = (TextView) itemView.findViewById(R.id.to_time);

            price = (TextView) itemView.findViewById(R.id.price);
            pathTime = (TextView) itemView.findViewById(R.id.path_time);

        }

        void bind(ScheduleItem scheduleItem) {
            fromCity.setText(scheduleItem.getFromCityString());
            toCity.setText(scheduleItem.getToCityString());

            fromDate.setText(MainActivity.convertDateToString(scheduleItem.getFromDate()));
            toDate.setText(MainActivity.convertDateToString(scheduleItem.getToDate()));

            fromTime.setText(scheduleItem.getFromTime().substring(0,5));
            toTime.setText(scheduleItem.getToTime().substring(0,5));

            price.setText(context.getString(R.string.price_template, scheduleItem.getPrice()));


            pathTime.setText(context.getString(
                    R.string.path_time_template,
                    generatePathTime(scheduleItem.getFromTime(),scheduleItem.getToTime())));
        }

        private String generatePathTime(String fromTime, String toTime) {
            String[] fromTimeArray = fromTime.split(":");
            String[] toTimeArray = toTime.split(":");

            int hour = Math.abs(Integer.valueOf(toTimeArray[0]) - Integer.valueOf(fromTimeArray[0]));
            int min = Math.abs(Integer.valueOf(toTimeArray[1]) - Integer.valueOf(fromTimeArray[1]));

            return hour + " ч. " + (min != 0 ? " мин." : "");
        }
    }

}
