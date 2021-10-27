package com.acuity.visualisations.web.service;

import com.acuity.visualisations.web.dao.HelpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Map;

@Service
public class HelpService implements IHelpService {

    @Autowired
    private HelpDao repo;

    @Override
    public Map<String, Map<String, String>> getHelp() throws JAXBException, IOException {
        return this.repo.parseHelpXml();
    }
}
