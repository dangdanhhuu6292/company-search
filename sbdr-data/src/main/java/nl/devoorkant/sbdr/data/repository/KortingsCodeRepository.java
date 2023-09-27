package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.KortingsCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KortingsCodeRepository extends JpaRepository<KortingsCode, Integer>, KortingsCodeRepositoryCustom {

}