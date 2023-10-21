package com.fedorov.util;


public class MyCacheFixedJUnitTest extends ILRUCacheJUnitTest{

    @Override
    protected ICache<Object> getInstance(){
        return (ICache<Object>) MyCacheFixed.getInstance();
    }
}