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

import com.acuity.visualisations.batch.holders.configuration.ConfigurationDataHolder;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtilHolder;
import com.acuity.visualisations.batch.holders.configuration.SdtmDataHolder;
import com.acuity.visualisations.batch.processor.DataAlertReport;
import com.acuity.visualisations.batch.processor.DataCommonReport;
import com.acuity.visualisations.batch.processor.ExceptionReport;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class HolderResolver implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ConcurrentMap<Long, CntlinDataHolder> cntlinHolders = new ConcurrentHashMap<Long, CntlinDataHolder>();
    private ConcurrentMap<Long, SdtmDataHolder> sdtmSupplementalDataHolders = new ConcurrentHashMap<>();
    private ConcurrentMap<Long, ConfigurationDataHolder> configurationHolders = new ConcurrentHashMap<Long, ConfigurationDataHolder>();
    private ConcurrentMap<Long, ConfigurationUtilHolder> configurationUtilHolders = new ConcurrentHashMap<Long, ConfigurationUtilHolder>();
    private ConcurrentMap<Long, EOFEventHolder> eofEventHolders = new ConcurrentHashMap<Long, EOFEventHolder>();
    private ConcurrentMap<Long, HashValuesHolder> hashValuesHolders = new ConcurrentHashMap<Long, HashValuesHolder>();

    private ConcurrentMap<Long, DataAlertReport> dataAlertReports = new ConcurrentHashMap<Long, DataAlertReport>();

    private ConcurrentMap<Long, DataCommonReport> dataCommonReports = new ConcurrentHashMap<Long, DataCommonReport>();
    private ConcurrentMap<Long, ExceptionReport> exceptionReports = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public CntlinDataHolder getCntlinDataHolder(Long jobExecutionId) {
        if (!cntlinHolders.containsKey(jobExecutionId)) {
            cntlinHolders.put(jobExecutionId, applicationContext.getBean("cntlinHolder", CntlinDataHolder.class));
        }
        return cntlinHolders.get(jobExecutionId);
    }

    public SdtmDataHolder getSdtmSupplementalDataHolder(Long jobExecutionId) {
        if (!sdtmSupplementalDataHolders.containsKey(jobExecutionId)) {
            sdtmSupplementalDataHolders.put(jobExecutionId, applicationContext.getBean(SdtmDataHolder.class));
        }
        return sdtmSupplementalDataHolders.get(jobExecutionId);
    }

    public void removeCntlinDataHolder(Long jobExecutionId) {
        cntlinHolders.remove(jobExecutionId);
    }

    public void removeSdtmSupplementalDataHolder(Long jobExecutionId) {
        sdtmSupplementalDataHolders.remove(jobExecutionId);
    }

    public ConfigurationUtilHolder getConfigurationUtilHolder(Long jobExecutionId) {
        if (!configurationUtilHolders.containsKey(jobExecutionId)) {
            configurationUtilHolders.put(jobExecutionId,
                    applicationContext.getBean("configurationUtilHolder", ConfigurationUtilHolder.class));
        }
        return configurationUtilHolders.get(jobExecutionId);
    }

    public void removeConfigurationUtilHolder(Long jobExecutionId) {
        configurationUtilHolders.remove(jobExecutionId);
    }

    public ConfigurationDataHolder getConfigurationDataHolder(Long jobExecutionId) {
        if (!configurationHolders.containsKey(jobExecutionId)) {
            configurationHolders.put(jobExecutionId, applicationContext.getBean("configurationHolder", ConfigurationDataHolder.class));
        }
        return configurationHolders.get(jobExecutionId);
    }

    public void removeConfigurationDataHolder(Long jobExecutionId) {
        configurationHolders.remove(jobExecutionId);
    }

    public EOFEventHolder getEOFEventHolder(Long jobExecutionId) {
        if (!eofEventHolders.containsKey(jobExecutionId)) {
            eofEventHolders.put(jobExecutionId, applicationContext.getBean("eofHolder", EOFEventHolder.class));
        }
        return eofEventHolders.get(jobExecutionId);
    }

    public void removeEOFEventHolder(Long jobExecutionId) {
        eofEventHolders.remove(jobExecutionId);
    }

    public HashValuesHolder getHashValuesHolder(Long jobExecutionId) {
        if (!hashValuesHolders.containsKey(jobExecutionId)) {
            hashValuesHolders.put(jobExecutionId, applicationContext.getBean("hashHolder", HashValuesHolder.class));
        }
        return hashValuesHolders.get(jobExecutionId);
    }

    public void removeHashValuesHolder(Long jobExecutionId) {
        hashValuesHolders.remove(jobExecutionId);
    }

    public DataAlertReport getDataAlertReport(Long jobExecutionId) {
        if (!dataAlertReports.containsKey(jobExecutionId)) {
            dataAlertReports.put(jobExecutionId, applicationContext.getBean("dataAlertReport", DataAlertReport.class));
        }
        return dataAlertReports.get(jobExecutionId);
    }

    public void removeDataAlertReport(Long jobExecutionId) {
        dataAlertReports.remove(jobExecutionId);
    }

    public DataCommonReport getDataCommonReport(Long jobExecutionId) {
        if (!dataCommonReports.containsKey(jobExecutionId)) {
            dataCommonReports.put(jobExecutionId, applicationContext.getBean("dataCommonReport", DataCommonReport.class));
        }
        return dataCommonReports.get(jobExecutionId);
    }

    public void removeDataCommonReport(Long jobExecutionId) {
        dataCommonReports.remove(jobExecutionId);
    }

    public ExceptionReport getExceptionReport(Long jobExecutionId) {
        if (!exceptionReports.containsKey(jobExecutionId)) {
            exceptionReports.put(jobExecutionId, applicationContext.getBean("exceptionReport", ExceptionReport.class));
        }
        return exceptionReports.get(jobExecutionId);
    }

    public void removeExceptionReport(Long jobExecutionId) {
        exceptionReports.remove(jobExecutionId);
    }

}
