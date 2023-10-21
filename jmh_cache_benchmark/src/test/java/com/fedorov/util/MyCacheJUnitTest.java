package com.fedorov.util;


public class MyCacheJUnitTest extends ILRUCacheJUnitTest{

    @Override
    protected ICache<Object> getInstance(){
        return  MyCache.getInstance();
    }
}
