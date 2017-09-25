package com.haoxi.dove.bean;

public class IsMateBean {
    private String title;
    private boolean iconUp;

    public IsMateBean(String title, boolean iconUp) {
        this.title = title;
        this.iconUp = iconUp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIconUp() {
        return iconUp;
    }

    public void setIconUp(boolean iconUp) {
        this.iconUp = iconUp;
    }
}
