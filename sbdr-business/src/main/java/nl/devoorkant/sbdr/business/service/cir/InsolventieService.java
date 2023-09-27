package nl.devoorkant.sbdr.business.service.cir;

import org.springframework.data.domain.Pageable;

import nl.devoorkant.insolventie.client.CirParameters;
import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.business.transfer.FaillissementenOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.InsolventiePublicTransfer;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;

public interface InsolventieService {

	void processCir();

	CirParameters getCirParameters(String configuratieCode);

	CirParameters getCirParameters();

	PageTransfer<FaillissementenOverviewTransfer> findFaillissementenAfgelopenWeek(
			Pageable pageable) throws ServiceException;

	InsolventiePublicTransfer findPublicData() throws ServiceException;
	

}
