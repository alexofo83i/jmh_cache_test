package com.fedorov.util; 
    
public class RLCacheJUnitTest extends ICacheJUnitTest {

    @Override
    protected ICache<Object> getInstance(){
        return RLCache.getInstance();
    }
}
    