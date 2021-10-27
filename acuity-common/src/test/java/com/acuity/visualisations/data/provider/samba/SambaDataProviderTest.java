package com.acuity.visualisations.data.provider.samba;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SambaDataProviderTest {

    @InjectMocks
    private SambaDataProvider sambaDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testMatch() {

        softly.assertThat(sambaDataProvider.match("smb://a/b/c")).isTrue();
        softly.assertThat(sambaDataProvider.match("\\\\a\\b\\c")).isTrue();
        softly.assertThat(sambaDataProvider.match("//a/b/c")).isTrue();
        softly.assertThat(sambaDataProvider.match("a/b/c")).isFalse();
        softly.assertThat(sambaDataProvider.match("other://a/b/c")).isFalse();

        try {
            sambaDataProvider.match(null);
            softly.fail("NPE expected");
        } catch(NullPointerException e) {
            softly.assertThat(e.getMessage().equals("location"));
        }
    }
}
