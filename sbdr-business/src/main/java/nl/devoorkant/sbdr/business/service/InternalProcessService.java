package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.BriefBatchTransfer;
import nl.devoorkant.sbdr.business.transfer.InternalProcessTransfer;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.data.model.InternalProcess;
import org.springframework.data.domain.Pageable;


public interface InternalProcessService {
	void createCustomerLetterBatchQuartzJob() throws ServiceException;

	void createNotificationLetterBatchQuartzJob() throws ServiceException;

	PageTransfer<InternalProcessTransfer> findAllNewProcessRows(Pageable p) throws ServiceException;

	InternalProcess findById(Integer internalProcessId) throws ServiceException;

	PageTransfer<BriefBatchTransfer> findNewBriefBatches(Pageable p) throws ServiceException;

	void printCustomerLetter(Integer klantId) throws ServiceException;

	void printNotificationLetter(Integer meldingId) throws ServiceException;

	InternalProcess save(InternalProcess iP) throws ServiceException;

	void setBatchAsDownloaded(Integer internalProcessId) throws ServiceException;

	void setInternalProcessRowAsSent(Integer internalProcessId, Integer gebruikerId) throws ServiceException;

	void removeInternalProcessRow(Integer internalProcessId) throws ServiceException;
}
