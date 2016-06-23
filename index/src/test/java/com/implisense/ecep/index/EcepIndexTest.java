package com.implisense.ecep.index;

import com.google.common.collect.ImmutableMap;
import com.implisense.ecep.index.model.Address;
import com.implisense.ecep.index.model.Company;
import com.implisense.ecep.index.model.ContentField;
import org.apache.commons.lang3.time.DateParser;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class EcepIndexTest {

    private static final DateParser DATE_PARSER = FastDateFormat.getInstance("dd/MM/yyyy",
            TimeZone.getTimeZone("Europe/London"), Locale.ENGLISH);

    @Test
    public void testBasicCrudFunctionality() throws Exception {
        Company expected = new Company();
        expected.setId("1234567");
        expected.setUri("http://business.data.gov.uk/id/company/1234567");
        expected.setName("ABC Ltd");
        expected.setAddress(new Address());
        expected.getAddress().setLine1("123 WHATEVER ROAD");
        expected.setCategory("Private Limited Company");
        expected.setContent(new ContentField("1234567\nABC Ltd"));
        expected.setStatus("Active");
        expected.setCountryOfOrigin("United Kingdom");
        expected.setIncorporationDate(DATE_PARSER.parse("22/07/1999"));
        index.putCompany(expected);
        index.commit();
        Company actual = index.getCompany(expected.getId());
        assertThat(actual.getName(), equalTo(expected.getName()));
        List<Company> companies = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Company company = new Company();
            company.setId("" + (i + 1234567));
            company.setName("test " + i);
            companies.add(company);
        }
        index.putCompanies(companies);
        index.commit();
        for (int i = 0; i < 10; i++) {
            assertThat(index.getCompany("" + (i + 1234567)).getName(), equalTo("test " + i));
        }
    }

    @Test
    public void testSicTitleManagement() throws Exception {
        index.clear();
        Map<String, String> exampleTitles = ImmutableMap.of(
                "4522", "Erection of roof covering & frames",
                "41202", "Construction of domestic buildings",
                "99999", "Dormant Company"
        );
        index.putSicTitles(exampleTitles);
        index.commit();
        for (Map.Entry<String, String> example : exampleTitles.entrySet()) {
            assertThat(index.getSicTitle(example.getKey()), equalTo(example.getValue()));
        }
        assertThat(index.getSicTitle("123123123"), nullValue());
        assertThat(new TreeMap<>(exampleTitles), equalTo(new TreeMap<>(index.getSicTitleMap())));
    }

    private static Client client;
    private static Node node;
    private static EcepIndex index;
    private static final String INDEX_NAME = "ecep-index-test";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Settings settings = Settings.settingsBuilder()
                .put("node.http.enabled", false)
                .put("path.logs", "target/elasticsearch/logs")
                .put("path.data", "target/elasticsearch/data")
                .put("path.home", "target")
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0).build();
        node = NodeBuilder.nodeBuilder().local(true).settings(settings).node();

        // We wait now for the yellow (or green) status
        node.client().admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
        Assert.assertNotNull(node);
        Assert.assertFalse(node.isClosed());
        client = node.client();
        index = new EcepIndex(client, INDEX_NAME);
        index.setTestMode();
        index.deleteIndex();
        index.createIndex();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (client != null) {
            client.close();
        }
        if (node != null) {
            node.close();
        }
    }

}