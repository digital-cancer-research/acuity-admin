package com.acuity.visualisations.transform.function;

public class StringAssembler extends AbstractFunction<String> {

    @Override
    public String function(Object[] params) {
        if (params == null || params.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Object param : params) {
            if (param == null) {
                continue;
            }
            if (!(param instanceof String)) {
                continue;
            }
            String pStr = (String) param;
            if (pStr.isEmpty()) {
                continue;
            }
            builder.append(pStr);
            builder.append("; ");
        }
        if (builder.length() != 0) {
            builder.setLength(builder.length() - 2);
        }
        return builder.toString();
    }
}
