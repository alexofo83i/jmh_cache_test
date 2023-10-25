package com.fedorov.util.generic;
 
public class CHMCacheJUnitTest extends ICacheJUnitTest {

    @Override
    protected ICache<Object> getInstance() {
        return CHMCache.getInstance();
    }
}