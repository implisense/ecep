package com.implisense.ecep.api.data;

import com.google.common.base.Charsets;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;

@Singleton
public class Sic07TitleProvider {

    private static final String MAPPING_FILE = "en_industry_titles_class_level_sic07_nace2.csv";
    private Map<String, String> map;

    public Sic07TitleProvider() {
        this.map = loadMapping();
    }

    public String getTitle(String code) {
        return this.map.getOrDefault(code, "");
    }

    private static Map<String, String> loadMapping() {
        InputStream inputStream = Sic07TitleProvider.class.getResourceAsStream(MAPPING_FILE);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
            return reader.lines()
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split("\t", -1))
                    .collect(toMap(cols -> cols[0], cols -> cols[1]));
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading sic07 titles file!", e);
        }
    }
}
