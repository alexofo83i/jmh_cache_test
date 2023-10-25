package com.fedorov.util.generic;

import com.fedorov.util.generic.ICache;
import com.fedorov.util.generic.MyCache;

public class MyCacheJUnitTest extends ILRUCacheJUnitTest{

    @Override
    protected ICache<Object> getInstance(){
        return  MyCache.getInstance();
    }
}
