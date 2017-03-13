package Modules;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atif.maps_.R;

/**
 * Created by Atif on 12/12/16.
 */

public class View_Holder_Planner extends RecyclerView.ViewHolder {

    TextView totalDuration;
    TextView timeRange;
    TextView title1;
    TextView title2;
    TextView title3;
    TextView title4;
    CardView cardView;



    View_Holder_Planner(View itemView) {
            super(itemView);
            //totalDuration = (TextView) itemView.findViewById(R.id.direction_item_total_time);
            //timeRange = (TextView) itemView.findViewById(R.id.direction_item_time);
            title1 = (TextView) itemView.findViewById(R.id.title1);
        title2 = (TextView) itemView.findViewById(R.id.title2);
        title3 = (TextView) itemView.findViewById(R.id.title3);
        title4 = (TextView) itemView.findViewById(R.id.title4);
        cardView = (CardView) itemView.findViewById(R.id.cardView);



    }
    }

