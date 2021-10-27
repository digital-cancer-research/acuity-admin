package com.acuity.visualisations.batch.tasklet;

import com.acuity.visualisations.aspect.LogMeAround;
import com.acuity.visualisations.batch.holders.HoldersAware;
import com.acuity.visualisations.batch.holders.configuration.ConfigurationUtil;
import com.acuity.visualisations.dal.EntityManager;
import com.acuity.visualisations.service.IExecutionProfiler;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("cleanupTasklet")
@Scope("step")
public class CleanupTasklet extends HoldersAware implements Tasklet {

	private ConfigurationUtil<?> configurationUtil;

	@Autowired
	private PostProcessUtils postProcessUtils;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private IExecutionProfiler executionProfiler;

	@Override
	protected void initHolders() {
		configurationUtil = getConfigurationUtil();
	}

	@Override
	@LogMeAround("Tasklet")
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        getHashValuesHolder().close();
		removeAllHolders();
		return RepeatStatus.FINISHED;
	}
}
