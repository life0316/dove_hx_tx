package com.haoxi.dove.callback;

import com.haoxi.dove.newin.bean.InnerDoveData;

public interface ExpandItemClickListener {
    void itemClick(InnerDoveData doveData, int position, String pigeonCode, String pigeonObjId, String ringObjID, String ringCode);
}
