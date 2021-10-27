package com.acuity.visualisations.transform.aggregation;

import com.acuity.visualisations.batch.processor.SdtmParsers;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class SdtmMapCentreTest {

    @Test
    public void nullNotMapped() {
        Integer result = SdtmParsers.parseCentre(null);
        assertThat(result).isNull();
    }

    @Test
    public void emptyStringNotMapped() {
        Integer result = SdtmParsers.parseCentre("");
        assertThat(result).isNull();
    }

    @Test
    public void invalidStringNotMapped() {
        Integer result = SdtmParsers.parseCentre("S");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("/E");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E1");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E12");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E123");
        assertThat(result).isNull();

        result = SdtmParsers.parseCentre("S/E1234");
        assertThat(result).isNull();
    }

    @Test
    public void validStringCorrectlyMapped() {
        Integer result = SdtmParsers.parseCentre("D2270C00005/E1001001");
        assertThat(result).isEqualTo(1001);

        result = SdtmParsers.parseCentre("S/E12345");
        assertThat(result).isEqualTo(1234);

        result = SdtmParsers.parseCentre("D6140C00001-3000-0001");
        assertThat(result).isEqualTo(3000);
    }
}
