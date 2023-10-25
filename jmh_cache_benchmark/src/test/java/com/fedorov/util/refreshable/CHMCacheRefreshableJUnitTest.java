package com.fedorov.util.refreshable;

import com.fedorov.util.generic.ICache;
import com.fedorov.util.generic.ICacheJUnitTest;

public class CHMCacheRefreshableJUnitTest extends ICacheJUnitTest {

    @Override
    protected ICache<Object> getInstance() {
        return (ICache) CHMCacheRefreshable.getInstance();
    }
}
