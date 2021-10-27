package com.acuity.visualisations.transform.function;

public class Concatenate extends AbstractFunction<String> {
    public String function(Object[] items) {
        final StringBuilder res = new StringBuilder();
        for (Object item : items) {
            res.append(item);
        }
        return res.toString();
    }

}
