package com.implisense.ecep.api.data;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class Sic03ToSic07ConverterTest {

    @Test
    public void testCertainAmbiguousMappings() throws Exception {
        Sic03ToSic07Converter converter = new Sic03ToSic07Converter();
        assertThat(converter.getSic07("01.11"), equalTo("01.11"));
        assertThat(converter.getSic07("01.13"), equalTo("01.24"));
        assertThat(converter.getSic07("17.54"), equalTo("13.99"));
        assertThat(converter.getSic07("92.62"), equalTo("93.12"));
        assertThat(converter.getSic07("92.71"), equalTo("92.00"));

    }
}