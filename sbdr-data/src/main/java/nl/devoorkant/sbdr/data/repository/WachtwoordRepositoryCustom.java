package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Wachtwoord;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WachtwoordRepositoryCustom {
	@Query("FROM Wachtwoord WHERE wachtwoordStatus.code = 'RES'")
	List<Wachtwoord> findByStatusReset();
}
