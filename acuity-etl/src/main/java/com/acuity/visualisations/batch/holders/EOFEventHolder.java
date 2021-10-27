package com.acuity.visualisations.batch.holders;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component("eofHolder")
@Scope("prototype")
public class EOFEventHolder extends JobExecutionInfoAware {

    private class EntityInfo {
        private Map<String, Boolean> fileNames = new HashMap<String, Boolean>();

        private void setFileFinished(String fileName) {
            Iterator<String> fileNamesIt = fileNames.keySet().iterator();
            boolean flag = false;
            while (fileNamesIt.hasNext()) {
                String curFileName = fileNamesIt.next();
                if (curFileName.equals(fileName)) {
                    fileNamesIt.remove();
                    flag = true;
                    break;
                }
            }
            if (flag) {
                fileNames.put(fileName, true);
            }
        }
    }

    private Map<String, EntityInfo> entitiesByNames = new HashMap<String, EntityInfo>();

    public void addFileRule(String dataFileName, List<String> entities) {
        for (String entityName : entities) {
            if (!entitiesByNames.containsKey(entityName)) {
                entitiesByNames.put(entityName, new EntityInfo());
            }
            entitiesByNames.get(entityName).fileNames.put(dataFileName, false);
        }
    }

    public void setFileFinished(String filename) {
        for (Map.Entry<String, EntityInfo> entry : entitiesByNames.entrySet()) {
            entry.getValue().setFileFinished(filename);
        }
    }

    public boolean areAllEntitiesFinished() {
        boolean flag = true;
        for (Map.Entry<String, EntityInfo> entry : entitiesByNames.entrySet()) {
            flag = flag && isEntityFinished(entry.getKey());
        }
        return flag;
    }

    public boolean isEntityFinished(String entityName) {
        if (!entitiesByNames.containsKey(entityName)) {
            return true;
        }
        boolean isFinished = true;
        Boolean[] flags = entitiesByNames.get(entityName).fileNames.values().toArray(new Boolean[0]);
        for (Boolean flag : flags) {
            isFinished = isFinished && flag;
        }
        return isFinished;
    }

    public Map<String, Boolean> getEntities() {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        for (Map.Entry<String, EntityInfo> entry : entitiesByNames.entrySet()) {
            boolean isFinished = true;
            Boolean[] flags = entry.getValue().fileNames.values().toArray(new Boolean[0]);
            for (Boolean flag : flags) {
                isFinished = isFinished && flag;
            }
            map.put(entry.getKey(), isFinished);
        }
        return map;
    }
}
