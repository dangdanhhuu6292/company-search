package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.NotificationPublicTransfer;
import nl.devoorkant.sbdr.business.util.ConvertUtil;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.service.MeldingDataService;
import nl.devoorkant.sbdr.data.service.SupportDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("meldingService")
@Transactional(readOnly = true)
public class MeldingServiceImpl implements  MeldingService{

	@Autowired
	private SupportDataService supportDataService;
	
	@Autowired
	private MeldingDataService meldingDataService;

	@Override
	public boolean meldingHasObjection(Integer meldingId) throws ServiceException{
		List<Object[]> startEndDatesSupport = null;
		boolean supportInProcess = false;

		try{
			startEndDatesSupport= supportDataService.findStartAndEndOfSupportTicketChainByMeldingId(meldingId);
		} catch(DataServiceException e){
			throw new ServiceException(e.getMessage());
		}

		if(startEndDatesSupport != null && startEndDatesSupport.size() > 0) {
			Object[] dates = startEndDatesSupport.get(0);

			if(dates[0] != null && dates[1] != null) {
				supportInProcess = true;
			}
		}

		return supportInProcess;
	}
	
	@Override
	public List<NotificationPublicTransfer> findNotificationsPublicData(Integer nrOfDays) throws ServiceException {		
		List<Melding> meldingen = null;
		Date fromDate = null;
		
		try{
			Calendar c = Calendar.getInstance();
			if (nrOfDays != null)
				c.add(Calendar.DATE, -1 * nrOfDays.intValue());  // number of days to add
			else
				c.add(Calendar.DATE, -30);  // default number of days to add
			fromDate = c.getTime(); 
			meldingen = meldingDataService.findAllFromDate(fromDate);
		} catch(DataServiceException e){
			throw new ServiceException(e.getMessage());
		}	
		
		return ConvertUtil.convertMeldingenToNotificationsPublicTransfer(fromDate, meldingen);
	}
}
