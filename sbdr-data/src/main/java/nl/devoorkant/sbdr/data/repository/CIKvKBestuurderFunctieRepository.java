package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKBestuurderFunctie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKBestuurderFunctieRepository extends JpaRepository<CIKvKBestuurderFunctie, Integer>, CIKvKBestuurderFunctieRepositoryCustom {

}