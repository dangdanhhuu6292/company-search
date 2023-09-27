package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKHistorieAandeelhouder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKHistorieAandeelhouderRepository extends JpaRepository<CIKvKHistorieAandeelhouder, Integer>, CIKvKHistorieAandeelhouderRepositoryCustom {
}
