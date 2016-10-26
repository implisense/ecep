package com.implisense.ecep.api.data;

import com.implisense.ecep.api.config.GeocoderConfig;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Singleton
public class Geocoder {
    private final String postcodeMapSourceFile;
    private Map<String, PostcodeData> postcodeMap;

    @Inject
    public Geocoder(GeocoderConfig config) {
        this.postcodeMapSourceFile = config.getSource();
        this.postcodeMap = null;
    }

    public void init() {
        if (this.postcodeMap == null) {
            this.postcodeMap = this.buildMapping();
        }
    }

    public PostcodeData lookup(String postcode) {
        return this.postcodeMap.get(postcode);
    }

    private Map<String, PostcodeData> buildMapping() {
        String source = this.postcodeMapSourceFile;
        Map<String, PostcodeData> map = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
            if (zis.getNextEntry() == null) {
                throw new IllegalArgumentException("Geocoder source \"" + source + "\" does not contain entries!");
            }
            CSVParser records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new InputStreamReader(zis, UTF_8));
            for (CSVRecord record : records) {
                map.put(record.get(0),
                        new PostcodeData(
                                Double.parseDouble(record.get(2)),
                                Double.parseDouble(record.get(3)),
                                record.get(19).isEmpty() ? null : Integer.parseInt(record.get(19)),
                                record.get(20).isEmpty() ? null : Integer.parseInt(record.get(20)),
                                record.get(24).isEmpty() ? null : record.get(24)));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Geocoder source \"" + source + "\" was not readable!", e);
        }
        return map;
    }
}
