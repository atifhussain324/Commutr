package Modules;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.atif.maps_.Alert;
import com.example.atif.maps_.R;
import com.example.atif.maps_.View_Holder;

import java.util.Collections;
import java.util.List;

/**
 * Created by Atif on 12/12/16.
 */

public class Recycler_Route_Adapter  extends RecyclerView.Adapter<View_Holder_Planner> {

    private int lastPosition = -1;

    List<Alert> list = Collections.emptyList();
    Context context;

    public Recycler_Route_Adapter(List<Alert> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder_Planner onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_row_layout, parent, false);
        View_Holder_Planner holder = new View_Holder_Planner(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder_Planner holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        //holder.title.setText(list.get(position).getDirection()+"  "+list.get(position).getTrain()+" Train");
        //holder.description.setText("Status:   "+list.get(position).getStatus());
        //holder.imageView.setImageResource(list.get(position).imageId);

        //animate(holder);
        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

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
    public void insert(int position, Alert data) {
        list.add(position, data);
        notifyItemInserted(position);
    }


    // Remove a RecyclerView item containing a specified Data object
    public void remove(Alert data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }
}

