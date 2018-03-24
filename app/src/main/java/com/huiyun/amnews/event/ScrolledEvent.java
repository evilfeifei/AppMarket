package com.huiyun.amnews.event;


/**
 * Created by Justy on 2017/12/6.
 */

public class ScrolledEvent {
    private boolean scrolledToTop;

    public boolean isScrolledToTop() {
        return scrolledToTop;
    }

    public void setScrolledToTop(boolean scrolledToTop) {
        this.scrolledToTop = scrolledToTop;
    }
}
