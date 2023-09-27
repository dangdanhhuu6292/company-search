package nl.devoorkant.exactonline.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import nl.devoorkant.sbdr.business.service.ExactOnlineService;
import nl.devoorkant.sbdr.spring.ZbrApp;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ZbrApp.class)
//@WebAppConfiguration
public class EoTest {
	
	@Autowired
	ExactOnlineService exactOnlineService;
	
	@Test
	public void testProcessExactOnline() {
		exactOnlineService.processExactOnline();
	}

	
}
