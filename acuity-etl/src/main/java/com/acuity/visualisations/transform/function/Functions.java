package com.acuity.visualisations.transform.function;

import java.util.HashMap;
import java.util.Map;

/**
 * Functions registry
 */
public final class Functions {
    private static Map<String, AbstractFunction> aggregators = new HashMap<>();

    static {
        aggregators.put("DateAssembler", new DateAssembler());
        aggregators.put("Concatenate", new Concatenate());
        aggregators.put("DateAssemblerDefaultTime", new DateAssemblerDefaultTime());
        aggregators.put("MaxValue", new MaxValue());
        aggregators.put("ObjectArray", new ObjectArray());
    }

    private Functions() {
    }

    public static AbstractFunction getAggregator(String name) {
        return aggregators.get(name);
    }
}
