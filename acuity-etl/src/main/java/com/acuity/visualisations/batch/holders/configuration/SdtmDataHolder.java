package com.acuity.visualisations.batch.holders.configuration;

import com.acuity.visualisations.sdtm.SdtmData;
import com.acuity.visualisations.sdtm.SdtmSuppData;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores Supplemental data per file
 */
@Component
@Scope("prototype")
public class SdtmDataHolder {
    private Map<String, SdtmData> mainMap = new HashMap<>();
    private Map<String, SdtmSuppData> suppMap = new HashMap<>();


    public SdtmData getMainData(String file) {
        return mainMap.get(file);
    }

    public boolean containsMainData(String file) {
        SdtmData data = mainMap.get(file);
        return data != null && data.getData().size() > 0;
    }

    public boolean containsSuppData(String file) {
        SdtmSuppData data = suppMap.get(file);
        return data != null && data.getData().size() > 0;
    }

    public void putMainData(String file, SdtmData data) {
        mainMap.put(file, data);
    }

    public SdtmSuppData getSuppData(String file) {
        return suppMap.get(file);
    }

    public void putSuppData(String file, SdtmSuppData data) {
        suppMap.put(file, data);
    }

}
