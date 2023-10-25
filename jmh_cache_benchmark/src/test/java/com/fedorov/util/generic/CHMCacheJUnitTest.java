package com.fedorov.util.generic;

import com.fedorov.util.generic.CHMCache;
import com.fedorov.util.generic.ICache;

public class CHMCacheJUnitTest extends ICacheJUnitTest {

    @Override
    protected ICache<Object> getInstance() {
        return CHMCache.getInstance();
    }
}