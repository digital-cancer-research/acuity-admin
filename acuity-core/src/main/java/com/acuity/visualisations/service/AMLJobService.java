package com.acuity.visualisations.service;

import com.acuity.visualisations.util.ETLStudyRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.acuity.visualisations.util.JobLauncherConsts.AML_GROUP_NAME;
import static com.acuity.visualisations.util.JobLauncherConsts.AML_JOB_NAME;

@Service
@ConditionalOnProperty(prefix = "azureml", name = "enable")
public class AMLJobService extends AbstractJobService implements ApplicationContextAware {
    protected static final Log LOGGER = LogFactory.getLog(AMLJobService.class);

    private static final String CRON_DISABLED_EXPRESSION = "0 0 0 1 1 ? 2099";

    @PostConstruct
    public void init() {
        LOGGER.debug("AMLJobService " + this.toString());
    }

    @Override
    String getGroupName() {
        return AML_GROUP_NAME;
    }

    @Override
    String getJobInstanceName() {
        return AML_JOB_NAME;
    }

    @Override
    void populateJobDetailFactoryBeanWithProperties(ETLStudyRule studyRule, Map<String, Object> jobDataAsMap) {
        jobDataAsMap.put("etl.cronExpression", CRON_DISABLED_EXPRESSION);
    }
}
