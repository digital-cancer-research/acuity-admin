package com.acuity.visualisations.transform.aggregation;

import com.acuity.visualisations.transform.function.MaxValue;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by knml167 on 01/07/2014.
 */
public class MaxValueTest {
    @Test
    public void integersTest () {
        Integer[] arg = new Integer[]{10, null, 20, 30, 1};
        Number res = (new MaxValue()).function(arg);
        Assert.assertEquals(res, 30);
    }
    @Test
    public void bigDecimalsTest () {
        BigDecimal[] arg = new BigDecimal[]{
                new BigDecimal(10.3),
                new BigDecimal(20.00),
                new BigDecimal(30.00),
                null,
                new BigDecimal(10.00)};
        Number res = (new MaxValue()).function(arg);
        Assert.assertEquals(res, new BigDecimal(30));
   }

   @Test
    public void stringsTest () {
       String[] arg = new String[]{"10", "20", "", "30", null, "40.4", "1"};
        Number res = (new MaxValue()).function(arg);
        Assert.assertEquals(res, 40);
   }
}
