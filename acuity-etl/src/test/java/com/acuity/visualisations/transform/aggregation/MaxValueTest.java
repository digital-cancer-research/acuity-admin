/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
