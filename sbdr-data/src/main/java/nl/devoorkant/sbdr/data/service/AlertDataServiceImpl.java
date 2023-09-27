package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Alert;
import nl.devoorkant.sbdr.data.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("alertDataService")
public class AlertDataServiceImpl implements AlertDataService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WachtwoordDataServiceImpl.class);
	@Autowired
	private AlertRepository alertRepository;

	@Override
	public Boolean checkIfAlertWithSupportIdExists(Integer sId) throws DataServiceException {
		if (sId != null) {
			try {
				List<Alert> al = alertRepository.findAlertBySupportId(sId);
				return al.size() > 0;
			} catch (Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		} else throw new DataServiceException("Parameter sId(Integer) cannot be null");
	}

	@Override
	@Transactional
	public void delete(Alert alert) throws DataServiceException {
		if (alert != null) {
			try {
				alertRepository.delete(alert);
			} catch (Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		}
	}

	@Override
	@Transactional
	public void deleteAlertBySupportId(Integer sId, Integer bId) throws DataServiceException {
		if (sId != null && bId != null) {
			try {
				Alert a = alertRepository.findAlertBySupportIdAndBedrijfId(sId, bId);
				if (a != null) delete(a);
			} catch (Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.error("Parameter sId(Integer) and bId(Integer) cannot be null");
			throw new DataServiceException("Parameter sId(Integer) and bId(Integer) cannot be null");
		}
	}

	@Override
	public Alert findAlertByAlertId(Integer alertId) throws DataServiceException {

		if (alertId != null) {
			try {
				Optional<Alert> alert = alertRepository.findById(alertId);
				return alert != null ? alert.get() : null;
			} catch (Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		} else {
			LOGGER.debug("Cannot retrieve Alert without a key.");
			return null;
		}
	}

	@Override
	public Alert findAlertBySupportIdAndBedrijfId(Integer sId, Integer bId) throws DataServiceException {
		if (sId != null && bId != null) {
			Alert a = alertRepository.findAlertBySupportIdAndBedrijfId(sId, bId);
			if (a != null) {
				return a;
			} else {
				return null;
			}
		} else {
			LOGGER.error("Parameter sId(Integer) and bId(Integer) cannot be null");
			throw new DataServiceException("Parameter sId(Integer) and bId(Integer) cannot be null");
		}
	}

	@Override
	public List<Alert> findAlertsMeantForGebruiker(Integer gId, Integer bedrijfId) throws DataServiceException {
		try {
			return alertRepository.findAlertsMeantForGebruiker(gId, bedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Alert> findAlertsOfBedrijfByBedrijfId(Integer bId) throws DataServiceException {
		try {
			return alertRepository.findAlertByBedrijfId(bId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Alert save(Alert alert) throws DataServiceException {
		Alert result;

		if (alert != null) {
			try {
				result = alertRepository.save(alert);
			} catch (Exception e) {
				throw new DataServiceException(e.getMessage());
			}

			return result;
		} else {
			LOGGER.warn("Alert is ongeldig en kan niet opgeslagen worden.");
			throw new DataServiceException("Cannot save empty alert");
		}
	}
}
