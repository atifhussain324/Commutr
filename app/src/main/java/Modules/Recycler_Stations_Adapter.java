package Modules;

/**
 * Created by Atif on 5/7/17.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atif.maps_.R;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;


public class Recycler_Stations_Adapter extends RecyclerView.Adapter<View_Holder_NearbyStations> {


    List<NearbyStations> list = Collections.emptyList();
    Context context;

    public Recycler_Stations_Adapter(List<NearbyStations> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder_NearbyStations onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_cardview, parent, false);
        View_Holder_NearbyStations holder = new View_Holder_NearbyStations(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder_NearbyStations holder, int position) {

        holder.stationName.setText(list.get(position).getStationName());

        holder.stationImage.setImageDrawable(LoadImageFromWebOperations(list.get(position).getImageRef()));
        Log.v("stationNameAdap", list.get(position).getStationName());
        Log.v("imageref", list.get(position).getImageRef());


    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    // Insert a new item to the RecyclerView on a predefined position

}

