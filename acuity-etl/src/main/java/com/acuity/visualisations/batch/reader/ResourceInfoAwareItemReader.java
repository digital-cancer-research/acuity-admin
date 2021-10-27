package com.acuity.visualisations.batch.reader;

import com.acuity.visualisations.model.input.InputModelChunk;
import org.springframework.batch.item.ItemStreamReader;

public interface ResourceInfoAwareItemReader extends ItemStreamReader<InputModelChunk> {

    void setResourceInfo(ResourceInfo resourceInfo);

}
