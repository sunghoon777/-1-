package com.stepbystep.bossapp.foottraffic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stepbystep.bossapp.DO.Traffic;
import com.stepbystep.bossapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    private ArrayList<Traffic> items = new ArrayList<Traffic>();
    private onMapItemClickListener listener;
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_map_marker_description_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView trafficName;
        private TextView distance;
        private TextView percentageRankInRegion;
        private TextView percentageRankInTotalRegion;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trafficName = itemView.findViewById(R.id.recyclerview_map_marker_description_item_traffic_name);
            distance = itemView.findViewById(R.id.recyclerview_map_marker_description_item_distance);
            percentageRankInRegion = itemView.findViewById(R.id.recyclerview_map_marker_description_item_percentage_rank_in_region);
            percentageRankInTotalRegion = itemView.findViewById(R.id.recyclerview_map_marker_description_item_percentage_rank_in_total_region);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(ViewHolder.this,view,getAdapterPosition());
                }
            });
        }

        public void setItem(Traffic traffic){
            DecimalFormat f = new DecimalFormat("#.##");
            trafficName.setText(traffic.getHubName());
            distance.setText(f.format(traffic.getDistance()/1000)+"km");
            String region = traffic.getRegion();
            if(region.equals("seoul")){
                percentageRankInRegion.setText("서울 지역 내 상위 "+traffic.getPercentageRankInRegion()+"% 유동 인구");
            }
            else{
                percentageRankInRegion.setText("경기 지역 내 상위 "+traffic.getPercentageRankInRegion()+"% 유동 인구");
            }

            percentageRankInTotalRegion.setText("전체 지역 내 상위 "+traffic.getPercentageRankInTotalRegion()+"% 유동 인구");
        }
    }

    public void addItem(Traffic traffic){
        items.add(traffic);
    }

    public Traffic getItem(int position){
        return items.get(position);
    }

    public void setItems(ArrayList<Traffic> items){
        this.items = items;
    }

    public ArrayList<Traffic> getItems(){
        return  items;
    }

    public void setListener(onMapItemClickListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
