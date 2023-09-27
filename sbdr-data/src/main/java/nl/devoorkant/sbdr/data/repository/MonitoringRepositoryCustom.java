package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Monitoring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoringRepositoryCustom {
	@Query("SELECT m " +
			"FROM Monitoring m " +
			"WHERE m.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :bedrijfid " +
			"AND m.monitoringStatus.code = 'ACT'")
	Page<Monitoring> findActiveMonitoringOfBedrijf(@Param("bedrijfid") Integer bedrijfId, Pageable pageable);

	@Query("SELECT m " +
			"FROM Monitoring m " +
			"WHERE m.bedrijfByMonitoringDoorBedrijfId.bedrijfId = :doorbedrijfid " +
			"AND m.bedrijfByMonitoringVanBedrijfId.bedrijfId = :vanbedrijfid " +
			"AND  m.monitoringStatus.code = 'ACT'")
	List<Monitoring> findActiveMonitoringOfBedrijf(@Param("doorbedrijfid") Integer doorBedrijfId, @Param("vanbedrijfid") Integer vanBedrijfId);

	@Query("FROM Monitoring " +
			"WHERE referentieNummerIntern = :referentie")
	List<Monitoring> findByReferentieNummerIntern(@Param("referentie") String referentie);

	@Query("FROM Monitoring " +
			"WHERE bedrijfByMonitoringDoorBedrijfId.bedrijfId = :monitoringBedr " +
			"AND monitoringStatus.code = 'ACT'")
	List<Monitoring> findMonitorsOfBedrijf(@Param("monitoringBedr") Integer monitorDoorBedrijfId);

}
