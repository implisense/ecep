package com.implisense.ecep.api.data;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.implisense.ecep.index.model.Company;
import com.implisense.ecep.index.model.PreviousName;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class CompaniesHouseFormatParserTest {

    private static final String SAMPLE_FILE_NAME = "BasicCompanyData.sample.csv";
    private static final DateParser DATE_PARSER = FastDateFormat.getInstance("yyyy-MM-dd",
            TimeZone.getTimeZone("Europe/London"), Locale.ENGLISH);

    @Test
    public void testSampleFile() throws Exception {
        byte[] input = loadSampleFile();

        Sic03ToSic07Converter sic03ToSic07Converter = new Sic03ToSic07Converter();
        CompaniesHouseFormatParser parser = new CompaniesHouseFormatParser(sic03ToSic07Converter);
        List<Company> companies = Lists.newArrayList(parser.iterateCompanies(input, Charsets.US_ASCII));
        assertThat(companies, hasSize(3));
        {
            Company c = companies.get(0);
            assertThat(c.getName(), equalTo("!WHATEVER LIMITED"));
            assertThat(c.getId(), equalTo("01234567"));
            assertThat(c.getAddress().getCareOf(), equalTo(""));
            assertThat(c.getAddress().getPoBox(), equalTo(""));
            assertThat(c.getAddress().getLine1(), equalTo("123 ABC DEF"));
            assertThat(c.getAddress().getLine2(), equalTo("372 OLD STREET"));
            assertThat(c.getAddress().getTown(), equalTo("LONDON"));
            assertThat(c.getAddress().getCounty(), equalTo(""));
            assertThat(c.getAddress().getCountry(), equalTo(""));
            assertThat(c.getAddress().getPostCode(), equalTo("DW3N 6BV"));
            assertThat(c.getCategory(), equalTo("Private Limited Company"));
            assertThat(c.getStatus(), equalTo("Active"));
            assertThat(c.getCountryOfOrigin(), equalTo("United Kingdom"));
            assertThat(c.getDissolutionDate(), equalTo(DATE_PARSER.parse("2015-12-31")));
            assertThat(c.getIncorporationDate(), equalTo(DATE_PARSER.parse("2010-09-21")));
            assertThat(c.getSicCodes(), equalTo(ImmutableList.of("59.11", "74.10", "74.20")));
            assertThat(c.getUri(), equalTo("http://business.data.gov.uk/id/company/07382019"));
            assertThat(c.getPreviousNames(), empty());
        }
        {
            Company c = companies.get(1);
            assertThat(c.getName(), equalTo("BLABLAH LTD."));
            assertThat(c.getId(), equalTo("07162534"));
            assertThat(c.getAddress().getCareOf(), equalTo(""));
            assertThat(c.getAddress().getPoBox(), equalTo(""));
            assertThat(c.getAddress().getLine1(), equalTo("OUT HOUSE 1-2"));
            assertThat(c.getAddress().getLine2(), equalTo("BLAH CIRCUS"));
            assertThat(c.getAddress().getTown(), equalTo("LONDON"));
            assertThat(c.getAddress().getCounty(), equalTo(""));
            assertThat(c.getAddress().getCountry(), equalTo(""));
            assertThat(c.getAddress().getPostCode(), equalTo("WF2N 4HB\\"));
            assertThat(c.getCategory(), equalTo("Private Limited Company"));
            assertThat(c.getStatus(), equalTo("Active"));
            assertThat(c.getCountryOfOrigin(), equalTo("United Kingdom"));
            assertThat(c.getDissolutionDate(), nullValue());
            assertThat(c.getIncorporationDate(), equalTo(DATE_PARSER.parse("2003-05-03")));
            assertThat(c.getSicCodes(), equalTo(ImmutableList.of("82.99")));
            assertThat(c.getUri(), equalTo("http://business.data.gov.uk/id/company/04753368"));
            assertThat(c.getPreviousNames(), equalTo(ImmutableList.of(
                    new PreviousName(DATE_PARSER.parse("2009-01-27"), "DISTINCTIVE IMPRINT WORLDWIDE LIMITED"),
                    new PreviousName(DATE_PARSER.parse("2003-09-02"), "DISTINCTIVE IMPRINT CONSULTANTS LIMITED"),
                    new PreviousName(DATE_PARSER.parse("2003-07-03"), "AMEROOLA CONSULTANTS LIMITED")
            )));
        }
        {
            Company c = companies.get(2);
            assertThat(c.getName(), equalTo("\"XYZ CLOTHING\" LLC"));
            assertThat(c.getId(), equalTo("FC099887"));
            assertThat(c.getAddress().getCareOf(), equalTo(""));
            assertThat(c.getAddress().getPoBox(), equalTo(""));
            assertThat(c.getAddress().getLine1(), equalTo("BUILDING LEFT TO THE \"TUUL\""));
            assertThat(c.getAddress().getLine2(), equalTo("DRY CLEANING"));
            assertThat(c.getAddress().getTown(), equalTo("KHAN-UUL DISTRICT"));
            assertThat(c.getAddress().getCounty(), equalTo("ULAANBAATAR, MONGOLIA"));
            assertThat(c.getAddress().getCountry(), equalTo("MONGOLIA"));
            assertThat(c.getAddress().getPostCode(), equalTo(""));
            assertThat(c.getCategory(), equalTo("Other company type"));
            assertThat(c.getStatus(), equalTo("Active"));
            assertThat(c.getCountryOfOrigin(), equalTo("MONGOLIA"));
            assertThat(c.getDissolutionDate(), nullValue());
            assertThat(c.getIncorporationDate(), equalTo(DATE_PARSER.parse("2006-06-14")));
            assertThat(c.getSicCodes(), empty());
            assertThat(c.getUri(), equalTo("http://business.data.gov.uk/id/company/FC027187"));
            assertThat(c.getPreviousNames(), empty());
        }
    }

    @Test
    public void testZippedSampleFile() throws Exception {
        byte[] input = loadSampleFile();

        // create a virtual zip file
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        zos.putNextEntry(new ZipEntry(SAMPLE_FILE_NAME));
        zos.write(input);
        zos.close();

        // now take the zipped bytes as input
        input = bos.toByteArray();

        Sic03ToSic07Converter sic03ToSic07Converter = new Sic03ToSic07Converter();
        CompaniesHouseFormatParser parser = new CompaniesHouseFormatParser(sic03ToSic07Converter);
        List<Company> companies = Lists.newArrayList(parser.iterateCompanies(input, Charsets.US_ASCII));

        // just check whether this worked at all.. :)
        assertThat(companies, hasSize(3));
        assertThat(companies.get(0).getName(), equalTo("!WHATEVER LIMITED"));
    }

    private byte[] loadSampleFile() throws IOException {
        InputStream is = CompaniesHouseFormatParserTest.class.getResourceAsStream(SAMPLE_FILE_NAME);
        byte[] input = ByteStreams.toByteArray(is);
        is.close();
        return input;
    }
}