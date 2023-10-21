package com.fedorov.util;
    
public class CHMCacheJUnitTest extends ICacheJUnitTest {

    @Override
    protected ICache<Object> getInstance() {
        return CHMCache.getInstance();
    }
}