package nl.devoorkant.sbdr.business.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.service.SupportService;

public class AssignExpiredObjectionsToAdminJob extends QuartzJobBean {
	private static final Logger logger = LoggerFactory.getLogger(AssignExpiredObjectionsToAdminJob.class);
	
	@Autowired
	SupportService supportService;
	
	@Override
	public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

		try {
			supportService.assignExpiredObjectionsToAdmin();
		} catch (ServiceException e) {
			throw new JobExecutionException(e);
		}			
	}
}
