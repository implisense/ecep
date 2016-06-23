package com.implisense.ecep.api.util;

import com.implisense.ecep.index.EcepIndex;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class SicTitleProvider {
    private Map<String, String> codeTitleMap;
    private EcepIndex index;

    @Inject
    public SicTitleProvider(EcepIndex index) {
        this.index = index;
        this.codeTitleMap = new HashMap<>(this.index.getSicTitleMap());
    }

    public String getTitle(String code) {
        return this.codeTitleMap.get(code);
    }

    public void putAll(Map<String, String> codeTitleMap) {
        this.codeTitleMap.putAll(codeTitleMap);
        this.index.putSicTitles(codeTitleMap);
    }
}
