package nl.devoorkant.sbdr.business.service;

import java.util.List;

import nl.devoorkant.sbdr.business.transfer.NotificationPublicTransfer;

public interface MeldingService {
	boolean meldingHasObjection(Integer meldingId) throws ServiceException;
	
	List<NotificationPublicTransfer> findNotificationsPublicData(Integer nrOfDays) throws ServiceException;
}
