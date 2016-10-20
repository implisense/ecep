package com.implisense.ecep.api.data;

import com.implisense.ecep.api.config.GeocoderConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Geocoder {
    private final Map<String, PostcodeData> postcodeMap;

    public Geocoder(GeocoderConfig config) {
        this.postcodeMap = buildMapping(config);
    }

    public PostcodeData lookup(String postcode) {
        return this.postcodeMap.get(postcode);
    }

    private Map<String, PostcodeData> buildMapping(GeocoderConfig config) {
        String source = config.getSource();
        Map<String, PostcodeData> map = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
            if (zis.getNextEntry() == null) {
                throw new IllegalArgumentException("Geocoder source \"" + source + "\" does not contain entries!");
            }
            for (CSVRecord record : CSVFormat.RFC4180.parse(new InputStreamReader(zis, UTF_8))) {
                map.put(record.get(0),
                        new PostcodeData(
                                Double.parseDouble(record.get(2)),
                                Double.parseDouble(record.get(3)),
                                record.get(19).isEmpty() ? -1 : Integer.parseInt(record.get(19)),
                                record.get(20).isEmpty() ? -1 : Integer.parseInt(record.get(20)),
                                record.get(24)));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Geocoder source \"" + source + "\" was not readable!", e);
        }
        return map;
    }
}
