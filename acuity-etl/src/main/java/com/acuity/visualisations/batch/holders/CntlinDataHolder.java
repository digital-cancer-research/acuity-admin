package com.acuity.visualisations.batch.holders;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("cntlinHolder")
@Scope("prototype")
public class CntlinDataHolder extends JobExecutionInfoAware {

    private Map<String, Map<String, String>> cntlinMap = new HashMap<String, Map<String, String>>();

    public Map<String, String> getFmtMap(String fmtName) {
        return cntlinMap.get(fmtName);
    }

    public void setFmtMap(String fmtName, Map<String, String> fmtMap) {
        cntlinMap.put(fmtName, fmtMap);
    }

    public String getValue(String fmtName, String fmtValue) {
        if (cntlinMap.get(fmtName) == null) {
            return null;
        }
        return cntlinMap.get(fmtName).get(fmtValue);
    }

}
