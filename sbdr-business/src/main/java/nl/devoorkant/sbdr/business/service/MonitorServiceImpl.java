package nl.devoorkant.sbdr.business.service;


import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Monitoring;
import nl.devoorkant.sbdr.data.service.MonitoringDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("monitorService")
@Transactional(readOnly = true)
public class MonitorServiceImpl implements MonitorService {
	@Autowired
	private MonitoringDataService monitoringDataService;

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorService.class);

	@Override
	public boolean bedrijfHasMonitor(Integer doorBedrijfId, Integer overBedrijfId) throws ServiceException {
		if(doorBedrijfId!=null && overBedrijfId!=null){
			try{
				Monitoring bestaandeMonitoring = monitoringDataService.findActiveMonitoringOfBedrijfByBedrijf(doorBedrijfId, overBedrijfId);

				return bestaandeMonitoring!=null;
			} catch(DataServiceException e){
				LOGGER.error("Error in bedrijfHasMonitor: " + e.getLocalizedMessage());
				throw new ServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("Method bedrijfHasMonitor: parameters are null");
			throw new ServiceException(new ErrorService(ErrorService.PARAMETER_IS_EMPTY).getErrorMsg());
		}
	}
}
