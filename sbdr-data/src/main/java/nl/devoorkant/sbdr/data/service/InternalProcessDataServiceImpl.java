package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Alert;
import nl.devoorkant.sbdr.data.model.InternalProcess;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.repository.BriefBatchRepository;
import nl.devoorkant.sbdr.data.repository.InternalProcessRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.Optional;


@Service("internalProcessDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class InternalProcessDataServiceImpl implements InternalProcessDataService {
	@Autowired
	private BriefBatchRepository briefBatchRepository;
	@Autowired
	private InternalProcessRepository internalProcessRepository;

	private static Logger LOGGER = LoggerFactory.getLogger(InternalProcessDataServiceImpl.class);
	
	@Override
	public List<Klant> findAllCustomersOfBatch(Integer batchId) throws DataServiceException {
		try {
			return briefBatchRepository.findCustomersOfBatch(batchId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<InternalProcess> findAllNewProcessRows(Pageable p) throws DataServiceException {
		try {
			return internalProcessRepository.findPageOfNewProcessRows(p);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findAllNotificationsOfBatch(Integer batchId) throws DataServiceException {
		try {
			return briefBatchRepository.findNotificationsOfBatch(batchId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public InternalProcess findByBriefBatchId(Integer batchId) throws DataServiceException {
		try {
			return briefBatchRepository.findByBriefBatchId(batchId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public InternalProcess findById(Integer internalProcessId) throws DataServiceException {
		try {
			Optional<InternalProcess> internalProcess = internalProcessRepository.findById(internalProcessId);
			return internalProcess != null ? internalProcess.get() : null;
		} catch(Exception e) {			
			LOGGER.error("InternalProcess " + internalProcessId + " could not be found: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public InternalProcess save(InternalProcess iP) throws DataServiceException {
		try {
			return internalProcessRepository.save(iP);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Melding findMeldingOfInternalProcess(Integer internalProcessId) throws DataServiceException {
		try {
			return internalProcessRepository.findMeldingOfInternalProcess(internalProcessId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Klant findKlantOfInternalProcess(Integer internalProcessId) throws DataServiceException {
		try {
			return internalProcessRepository.findKlantOfInternalProcess(internalProcessId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void delete(InternalProcess internalProcess) throws DataServiceException {
		if(internalProcess != null) {
			try {
				internalProcessRepository.delete(internalProcess);
			} catch(Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		}
	}
	
//	@Override
//	public void removeInternalProcessRow(Integer internalProcessId) throws DataServiceException {
//		try {
//			InternalProcess iP = findById(internalProcessId);
//			internalProcessRepository.delete(iP);
//		} catch(Exception e) {
//			throw new DataServiceException(e.getMessage());
//		}
//	}
}
