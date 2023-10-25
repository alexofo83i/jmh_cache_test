package com.fedorov.util.refreshable;

import com.fedorov.util.generic.ICache;

public interface ICacheRefreshable<T> extends ICache<T> {
    void refresh();
}