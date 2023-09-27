package nl.devoorkant.sbdr.data.service;

import java.util.List;
import java.util.Optional;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Monitoring;
import nl.devoorkant.sbdr.data.repository.MonitoringRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("monitoringDataService")
public class MonitoringDataServiceImpl implements MonitoringDataService {

	@Autowired
	private MonitoringRepository monitoringRepository;

	@Override
	public Monitoring findById(Integer monitoringId) throws DataServiceException {
		try {
			Optional<Monitoring> monitoring = monitoringRepository.findById(monitoringId);
			return monitoring != null ? monitoring.get() : null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Page<Monitoring> findActiveMonitoringOfBedrijf(Integer bedrijfId, Pageable pageable) throws DataServiceException {
		Page<Monitoring> result = null;

		try {
			if(bedrijfId != null) {
				result = monitoringRepository.findActiveMonitoringOfBedrijf(bedrijfId, pageable);
			}

			return result;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Monitoring save(Monitoring monitoring) throws DataServiceException {
		try {
			return monitoringRepository.save(monitoring);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Monitoring findActiveMonitoringOfBedrijfByBedrijf(Integer doorBedrijfId, Integer vanBedrijfId) throws DataServiceException {
		try {
			List<Monitoring> monitors = monitoringRepository.findActiveMonitoringOfBedrijf(doorBedrijfId, vanBedrijfId);

			if(monitors != null && monitors.size() == 1) return monitors.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Monitoring findByReferentieNummerIntern(String referentie) throws DataServiceException {
		try {
			List<Monitoring> monitorings = monitoringRepository.findByReferentieNummerIntern(referentie);

			if(monitorings != null && monitorings.size() == 1) return monitorings.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Monitoring> findMonitorsOfBedrijf(Integer bedrId) throws DataServiceException {
		try {
			return monitoringRepository.findMonitorsOfBedrijf(bedrId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

}
