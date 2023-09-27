package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.BriefBatch;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface BriefBatchDataService {

	BriefBatch findById(Integer briefBatchId) throws DataServiceException;

	Page<BriefBatch> findProcessedBriefBatches(Pageable p) throws DataServiceException;

	List<Klant> getNewCustomerLetterBatchOfDay(Date dayOfBatch) throws DataServiceException;

	List<Melding> getNewNotificationLetterBatchOfDay(Date dayOfBatch) throws DataServiceException;

	BriefBatch save(BriefBatch bB) throws DataServiceException;
}
