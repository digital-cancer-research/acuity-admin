package com.acuity.visualisations.task;

import com.acuity.visualisations.web.dao.ExtendedJobExecutionDao;
import com.acuity.visualisations.web.service.AcuityEmailNotificationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Created by knml167 on 17/06/2014.
 */
public class CheckFailedJobsTask {
    protected static final Log LOGGER = LogFactory.getLog(CheckFailedJobsTask.class);

    @Autowired
    private ExtendedJobExecutionDao customJobExecutionDao;

    @Autowired
    private AcuityEmailNotificationService acuityEmailNotificationService;

    @Scheduled(cron = "0 0 6 * * ?")
    public void run() {
        LOGGER.info("Checking failed jobs");
        List<String> failed = customJobExecutionDao.getFailedStudies();
        List<String> notRun = customJobExecutionDao.getNotRunStudies();
        if (failed.size() > 0 || notRun.size() > 0) {
            LOGGER.warn(String.format("Failed studies: %s, not run studies: %s, not cleaned studies: %s",
                    StringUtils.join(failed, ", "), StringUtils.join(notRun, ", ")));
            acuityEmailNotificationService.sendFailedStudiesEmail(failed, notRun);
        } else {
            LOGGER.info("No failed jobs found");
        }
    }
}
