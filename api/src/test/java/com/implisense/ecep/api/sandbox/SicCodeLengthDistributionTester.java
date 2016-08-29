package com.implisense.ecep.api.sandbox;

import com.google.common.base.Charsets;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.implisense.ecep.api.data.CompaniesHouseFormatParser;
import com.implisense.ecep.api.data.Sic03ToSic07Converter;
import com.implisense.ecep.index.model.Company;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toSet;

public class SicCodeLengthDistributionTester {

    public static void main(String[] args) throws IOException {
        File dir = new File("/home/hkorte/Projects/ECEP");
        Sic03ToSic07Converter sic03ToSic07Converter = new Sic03ToSic07Converter();
        CompaniesHouseFormatParser parser = new CompaniesHouseFormatParser(sic03ToSic07Converter);

        Multiset<Integer> lengthDistribution = TreeMultiset.create();
        for (int i = 1; i <= 5; i++) {
            File file = new File(dir, "BasicCompanyData-2016-06-01-part" + i + "_5.csv");
            System.out.println("File: " + file.getName());
            for (Company company : parser.iterateCompanies(Files.readAllBytes(file.toPath()), Charsets.US_ASCII)) {
                Set<Integer> lengths = company.getSicCodes().stream().map(String::length).collect(toSet());
//                if (lengths.size() > 1) {
//                    System.out.println("different sic versions for the same company: " + company.getId()
//                            + " -> " + lengths);
//                }
                if (lengths.isEmpty()) {
                    lengthDistribution.add(-1);
                } else {
                    lengthDistribution.add(lengths.stream().collect(summingInt(v -> v.intValue())));
                }
            }
        }
        System.out.println("Lengths:");
        for (Multiset.Entry<Integer> entry : lengthDistribution.entrySet()) {
            System.out.printf("%3d %10d\n", entry.getElement(), entry.getCount());
        }
    }
}
