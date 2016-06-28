package com.implisense.ecep.api.data;

import com.google.common.collect.ImmutableList;
import com.implisense.ecep.index.model.Address;
import com.implisense.ecep.index.model.Company;
import com.implisense.ecep.index.model.PreviousName;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

@Singleton
public class CompaniesHouseFormatParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompaniesHouseFormatParser.class);

    private Sic03ToSic07Converter sic03ToSic07Converter;

    @Inject
    public CompaniesHouseFormatParser(Sic03ToSic07Converter sic03ToSic07Converter) {
        this.sic03ToSic07Converter = sic03ToSic07Converter;
    }

    public Iterable<Company> iterateCompanies(byte[] input, Charset charset) {
        final Iterator<CSVRecord> records = buildReader(input, charset);
        final ColumnIndices idx = parseHeader(records.next());
        return new Iterable<Company>() {
            @Override
            public Iterator<Company> iterator() {
                return new Iterator<Company>() {

                    @Override
                    public boolean hasNext() {
                        return records.hasNext();
                    }

                    @Override
                    public Company next() {
                        Company company = parseLine(records.next(), idx);
                        return company;
                    }
                };
            }
        };
    }

    private static class ColumnIndices {
        private int NAME = -1;
        private int NUMBER = -1;
        private int ADDR_CAREOF = -1;
        private int ADDR_POBOX = -1;
        private int ADDR_LINE1 = -1;
        private int ADDR_LINE2 = -1;
        private int ADDR_TOWN = -1;
        private int ADDR_COUNTY = -1;
        private int ADDR_COUNTRY = -1;
        private int ADDR_POSTCODE = -1;
        private int CATEGORY = -1;
        private int STATUS = -1;
        private int COUNTRYOFORIGIN = -1;
        private int DISSOLUTIONDATE = -1;
        private int INCORPORATIONDATE = -1;
        private int URI = -1;
        private int SICCODESSTART = -1;
        private int SICCODESEND = -1;
        private int PREVIOUSNAMESSTART = -1;
        private int PREVIOUSNAMESEND = -1;
    }

    private static Iterator<CSVRecord> buildReader(byte[] input, Charset charset) {
        InputStream is;
        // make it a ZipInputStream if it is readable as such
        try {
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(input));
            ZipEntry entry = zis.getNextEntry();
            if (entry == null) {
                is = new ByteArrayInputStream(input);
            } else {
                is = zis;
            }
        } catch (IOException e) {
            is = new ByteArrayInputStream(input);
        }
        try {
            return CSVFormat.RFC4180.parse(new InputStreamReader(is, charset)).iterator();
        } catch (IOException e) {
            throw new CompaniesHouseParsingException("Invalid CSV file format!", e);
        }
    }

    private static final Pattern SIC_CODE_TITLE_PATTERN = Pattern.compile("^(\\d+) - (.+)$");

    private Company parseLine(CSVRecord line, ColumnIndices idx) {
        Company company = new Company();
        company.setName(line.get(idx.NAME));
        company.setId(line.get(idx.NUMBER));
        Address address = new Address();
        address.setCareOf(line.get(idx.ADDR_CAREOF));
        address.setPoBox(line.get(idx.ADDR_POBOX));
        address.setLine1(line.get(idx.ADDR_LINE1));
        address.setLine2(line.get(idx.ADDR_LINE2));
        address.setTown(line.get(idx.ADDR_TOWN));
        address.setCounty(line.get(idx.ADDR_COUNTY));
        address.setCountry(line.get(idx.ADDR_COUNTRY));
        address.setPostCode(line.get(idx.ADDR_POSTCODE));
        company.setAddress(address);
        company.setCategory(line.get(idx.CATEGORY));
        company.setStatus(line.get(idx.STATUS));
        company.setCountryOfOrigin(line.get(idx.COUNTRYOFORIGIN));
        company.setIncorporationDate(parseDate(line.get(idx.INCORPORATIONDATE)));
        company.setDissolutionDate(parseDate(line.get(idx.DISSOLUTIONDATE)));
        company.setUri(line.get(idx.URI));
        Set<String> sicCodes = new LinkedHashSet<>(idx.SICCODESEND - idx.SICCODESSTART);
        for (int i = idx.SICCODESSTART; i < idx.SICCODESEND; i++) {
            Matcher m = SIC_CODE_TITLE_PATTERN.matcher(line.get(i));
            if (m.find()) {
                String code = normalizeSicCode(m.group(1));
                if (code != null) {
                    sicCodes.add(code);
                }
            }
        }
        company.setSicCodes(ImmutableList.copyOf(sicCodes));
        List<PreviousName> previousNames = new ArrayList<>(idx.PREVIOUSNAMESEND - idx.PREVIOUSNAMESSTART);
        for (int i = idx.PREVIOUSNAMESSTART; i < idx.PREVIOUSNAMESEND; i += 2) {
            Date changeDate = parseDate(line.get(i));
            String name = line.get(i + 1).trim();
            if (changeDate != null && !"".equals(name)) {
                previousNames.add(new PreviousName(changeDate, name));
            }
        }
        company.setPreviousNames(ImmutableList.copyOf(previousNames));
        return company;
    }

    private String normalizeSicCode(String code) {
        if (code.length() == 5) {
            // we add the separating dot and strip the sub-class (level 5)
            return code.substring(0, 2) + "." + code.substring(2, 4);
        } else if (code.length() == 4) {
            // we add the separating dot and convert sic03 to sic07
            code = code.substring(0, 2) + "." + code.substring(2, 4);
            return this.sic03ToSic07Converter.getSic07(code);
        } else {
            LOGGER.warn("Unexpected SIC code format: \"" + code + "\"");
            return null;
        }
    }

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final DateParser DATE_PARSER = FastDateFormat.getInstance(DATE_PATTERN,
            TimeZone.getTimeZone("Europe/London"), Locale.ENGLISH);

    private static Date parseDate(String dateString) {
        try {
            String trimmed = dateString.trim();
            if (isNullOrEmpty(trimmed)) {
                return null;
            } else {
                return DATE_PARSER.parse(dateString);
            }
        } catch (ParseException e) {
            throw new CompaniesHouseParsingException("Unexpected date format: \"" + dateString
                    + "\" - expected pattern: \"" + DATE_PATTERN + "\"", e);
        }
    }

    private static ColumnIndices parseHeader(CSVRecord header) {
        ColumnIndices idx = new ColumnIndices();
        for (int i = 0; i < header.size(); i++) {
            String trimmed = header.get(i).trim();
            switch (trimmed) {
                case "CompanyName":
                    idx.NAME = i;
                    break;
                case "CompanyNumber":
                    idx.NUMBER = i;
                    break;
                case "RegAddress.CareOf":
                    idx.ADDR_CAREOF = i;
                    break;
                case "RegAddress.POBox":
                    idx.ADDR_POBOX = i;
                    break;
                case "RegAddress.AddressLine1":
                    idx.ADDR_LINE1 = i;
                    break;
                case "RegAddress.AddressLine2":
                    idx.ADDR_LINE2 = i;
                    break;
                case "RegAddress.PostTown":
                    idx.ADDR_TOWN = i;
                    break;
                case "RegAddress.County":
                    idx.ADDR_COUNTY = i;
                    break;
                case "RegAddress.Country":
                    idx.ADDR_COUNTRY = i;
                    break;
                case "RegAddress.PostCode":
                    idx.ADDR_POSTCODE = i;
                    break;
                case "CompanyCategory":
                    idx.CATEGORY = i;
                    break;
                case "CompanyStatus":
                    idx.STATUS = i;
                    break;
                case "CountryOfOrigin":
                    idx.COUNTRYOFORIGIN = i;
                    break;
                case "DissolutionDate":
                    idx.DISSOLUTIONDATE = i;
                    break;
                case "IncorporationDate":
                    idx.INCORPORATIONDATE = i;
                    break;
                case "URI":
                    idx.URI = i;
                    break;
            }
            if (trimmed.startsWith("SICCode")) {
                if (idx.SICCODESSTART == -1) {
                    idx.SICCODESSTART = i;
                }
            } else if (idx.SICCODESSTART != -1 && idx.SICCODESEND == -1) {
                idx.SICCODESEND = i;
            }
            if (trimmed.startsWith("PreviousName")) {
                if (idx.PREVIOUSNAMESSTART == -1) {
                    idx.PREVIOUSNAMESSTART = i;
                }
            } else if (idx.PREVIOUSNAMESSTART != -1 && idx.PREVIOUSNAMESEND == -1) {
                idx.PREVIOUSNAMESEND = i;
            }
        }
        if (idx.PREVIOUSNAMESEND == -1) {
            idx.PREVIOUSNAMESEND = header.size();
        }
        return idx;
    }
}
