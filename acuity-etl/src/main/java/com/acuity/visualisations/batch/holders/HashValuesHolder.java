/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.batch.holders;

import com.acuity.visualisations.aspect.TimeMe;
import com.acuity.visualisations.dal.util.State;
import com.acuity.visualisations.mapping.OctetString;
import org.apache.commons.lang.StringUtils;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.HTreeMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component("hashHolder")
@Scope("prototype")
@TimeMe
public class HashValuesHolder extends JobExecutionInfoAware {

    private DB db = null;

    private Map<Class<?>, Map<OctetString, RowParameters>> hashValues = new HashMap<>();

    private String studyGuid;
    private String studyName;

    public void initializeMapDB() {
        checkDBInitialized();
    }

    private void checkDBInitialized() {
        if (db == null) {
            if (StringUtils.isEmpty(studyName)) {
                throw new IllegalStateException("Study name is not set");
            }
            File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "hashes_" + studyName.replaceAll("\\W+", "_"));
            try {
                createDb(file);
            } catch (IOError e) {
                if (file.exists()) {
                    file.delete();
                }
                createDb(file);
            }
        }
    }

    private void createDb(File file) {
        db = DBMaker
                .newFileDB(file)
                .transactionDisable()
                .asyncWriteEnable()
                .make();
    }

    public void addHashValuesForTable(Class<?> entityClass, String uniqueHash, int secondaryHash, String id, State state, boolean actionPerformed) {
        checkDBInitialized();
        db.checkNotClosed();
        Map<OctetString, RowParameters> classHashes = getMapDBHashMap(entityClass);
        RowParameters parameters = new RowParameters(secondaryHash, id, state, actionPerformed);
        classHashes.put(new OctetString(uniqueHash), parameters);
    }

    private HTreeMap<OctetString, RowParameters> getMapDBHashMap(Class<?> entityClass) {
        HTreeMap<OctetString, RowParameters> hashMap;
        if (!hashValues.containsKey(entityClass)) {
            hashMap = db.getHashMap(entityClass.getName());
            Set<Fun.Tuple2<Boolean, OctetString>> keySet = db.getHashSet(getNoActionIndexSetName(entityClass));
            Bind.secondaryKey(hashMap, keySet, (os, rp) -> rp.isActionPerformed());
            hashValues.put(entityClass, hashMap);
        } else {
            hashMap = db.getHashMap(entityClass.getName());
        }
        return hashMap;
    }

    private String getNoActionIndexSetName(Class<?> entityClass) {
        return entityClass.getName() + "_index";
    }

    public String getStudyGuid() {
        return studyGuid;
    }

    public void setStudyGuid(String studyGuid) {
        this.studyGuid = studyGuid;
    }

    @Override
    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public Map<OctetString, RowParameters> getHashValues(Class<?> entityClass) {
        checkDBInitialized();
        db.checkNotClosed();
        return getMapDBHashMap(entityClass);
    }

    public List<String> getNoActionIds(Class<?> entityClass) {
        checkDBInitialized();
        db.checkNotClosed();
        Set<Fun.Tuple2<Boolean, OctetString>> keySet = db.getHashSet(getNoActionIndexSetName(entityClass));
        Map<OctetString, RowParameters> classHashes = getMapDBHashMap(entityClass);
        return keySet.stream().filter(k -> Boolean.FALSE.equals(k.a)).map(ke -> {
            RowParameters rowParameters = classHashes.get(ke.b);
            return rowParameters != null ? rowParameters.getId() : null;
        }).filter(rp -> rp != null).collect(Collectors.toList());
    }

    public void resetNoAction(Class<?> entityClass) {
        checkDBInitialized();
        db.checkNotClosed();
        Set<Fun.Tuple2<Boolean, OctetString>> keySet = db.getHashSet(getNoActionIndexSetName(entityClass));
        Map<OctetString, RowParameters> classHashes = getMapDBHashMap(entityClass);
        keySet.stream().filter(k -> Boolean.TRUE.equals(k.a)).forEach(i -> classHashes.put(i.b, RowParameters.createFromPreviousEtlItem(classHashes.get(i.b))));
    }

    public boolean hashValuesLoaded(Class<?> entityClass) {
        return db.exists(entityClass.getName());
    }

    public void removeNoActionItems(Class<?> entityClass) {
        checkDBInitialized();
        db.checkNotClosed();
        Set<Fun.Tuple2<Boolean, OctetString>> keySet = db.getHashSet(getNoActionIndexSetName(entityClass));
        Map<OctetString, RowParameters> classHashes = getMapDBHashMap(entityClass);
        keySet.stream().filter(k -> Boolean.FALSE.equals(k.a)).forEach(i -> classHashes.remove(i.b));
        resetNoAction(entityClass);
    }

    public void close() {
        if (db != null) {
            db.checkNotClosed();
            db.close();
        }
    }

    public Map<OctetString, String> findIdsByHash(Class<?> entityClass, List<OctetString> uniqueHashes) {
        checkDBInitialized();
        db.checkNotClosed();
        Map<OctetString, RowParameters> hashes = getMapDBHashMap(entityClass);
        Map<OctetString, String> res = new HashMap<>();
        uniqueHashes.forEach(hash -> {
            RowParameters rowParameters = hashes.get(hash);
            if (rowParameters != null) {
                res.put(hash, rowParameters.getId());
            }
        });
        return res;
    }

    public boolean checkEntityAction(Class<?> entityClass, OctetString hash) {
        checkDBInitialized();
        db.checkNotClosed();
        Map<OctetString, RowParameters> hashes = getMapDBHashMap(entityClass);
        RowParameters rowParameters = hashes.get(hash);
        return rowParameters != null && rowParameters.isActionPerformed();
    }

    public void cleanHashes(Class<?> entityClass) {
        checkDBInitialized();
        db.checkNotClosed();
        if (db.exists(entityClass.getName())) {
            db.delete(entityClass.getName());
        }
        if (db.exists(getNoActionIndexSetName(entityClass))) {
            db.delete(getNoActionIndexSetName(entityClass));
        }
    }
}
