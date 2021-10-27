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

package com.acuity.visualisations.batch.reader;

import com.acuity.visualisations.batch.holders.EOFEventHolder;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.holders.configuration.StudyRuntimeConfigurationHolder;
import com.acuity.visualisations.batch.processor.DataCommonReport;
import com.acuity.visualisations.batch.processor.ExceptionReport;
import com.acuity.visualisations.model.input.InputModelChunk;
import com.acuity.visualisations.service.IExecutionProfiler;
import com.acuity.visualisations.transform.rule.DataFileRule;
import com.acuity.visualisations.transform.rule.StudyRule;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.util.ExecutionContextUserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("multiResourceReader")
@Scope("step")
public class SmbSasMultiResourceItemReader extends SkippedFilesAndHoldersAware implements ItemReader<InputModelChunk>, ItemStream {

    private ConfigurationUtil<?> configurationUtil;

    private StudyRuntimeConfigurationHolder runtimeConfiguration;

    private EOFEventHolder eofEventHolder;

    private static final String RESOURCE_KEY = "resourceIndex";

    private final ExecutionContextUserSupport executionContextUserSupport = new ExecutionContextUserSupport();

    @Autowired
    private ResourceInfoAwareItemReader delegate;

    @Autowired
    private IExecutionProfiler executionProfiler;

    private DataCommonReport report;
    private ExceptionReport exceptionReport;

    private ResourceInfo[] resources;

    private boolean saveState = true;

    private int currentResource = -1;

    // signals there are no resources to read -> just return null on first read
    private boolean noInput;

    private boolean strict = false;

    @Override
    protected void initHolders() {
        super.initHolders();
        eofEventHolder = getEOFEventHolder();
        configurationUtil = getConfigurationUtil();
        runtimeConfiguration = getStudyRuntimeConfigurationHolder();
        report = getDataCommonReport();
        exceptionReport = getExceptionReport();
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public SmbSasMultiResourceItemReader() {
        executionContextUserSupport.setName(ClassUtils.getShortName(SmbSasMultiResourceItemReader.class));
    }

    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            List<ResourceInfo> allResources = new ArrayList<>();
            executionProfiler.startOperation(getJobExecutionId(), "getSourceFiles");

            List<DataFileRule> dataFileRules = ((StudyRule) configurationUtil.getStudy()).getFile();

            for (DataFileRule dataFileRule : dataFileRules) {
                allResources.add(new ResourceInfoFile(dataFileRule.getName(), dataFileRule.getStandard()));
            }
            executionProfiler.stopOperation(getJobExecutionId(), "getSourceFiles");
            resources = allResources.toArray(new ResourceInfo[allResources.size()]);
            for (ResourceInfo info : resources) {
                eofEventHolder.addFileRule(info.toString(), configurationUtil.getEntities(info.toString()));
            }
        } catch (Exception e) {
            throw new ItemStreamException(e);
        }

        noInput = false;
        if (resources.length == 0) {
            if (strict) {
                throw new IllegalStateException("No resources to read. Set strict=false if this is not an error condition.");
            } else {
                warn("No resources to read. Set strict=true if this should be an error condition.");
                noInput = true;
                return;
            }
        }

        if (executionContext.containsKey(executionContextUserSupport.getKey(RESOURCE_KEY))) {
            currentResource = executionContext.getInt(executionContextUserSupport.getKey(RESOURCE_KEY));

            // context could have been saved before reading anything
            if (currentResource == -1) {
                currentResource = 0;
            }

            delegate.setResourceInfo(resources[currentResource]);
            try {
                invokeOpenUtilTrue();
            } catch (IOException e) {
                throw new ItemStreamException("Unable to open file: " + resources[currentResource].toString(), e);
            }
        } else {
            currentResource = -1;
        }
    }


    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (saveState) {
            executionContext.putInt(executionContextUserSupport.getKey(RESOURCE_KEY), currentResource);
            delegate.update(executionContext);
        }
    }

    public void close() throws ItemStreamException {
        delegate.close();
        noInput = false;
    }

    public InputModelChunk read() throws Exception {
        if (noInput) {
            return null;
        }

        // If there is no resource, then this is the first item, set the current
        // resource to 0 and open the first delegate.
        if (currentResource == -1) {
            currentResource = 0;
            delegate.setResourceInfo(resources[currentResource]);
            if (!invokeOpenUtilTrue()) {
                return null;
            }
        }

        return readNextItem();
    }

    private InputModelChunk readNextItem() throws Exception {

        InputModelChunk item = delegate.read();

        while (item == null) {

            currentResource++;

            if (currentResource >= resources.length) {
                return null;
            }

            delegate.close();
            delegate.setResourceInfo(resources[currentResource]);
            if (!invokeOpenUtilTrue()) {
                return null;
            }

            item = delegate.read();
        }

        return item;
    }

    private boolean invokeOpenUtilTrue() throws IOException {
        while (!invokeOpen()) {
            currentResource++;

            if (currentResource >= resources.length) {
                return false;
            }

            delegate.setResourceInfo(resources[currentResource]);
        }
        return true;
    }

    private boolean invokeOpen() throws IOException {
        try {
            delegate.open(new ExecutionContext());
        } catch (ItemStreamException e) {
            exceptionReport.addException("read", e);
            ResourceInfo resourceInfo = resources[currentResource];
            if (resourceInfo instanceof ResourceInfoFile) {
                String pathToResource = resourceInfo.toString();

                // This will set the number of subjects in the file to be zero so that it will be
                // flagged as RED in the report
                if (!new File(pathToResource).exists()) {
                    report.getFileReport(pathToResource);
                }

                warn("Unable to parse file " + pathToResource);
                if (runtimeConfiguration.isIgnoreUnparsedFiles()) {
                    addSkippedItem(pathToResource);
                    warn("Unable to parse file " + pathToResource + ". No additional tool is provided, so file is skipped");
                    return false;
                } else {
                    throw new ItemStreamException(
                            String.format(
                                    "Job Execution Id: %s, Job Name: %s, Project: %s, Study: %s. Unable to parse file %s.\nCause: %s",
                                    getJobExecutionId(), getJobName(), getProjectName(), getStudyName(), pathToResource, e.getCause()));
                }
            }
        }
        return true;
    }

    public void setDelegate(ResourceInfoAwareItemReader delegate) {
        this.delegate = delegate;
    }

    public void setSaveState(boolean saveState) {
        this.saveState = saveState;
    }

    public ResourceInfo getCurrentResource() {
        if (currentResource >= resources.length || currentResource < 0) {
            return null;
        }
        return resources[currentResource];
    }

}
