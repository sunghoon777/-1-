package org.techtown.foodtruck.account;

import android.view.View;

import org.techtown.foodtruck.account.NoticeAdapter;

public interface OnNoticeItemClickListener {
    public void onItemClick(NoticeAdapter.ViewHolder holder , View view, int position);
}
