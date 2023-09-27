package nl.devoorkant.sbdr.business.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import nl.devoorkant.sbdr.business.service.CompanyAccountService;
import nl.devoorkant.sbdr.business.service.ServiceException;

public class DeleteOverdueKlantenJob extends QuartzJobBean {
	private static final Logger logger = LoggerFactory.getLogger(DeleteOverdueKlantenJob.class);
	
	@Autowired
	CompanyAccountService companyAccountService;
	
	@Override
	public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.info("Executing Job with key {}", jobExecutionContext.getJobDetail().getKey());

		try {
			companyAccountService.deleteOverdueKlanten();
		} catch (ServiceException e) {
			throw new JobExecutionException(e);
		}			
	}
}
