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

package com.acuity.visualisations.transform.function;

import java.math.BigDecimal;

public class MaxValue extends AbstractFunction<Number> {

    @Override
    public Number function(Object[] params) {

        Number maxValue = null;
        for (Object param : params) {
            if (param == null) {
                continue;
            }
            Number intParam = maxValue;
            if (param.getClass() == Integer.TYPE) {
                intParam = (Integer) param;
            } else if (param instanceof BigDecimal) {
                intParam = (BigDecimal) param;
            } else {
                if (param.toString().matches("\\d+(\\.\\d+)?")) {
                    intParam = (int) Math.round(Double.valueOf(param.toString()));
                }
            }
            if (maxValue == null || (intParam.doubleValue() > maxValue.doubleValue())) {
                maxValue = intParam;
            }
        }
        return maxValue;
    }
}
