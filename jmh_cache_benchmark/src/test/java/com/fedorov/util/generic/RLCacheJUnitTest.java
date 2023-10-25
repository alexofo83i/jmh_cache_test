package com.fedorov.util.generic;

import com.fedorov.util.generic.ICache;
import com.fedorov.util.generic.RLCache;

public class RLCacheJUnitTest extends ICacheJUnitTest {

    @Override
    protected ICache<Object> getInstance(){
        return RLCache.getInstance();
    }
}
    