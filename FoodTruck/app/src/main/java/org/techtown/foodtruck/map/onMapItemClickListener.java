package org.techtown.foodtruck.map;

import android.view.View;

import org.techtown.foodtruck.Favorite.FavoriteAdapter;


public interface onMapItemClickListener {
    public void onItemClick(MapAdapter.ViewHolder holder , View view, int position);
}
