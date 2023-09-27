package nl.devoorkant.sbdr.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import nl.devoorkant.sbdr.business.job.AssignExpiredObjectionsToAdminJob;
import nl.devoorkant.sbdr.business.job.RemoveInvalidIpLoginAttemptsIpJob;
import nl.devoorkant.sbdr.business.transfer.LoginAllowed;
import nl.devoorkant.sbdr.business.util.LoginAttempt;
import nl.devoorkant.sbdr.business.util.LoginAttemptsTuple;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ipAddressBlockingService")
@Transactional(readOnly = true)
public class IpAddressBlockingServiceImpl implements IpAddressBlockingService {
	
	@Autowired
	Scheduler scheduler;
	
	@Value("${job.cron.remove_invalid_login_attempts_ip}")
	String cronExpressionRemoveInvalidLoginAttemptsIp;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressBlockingServiceImpl.class);
	
	@PostConstruct
	public void createSchedule() {        
        JobDetail jobDetail = buildJobDetail();
        Trigger trigger = buildJobTrigger(jobDetail);
        try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			LOGGER.error("Cannot start ipAddressBlocking schedule RemoveInvalidLoginAttemptsIp", e);
		}        
	}
	
	private JobDetail buildJobDetail() {

        return JobBuilder.newJob(RemoveInvalidIpLoginAttemptsIpJob.class)
                .withIdentity(UUID.randomUUID().toString(), "ipaddressblocking-jobs")
                .withDescription("Remove invalid login attempts IP")
                .storeDurably()
                .build();
    }	

    private Trigger buildJobTrigger(JobDetail jobDetail) {
    	return TriggerBuilder.newTrigger()
    			  .forJob(jobDetail)
    			  .withIdentity(jobDetail.getKey().getName(), "ipaddressblocking")
    			  .withSchedule(CronScheduleBuilder.cronSchedule(cronExpressionRemoveInvalidLoginAttemptsIp))
    			  .build();    	
    }  
    
	HashMap<String, LoginAttemptsTuple> loginAttemptsIp = new HashMap<String, LoginAttemptsTuple>();
	/* Expires in x minutes */
	long expiresInMinutes = LoginAllowed.MAX_BLOCKING_MINUTES;	
	
	private LoginAttemptsTuple getLoginAttemptsTuple(String ipaddress) {
		return loginAttemptsIp.get(ipaddress);
	}
	
	private boolean isloginAttemptsIpExpired(LoginAttemptsTuple loginAttemptsIp) {
		long expires = loginAttemptsIp.getBlockExpires();
		return expires < System.currentTimeMillis();

	}
	
	@Override
	public boolean isIpAllowedToLogin(String ipaddress) {
		boolean result = true;
		
		LoginAttemptsTuple loginAttemptsTuple = getLoginAttemptsTuple(ipaddress);
		
		if (loginAttemptsTuple != null) {
			if (isloginAttemptsIpExpired(loginAttemptsTuple)) {
				removeLoginAttemptsIp(ipaddress);
			} else {	
				if (loginAttemptsTuple.getNrloginAttempts() >= LoginAllowed.MAX_LOGINATTEMPTS)
					result = false;
			}
		}
		
		return result;
	}
	
	@Override
	public int getNrOfLoginAttempts(String ipaddress) {
		LoginAttemptsTuple loginAttemptsTuple = getLoginAttemptsTuple(ipaddress);
		
		if (loginAttemptsTuple != null) 
			return loginAttemptsTuple.getNrloginAttempts();
		else
			return 0;
	}
	
	@Override
	public void addLoginAttempt(String ipaddress, String username, String password, Date datetime) {
		LoginAttemptsTuple loginAttemptsTuple = getLoginAttemptsTuple(ipaddress);
		
		/* Expires in x minutes */
		long expires = System.currentTimeMillis() + 1000L * 60 * expiresInMinutes;	
		
		LOGGER.info("LoginAttempt from " + ipaddress + " username: " + username);
		if (loginAttemptsTuple == null) {			
			loginAttemptsTuple = new LoginAttemptsTuple(new LoginAttempt(username, password, datetime), expires);
		} else {
			loginAttemptsTuple.addLoginAttempt(username, password, datetime);
			loginAttemptsTuple.setBlockExpires(expires);
		}
		
		loginAttemptsIp.put(ipaddress, loginAttemptsTuple);
	}
	
	@Override
	public void removeLoginAttemptsIp(String ipaddress) {
		
		if (ipaddress != null) {
			LoginAttemptsTuple loginAttemptsTuple = loginAttemptsIp.get(ipaddress);
			
			if (loginAttemptsTuple != null) {				
				if (loginAttemptsIp.remove(ipaddress) != null)
					LOGGER.info("Removed loginAttemptsTuple: " + ipaddress);
				else
					LOGGER.warn("Could not remove loginAttemptsTuple. Probably already invalidated: " + ipaddress);
			}
			else
				LOGGER.warn("Could not find loginAttemptsTuple. Probably already invalidated: " + ipaddress);
		}
	}
	
	@Override
	public void removeInvalidLoginAttemptsIp() {
		Set<Entry<String, LoginAttemptsTuple>> loginAttemptsIpSet = loginAttemptsIp.entrySet();
		List<String> toremove = new ArrayList<String>();
		
		for(Entry<String, LoginAttemptsTuple> entry : loginAttemptsIpSet) {
			// if expired remove from loginAttemptsIp
			if (isloginAttemptsIpExpired(entry.getValue())) {				
				toremove.add(entry.getKey());
			}
		}
		
		// remove obsolete loginAttemptsIp
		if (toremove.size() > 0)
			for(String key: toremove) {
				loginAttemptsIp.remove(key);
			}
	}

}
