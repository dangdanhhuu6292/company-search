package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKAandeelhouder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKAandeelhouderRepository extends JpaRepository<CIKvKAandeelhouder, Integer>, CIKvKAandeelhouderRepositoryCustom{

}