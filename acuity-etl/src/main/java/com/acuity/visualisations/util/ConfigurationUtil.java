package com.acuity.visualisations.util;

import com.acuity.visualisations.transform.entity.EntitiesRootRule;
import com.acuity.visualisations.transform.entity.EntityBaseRule;
import com.acuity.visualisations.transform.entity.EntityDescriptionRule;
import com.acuity.visualisations.transform.entitytotable.EntityTablesRootRule;
import com.acuity.visualisations.transform.table.TablesRootRule;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public final class ConfigurationUtil {

    private static final String ENTITIES_TABLES_RESOURCE = "EntitiesToTables.xml";
    private static final String ENTITY_TABLES_CONTEXT_PATH = "com.acuity.visualisations.transform.entitytotable";

    private static final String TABLES_MAPPING_RESOURCE = "TablesDescription.xml";
    private static final String TABLES_MAPPING_CONTEXT_PATH = "com.acuity.visualisations.transform.table";

    private static final String ENTITIES_MAPPING_RESOURCE = "EntitiesDescription.xml";
    private static final String ENTITIES_MAPPING_CONTEXT_PATH = "com.acuity.visualisations.transform.entity";

    private static XMLInputFactory xmlInputFactory;
    private static EntitiesRootRule entitiesRootRule;
    private static TablesRootRule tablesRootRule;
    private static EntityTablesRootRule entityTablesRootRule;
    private static ConcurrentHashMap<String, EntityDescriptionRule> entityDescriptionRuleMap;

    static {
        xmlInputFactory = XMLInputFactory.newInstance();
        try {
            entitiesRootRule = getRootRule(ENTITIES_MAPPING_CONTEXT_PATH, ENTITIES_MAPPING_RESOURCE);
            entityDescriptionRuleMap = entitiesRootRule.getEntity().stream()
                    .collect(toMap(EntityBaseRule::getName, Function.identity(), (o1, o2) -> o1, ConcurrentHashMap::new));
            tablesRootRule = getRootRule(TABLES_MAPPING_CONTEXT_PATH, TABLES_MAPPING_RESOURCE);
            entityTablesRootRule = getRootRule(ENTITY_TABLES_CONTEXT_PATH, ENTITIES_TABLES_RESOURCE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ConfigurationUtil() {
    }

    public static EntitiesRootRule getEntitiesDescriptionRule() {
        return entitiesRootRule;
    }

    public static EntityDescriptionRule getEntityDescriptionRuleByName(String name) {
        return entityDescriptionRuleMap.get(name);
    }

    public static TablesRootRule getTableDescriptionRule() {
        return tablesRootRule;
    }

    public static EntityTablesRootRule getEntityTableDescriptionRule() {
        return entityTablesRootRule;
    }

    private static <T> T getRootRule(String contextPath, String resource) throws Exception {
        JAXBContext jaxbContextForEntityTablesMapping = JAXBContext.newInstance(contextPath);
        InputStream streamForEntityTablesMapping = ConfigurationUtil.class.getClassLoader().getResourceAsStream(resource);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(streamForEntityTablesMapping);
        JAXBElement<T> unmarshalEntityTablesMapping = (JAXBElement<T>) jaxbContextForEntityTablesMapping
                .createUnmarshaller()
                .unmarshal(xmlStreamReader);
        return unmarshalEntityTablesMapping.getValue();
    }


}
