package com.fedorov.util.generic;

import com.fedorov.util.generic.ICache;
import com.fedorov.util.generic.RLCacheLRU;

public class RLCacheLRUJUnitTest extends ILRUCacheJUnitTest {

    @Override
    protected ICache<Object> getInstance(){
        return RLCacheLRU.getInstance();
    }
}