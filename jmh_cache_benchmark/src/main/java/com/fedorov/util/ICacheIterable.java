package com.fedorov.util;

public interface ICacheIterable<T> extends ICache<T>{
    public void doForEach(  Callable<T> coll )  throws Exception;

    @FunctionalInterface
    public interface Callable<T> {
        T call(String v) throws Exception;
    }
}