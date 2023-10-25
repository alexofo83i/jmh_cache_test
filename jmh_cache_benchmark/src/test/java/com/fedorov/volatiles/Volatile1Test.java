package com.fedorov.volatiles;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;    
    
/**
 * that test is not valid yet
 */
public class Volatile1Test {

    Volatile1 vol;

    @Before
    public void setup(){
        vol = new Volatile1();
    }
        
    @Test
    public void test() {
        vol.update(1, 1, 1);
        int total = vol.totalDays();
        assertEquals(365+30+1, total);
    }   
}
    