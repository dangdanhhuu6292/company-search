package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.data.model.Gebruiker;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import nl.devoorkant.sbdr.data.service.GebruikerDataService;
import nl.devoorkant.sbdr.business.data.HelloWorldData;
import nl.devoorkant.sbdr.business.wrapper.HelloResponseWrapper;

//import org.activiti.engine.HistoryService;
//import org.activiti.engine.ProcessEngine;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.TaskService;
//import org.activiti.engine.history.HistoricProcessInstance;
//import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service("testSbdrService")
@Transactional(readOnly = true)
public class TestSbdrServiceImpl implements TestSbdrService {
	private static Logger LOGGER = LoggerFactory.getLogger(TestSbdrServiceImpl.class);
	
	//@Autowired
	//private ProcessEngine processEngine;
	
	//@Autowired
	//private RuntimeService runtimeService;
	
	@Autowired
	private HelloWorldData helloWorldData;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private GebruikerDataService gebruikerDataService;
	
	  @Transactional
	  public HelloResponseWrapper hello() {
		HelloResponseWrapper responseWrapper = new HelloResponseWrapper();  
		  
	    try
	    {
		    responseWrapper.setStatus("init");
		    responseWrapper.setInformation("initializing process Hello");
			
	    	// set dummy values in helloWorldData
			helloWorldData.setSender("info@bkrconnect.nl");
			helloWorldData.setRecipient("mbruinenberg@planet.nl");  
			helloWorldData.setBody("Dit is een test email via Activiti HelloProcess.");  
			  
		    // here you can do transactional stuff in your domain model
		    // and it will be combined in the same transaction as 
		    // the startProcessInstanceByKey to the Activiti RuntimeService
			//String procId = runtimeService.startProcessInstanceByKey("helloProcess").getId();
			
			LOGGER.info("HelloProcess " ); // + procId + " started 2.");
			
			Gebruiker gebruiker = gebruikerDataService.findByGebruikersnaam("admin");
			if (gebruiker != null)
			{
				java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
				
				Wachtwoord wachtwoord = gebruiker.getWachtwoord();
				wachtwoord.setDatumLaatsteWijziging(sqlDate);
				wachtwoord.setWachtwoord(passwordEncoder.encode("admin2"));
				gebruikerDataService.save(gebruiker);
				LOGGER.info("admin user updated.");
			}
			else
				LOGGER.info("No admin user found.");
			gebruiker = gebruikerDataService.findByGebruikersnaam("user");
			if (gebruiker != null)
			{
				java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
				
				Wachtwoord wachtwoord = gebruiker.getWachtwoord();
				wachtwoord.setDatumLaatsteWijziging(sqlDate);
				wachtwoord.setWachtwoord(passwordEncoder.encode("user"));
				
				gebruikerDataService.save(gebruiker);
				LOGGER.info("user user updated.");
			}
			else
				LOGGER.info("No user user found.");
		    			
		    // Now check for tasks for user 'martijn' since 'martijn' is a 'writer'
		    //TaskService taskService = processEngine.getTaskService();
		    //List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("writer").list();
		    
		    //int nr = tasks != null ? tasks.size() : 0;
		    //LOGGER.info("Nr of writer tasks: " + nr);
		    
		    responseWrapper.setStatus("searching");
		    responseWrapper.setInformation("searching for something to do");
//		    for (Task task : tasks)
//		    {
//		    	LOGGER.info("Task: " + task.getId() + " catagory: " + task.getCategory());
//		    	// Only claim the writeHelloWorld task
//		    	if (task.getId() != null && task.getTaskDefinitionKey().equals("writeHelloWorld"))
//		    	{
//		    	    responseWrapper.setStatus("claiming");
//		    	    responseWrapper.setInformation("found something to do");
//	
//		    	    taskService.claim(task.getId(), "martijn");
//		    		LOGGER.info("write hello world task: " + task.getId() + " claimed");
//		    		
//		    	    responseWrapper.setStatus("working");
//		    	    responseWrapper.setInformation("doing something");
//	
//		    	    // work on it
//		    		LOGGER.info("working on write hello world task: " + task.getId());
//		    		
//		    		// done..
//		    		taskService.complete(task.getId());
//		    		LOGGER.info("completed hello world task: " + task.getId());
//	
//		    	    responseWrapper.setStatus("completed");
//		    	    responseWrapper.setInformation("done something");
//		    	}
//		    }
	
		    
		    // verify that the process is actually finished
		    //HistoryService historyService = processEngine.getHistoryService();
		    //HistoricProcessInstance historicProcessInstance = 
		    //  historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
		    //LOGGER.info("Write Hello World Process instance end time: " + historicProcessInstance.getEndTime());
		    
		    responseWrapper.setStatus("finished");
		    responseWrapper.setInformation("finished doing something");
	    	
			return responseWrapper;
	    }
	    catch (Exception e)
	    {
	    	LOGGER.error("Something went wrong: " + e.getMessage());
	    	
	    	return responseWrapper;
	    }
	    
	    
	  }
	  
//	  public void setRuntimeService(RuntimeService runtimeService) {
//	    this.runtimeService = runtimeService;
//	  }	
}
