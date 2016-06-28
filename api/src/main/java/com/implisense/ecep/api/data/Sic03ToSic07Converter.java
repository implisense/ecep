package com.implisense.ecep.api.data;

import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

@Singleton
public class Sic03ToSic07Converter {

    private static final String MAPPING_FILE = "sic03_sic07_proportional_mapping.csv";
    private Map<String, String> map;

    public Sic03ToSic07Converter() {
        this.map = loadMapping();
    }

    public String getSic07(String sic03) {
        return this.map.get(sic03);
    }

    private static Map<String, String> loadMapping() {
        try {
            return Files.lines(Paths.get(Sic03ToSic07Converter.class.getResource(MAPPING_FILE).toURI()), Charsets.UTF_8)
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split("\t", -1))
                    // sort by Count percentage descending
                    .sorted(reverseOrder(comparing(cols -> Integer.parseInt(StringUtils.substring(cols[2], 0, -1)))))
                    // now build a map only considering the first occurence of each sic03 code
                    .collect(toMap(cols -> cols[0], cols -> cols[1], (v1, v2) -> v1, HashMap::new));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Exception while reading sic03 to sic07 mapping file!", e);
        }
    }
}
