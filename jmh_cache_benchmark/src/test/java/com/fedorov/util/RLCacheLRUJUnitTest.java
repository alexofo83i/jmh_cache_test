package com.fedorov.util; 
    
public class RLCacheLRUJUnitTest extends ILRUCacheJUnitTest {

    @Override
    protected ICache<Object> getInstance(){
        return RLCacheLRU.getInstance();
    }
}