package org.techtown.foodtruck.search;

import android.view.View;

import org.techtown.foodtruck.account.AccountAdapter;

public interface onSearchItemClickListener {
    public void onItemClick(SearchImageAdapter.ViewHolder holder , View view, int position);
}
