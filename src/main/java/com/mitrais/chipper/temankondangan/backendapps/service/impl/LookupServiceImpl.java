package com.mitrais.chipper.temankondangan.backendapps.service.impl;

import com.mitrais.chipper.temankondangan.backendapps.model.Lookup;
import com.mitrais.chipper.temankondangan.backendapps.repository.LookupRepository;
import com.mitrais.chipper.temankondangan.backendapps.service.LookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LookupServiceImpl implements LookupService {

    LookupRepository lookupRepository;

    @Autowired
    public LookupServiceImpl(LookupRepository lookupRepository) {
        this.lookupRepository = lookupRepository;
    }

    @Override
    public List<Lookup> getLookup(String key) {
        return lookupRepository.findByLookupKey(key);
    }
}
