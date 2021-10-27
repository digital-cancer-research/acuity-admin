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
