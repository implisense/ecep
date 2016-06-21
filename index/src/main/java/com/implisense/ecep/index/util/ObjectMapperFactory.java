package com.implisense.ecep.index.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class ObjectMapperFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = StdDateFormat.getISO8601Format(TimeZone.getTimeZone("Europe/London"), Locale.ENGLISH);
        objectMapper.setDateFormat(dateFormat);
    }

    public static ObjectMapper instance() {
        return objectMapper;
    }

}
