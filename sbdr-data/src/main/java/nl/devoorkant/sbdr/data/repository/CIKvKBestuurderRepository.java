package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKBestuurder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKBestuurderRepository extends JpaRepository<CIKvKBestuurder, Integer>, CIKvKBestuurderRepositoryCustom{

}