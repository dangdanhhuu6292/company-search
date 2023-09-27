package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.BriefBatch;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.repository.BriefBatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("briefBatchDataService")
public class BriefBatchDataServiceImpl implements BriefBatchDataService {

	@Autowired
	private BriefBatchRepository briefBatchRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(BriefBatchDataServiceImpl.class);

	@Override
	public BriefBatch findById(Integer briefBatchId) throws DataServiceException {
		try {
			Optional<BriefBatch> briefBatch = briefBatchRepository.findById(briefBatchId);
			return briefBatch != null ? briefBatch.get() : null;
		} catch(Exception e) {
			LOGGER.error("Method findById: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	public Page<BriefBatch> findProcessedBriefBatches(Pageable p) throws DataServiceException {
		try {
			return briefBatchRepository.findPageOfProcessedBriefBatches(p);
		} catch(Exception e) {
			LOGGER.error("Method findProcessedBriefBatches: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Klant> getNewCustomerLetterBatchOfDay(Date dayOfBatch) throws DataServiceException {
		try {
			Calendar nextDay = Calendar.getInstance();
			nextDay.setTime(dayOfBatch);
			nextDay.add(Calendar.HOUR, 24);
			return briefBatchRepository.getNewCustomerLetterBatch(dayOfBatch, nextDay.getTime());
		} catch(Exception e) {
			LOGGER.error("Method getNewCustomerLetterBatch: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> getNewNotificationLetterBatchOfDay(Date dayOfBatch) throws DataServiceException {
		try {
			Calendar nextDay = Calendar.getInstance();
			nextDay.setTime(dayOfBatch);
			nextDay.add(Calendar.HOUR, 24);
			return briefBatchRepository.getNewNotificationLetterBatch(dayOfBatch, nextDay.getTime());
		} catch(Exception e) {
			LOGGER.error("Method getNewNotificationLetterBatch: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public BriefBatch save(BriefBatch bB) throws DataServiceException {
		try {
			return briefBatchRepository.save(bB);
		} catch(Exception e) {
			LOGGER.error("Method save: " + e.getMessage());
			throw new DataServiceException(e.getMessage());
		}
	}
}
