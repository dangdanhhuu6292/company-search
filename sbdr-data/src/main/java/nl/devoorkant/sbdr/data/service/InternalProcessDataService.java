package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.InternalProcess;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface InternalProcessDataService {
	List<Klant> findAllCustomersOfBatch(Integer batchId) throws DataServiceException;

	Page<InternalProcess> findAllNewProcessRows(Pageable p) throws DataServiceException;

	List<Melding> findAllNotificationsOfBatch(Integer batchId) throws DataServiceException;

	InternalProcess findByBriefBatchId(Integer batchId) throws DataServiceException;

	InternalProcess findById(Integer internalProcessId) throws DataServiceException;

	Klant findKlantOfInternalProcess(Integer internalProcessId) throws DataServiceException;

	Melding findMeldingOfInternalProcess(Integer internalProcessId) throws DataServiceException;

	void delete(InternalProcess internalProcess) throws DataServiceException;	
	//void removeInternalProcessRow(Integer internalProcessId) throws DataServiceException;

	InternalProcess save(InternalProcess iP) throws DataServiceException;
}
