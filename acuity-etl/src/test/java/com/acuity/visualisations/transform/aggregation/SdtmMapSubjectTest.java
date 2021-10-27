package com.acuity.visualisations.transform.aggregation;

import com.acuity.visualisations.batch.processor.SdtmParsers;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class SdtmMapSubjectTest {

    @Test
    public void nullNotMapped() {
        String result = SdtmParsers.parseSubject(null);
        assertThat(result).isNull();
    }

    @Test
    public void emptyStringNotMapped() {
        String result = SdtmParsers.parseSubject("");
        assertThat(result).isEqualTo("");
    }

    @Test
    public void invalidStringNotMapped() {
        String result = SdtmParsers.parseSubject("S");
        assertThat(result).isEqualTo("S");

        result = SdtmParsers.parseSubject("S/");
        assertThat(result).isEqualTo("S/");

        result = SdtmParsers.parseSubject("/E");
        assertThat(result).isEqualTo("/E");
    }

    @Test
    public void extractSubjectFromValidUsubjid() {
        String result = SdtmParsers.parseSubject("D2270C00005/E1001001");
        assertThat(result).isEqualTo("E1001001");

        result = SdtmParsers.parseSubject("S/E");
        assertThat(result).isEqualTo("E");

        result = SdtmParsers.parseSubject("D6140C00001-3000-0001");
        assertThat(result).isEqualTo("3000-0001");
    }
}
