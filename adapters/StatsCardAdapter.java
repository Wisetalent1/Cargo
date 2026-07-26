package com.nicargo.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.nicargo.app.R;
import com.nicargo.app.models.StatsCardItem;
import java.util.ArrayList;
import java.util.List;

public class StatsCardAdapter extends RecyclerView.Adapter<StatsCardAdapter.StatsViewHolder> {
    
    private Context context;
    private List<StatsCardItem> statsList;
    private LayoutInflater inflater;

    public StatsCardAdapter(Context context) {
        this.context = context;
        this.statsList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_stats_card, parent, false);
        return new StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        StatsCardItem item = statsList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return statsList.size();
    }

    public void setStats(List<StatsCardItem> stats) {
        this.statsList.clear();
        if (stats != null) {
            this.statsList.addAll(stats);
        }
        notifyDataSetChanged();
    }

    public void addStat(StatsCardItem stat) {
        this.statsList.add(stat);
        notifyItemInserted(statsList.size() - 1);
    }

    public void clearStats() {
        this.statsList.clear();
        notifyDataSetChanged();
    }

    public static class StatsViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconImage;
        private TextView titleText;
        private TextView valueText;
        private TextView subtitleText;

        public StatsViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.statIcon);
            titleText = itemView.findViewById(R.id.statTitle);
            valueText = itemView.findViewById(R.id.statValue);
            subtitleText = itemView.findViewById(R.id.statSubtitle);
        }

        public void bind(StatsCardItem item) {
            titleText.setText(item.getTitle());
            valueText.setText(item.getValue());
            
            if (item.getSubtitle() != null && !item.getSubtitle().isEmpty()) {
                subtitleText.setText(item.getSubtitle());
                subtitleText.setVisibility(View.VISIBLE);
            } else {
                subtitleText.setVisibility(View.GONE);
            }
            
            if (item.getIconRes() != 0) {
                iconImage.setImageResource(item.getIconRes());
            }
            
            // Set color based on stat type
            int colorRes = item.getColorRes();
            if (colorRes != 0) {
                valueText.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));
            }
            
            // Set background color based on card type
            int bgColorRes = item.getBackgroundColorRes();
            if (bgColorRes != 0) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), bgColorRes));
            }
        }
    }
}
