package com.fedorov.util.generic;

import com.fedorov.util.generic.ICache;
import com.fedorov.util.generic.MyCacheFixed;

public class MyCacheFixedJUnitTest extends ILRUCacheJUnitTest{

    @Override
    protected ICache<Object> getInstance(){
        return (ICache<Object>) MyCacheFixed.getInstance();
    }
}