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

package com.acuity.visualisations.batch.tasklet;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationDataHolder;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.batch.holders.configuration.DBStudyMappingConfigurationHolder;
import com.acuity.visualisations.batch.holders.configuration.StaticConfigurationHolder;
import com.acuity.visualisations.batch.holders.configuration.StaticConfigurationHolderImpl;
import com.acuity.visualisations.batch.holders.configuration.StudyMappingConfigurationHolder;
import com.acuity.visualisations.batch.holders.configuration.StudyRuntimeConfigurationHolder;
import com.acuity.visualisations.batch.holders.configuration.StudyRuntimeConfigurationHolderImpl;
import com.acuity.visualisations.batch.processor.DataCommonReport;
import com.acuity.visualisations.exception.ConfigurationException;
import com.acuity.visualisations.transform.entity.EntitiesRootRule;
import com.acuity.visualisations.transform.entitytotable.EntityTablesRootRule;
import com.acuity.visualisations.transform.rule.ColumnRule;
import com.acuity.visualisations.transform.rule.DataFileRule;
import com.acuity.visualisations.transform.rule.EntityRule;
import com.acuity.visualisations.transform.rule.StudyRule;
import com.acuity.visualisations.transform.table.TablesRootRule;
import com.acuity.visualisations.util.ETLConfiguration;
import com.acuity.visualisations.util.ReadConfigurationTaskletConsts;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import static com.acuity.visualisations.data.util.Util.currentDate;
import static com.acuity.visualisations.util.ConfigurationUtil.getEntitiesDescriptionRule;
import static com.acuity.visualisations.util.ConfigurationUtil.getEntityTableDescriptionRule;
import static com.acuity.visualisations.util.ConfigurationUtil.getTableDescriptionRule;

public class ReadConfigurationTasklet extends HoldersAware implements Tasklet {

	private static final String LOG_SUPPRESS = "log.suppress.entities";

	@Autowired
	private ETLConfiguration etlConfiguration;

	private String confSourceType;

	private String studyCode;

	private String projectName;

	private ConfigurationDataHolder configurationDataHolder;

	private StudyRuntimeConfigurationHolder runtimeConfiguration;

	private StaticConfigurationHolder staticConfigurationHolder;

	private StudyMappingConfigurationHolder mappingConfiguration;

	private DataCommonReport dataCommonReport;

    @Autowired
    private ApplicationContext applicationContext;

	@Override
	protected void initHolders() {
		configurationDataHolder = getConfigurationDataHolder();
		if (confSourceType.equalsIgnoreCase(ReadConfigurationTaskletConsts.DB_CONF_SOURCE_TYPE)) {
			mappingConfiguration = applicationContext.getAutowireCapableBeanFactory().createBean(DBStudyMappingConfigurationHolder.class);
		}
		configurationDataHolder.setMappingConfiguration(mappingConfiguration);
		configurationDataHolder.setRuntimeConfiguration(new StudyRuntimeConfigurationHolderImpl());
		configurationDataHolder.setStaticConfigurationHolder(new StaticConfigurationHolderImpl());
		runtimeConfiguration = getStudyRuntimeConfigurationHolder();
		staticConfigurationHolder = getStaticConfigurationHolder();
		dataCommonReport = getDataCommonReport();
	}

	@LogMeAround("Tasklet")
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

		if (!confSourceType.equalsIgnoreCase(ReadConfigurationTaskletConsts.DB_CONF_SOURCE_TYPE)) {
			throw new ConfigurationException("Unknown type of source for study configuration.");
		}

		readCommonConfiguration();

		readLoggingConfiguration();

		readStaticConfiguration();

		if (confSourceType.equalsIgnoreCase(ReadConfigurationTaskletConsts.DB_CONF_SOURCE_TYPE)) {
			readDBConfiguration();
		} else {
			throw new RuntimeException("Unknown type of source for study configuration.");
		}

		checkMappings();

		return RepeatStatus.FINISHED;
	}

	@LogMeAround("Tasklet")
	private void checkMappings() {
		ConfigurationUtil<?> configUtil = getConfigurationUtil();
		StudyRule studyRule = (StudyRule) configUtil.getStudy();
		List<DataFileRule> fileRules = studyRule.getFile();
		for (DataFileRule fileRule : fileRules) {
			List<EntityRule> entityRules = fileRule.getEntity();
			for (EntityRule entityRule : entityRules) {
				List<ColumnRule> columnRules = entityRule.getColumn();
				for (ColumnRule columnRule : columnRules) {
					String colName = columnRule.getName();
					String colDefault = columnRule.getDefault();
					if ((colName == null || colName.trim().isEmpty()) && (colDefault == null || colDefault.isEmpty())) {
						dataCommonReport.getFileReport(fileRule.getName()).addEntityFieldNotMappedError(entityRule.getName(),
								columnRule.getField());
					}
				}
			}
		}
	}

	@LogMeAround("Tasklet")
	private void readLoggingConfiguration() throws Exception {
		String propertiesFileName = "log/suppress_log.properties";
		Properties properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFileName);
			if (inputStream == null) {
				return;
			}
			properties.load(inputStream);
			String entitiesList = properties.getProperty(LOG_SUPPRESS);
			if (entitiesList != null) {
				entitiesList = entitiesList.replaceAll(" ", "");
				String[] entities = entitiesList.split(",");
				runtimeConfiguration.setSuppressLogEntities(new HashSet<String>(Arrays.asList(entities)));
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e1) {
					error("Can't close input stream for file: " + propertiesFileName);
				}
			}
		}
	}

	@LogMeAround("Tasklet")
	private void readCommonConfiguration() throws Exception {
		runtimeConfiguration.setIgnoreUnparsedFiles(etlConfiguration.getIgnoreUnparsedFiles());

		runtimeConfiguration.setStudiesConfFolder(etlConfiguration.getStudiesConfFolder());
        runtimeConfiguration.setCurrentEtlStartTime(currentDate());
	}

	@LogMeAround("Tasklet")
	private void readDBConfiguration() throws Exception {
		((DBStudyMappingConfigurationHolder) mappingConfiguration).loadConfiguration(projectName, studyCode);
	}

	@LogMeAround("Tasklet")
	private void readStaticConfiguration() throws Exception {
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		EntitiesRootRule entitiesRootRule = getEntitiesDescriptionRule();
		TablesRootRule tablesRootRule = getTableDescriptionRule();
		EntityTablesRootRule entityTablesRootRule = getEntityTableDescriptionRule();
		staticConfigurationHolder.setEntityConf(entitiesRootRule);
		staticConfigurationHolder.setTableConf(tablesRootRule);
		staticConfigurationHolder.setTableToEntityConf(entityTablesRootRule);
	}


	public void setStudyCode(String studyCode) {
		this.studyCode = studyCode;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setConfSourceType(String confSourceType) {
		this.confSourceType = confSourceType;
	}

}
