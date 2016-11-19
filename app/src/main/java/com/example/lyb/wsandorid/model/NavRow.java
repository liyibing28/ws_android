package com.example.lyb.wsandorid.model;

/**
 * Created by lyb on 16-11-16.
 * 实现对侧滑栏 图标 文字 连接的活动的管理
 */

public class NavRow {
    int mIconResource;
    String mRowText;
    String mOnClickAction;

    public NavRow(int iconResourceId, String rowText, String onClickAction) {
        this.mIconResource = iconResourceId;
        this.mRowText = rowText;
        this.mOnClickAction = onClickAction;
    }
    public int getIconResource() {
        return mIconResource;
    }

    public String getRowText() {
        return mRowText;
    }

    public String getOnClickAction() {
        return mOnClickAction;
    }
}
