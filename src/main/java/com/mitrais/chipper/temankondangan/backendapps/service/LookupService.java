package com.mitrais.chipper.temankondangan.backendapps.service;

import com.mitrais.chipper.temankondangan.backendapps.model.Lookup;

import java.util.List;

public interface LookupService {
    List<Lookup> getLookup(String key);
}
