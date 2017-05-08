package Modules;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atif.maps_.R;

/**
 * Created by Atif on 5/7/17.
 */

public class View_Holder_NearbyStations extends RecyclerView.ViewHolder {
    TextView stationName;
    ImageView stationImage;
    CardView cardView;

    View_Holder_NearbyStations(View itemView) {
        super(itemView);
        stationName = (TextView) itemView.findViewById(R.id.stationName);
        stationImage = (ImageView) itemView.findViewById(R.id.stationImage);
        cardView = (CardView) itemView.findViewById(R.id.station_card_view);


    }
}
