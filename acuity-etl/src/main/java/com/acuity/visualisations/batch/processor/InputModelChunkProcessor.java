package com.acuity.visualisations.batch.processor;

import com.acuity.visualisations.model.input.InputModelChunk;
import com.acuity.visualisations.model.output.OutputModelChunk;
import org.springframework.batch.item.ItemProcessor;

public interface InputModelChunkProcessor extends ItemProcessor<InputModelChunk, OutputModelChunk> {

}
