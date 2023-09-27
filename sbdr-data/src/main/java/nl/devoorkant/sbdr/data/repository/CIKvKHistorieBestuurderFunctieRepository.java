package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKHistorieBestuurderFunctie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKHistorieBestuurderFunctieRepository extends JpaRepository<CIKvKHistorieBestuurderFunctie, Integer>, CIKvKHistorieBestuurderRepositoryCustom {
}
