package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Alert;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Integer>, AlertRepositoryCustom {

}
