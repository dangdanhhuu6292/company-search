package nl.devoorkant.sbdr.business.service;

public interface MonitorService {
	boolean bedrijfHasMonitor(Integer doorBedrijfId, Integer overBedrijfId) throws ServiceException;
}