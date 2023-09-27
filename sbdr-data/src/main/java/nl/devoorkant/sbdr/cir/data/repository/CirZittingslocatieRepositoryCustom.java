package nl.devoorkant.sbdr.cir.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirZittingslocatie;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirZittingslocatieRepositoryCustom {
	
	@Query("SELECT czl FROM CirCirZittingslocatie czl WHERE czl.plaats = :plaats AND czl.straat = :straat AND czl.huisNummer = :huisNummer")
	List<CirZittingslocatie> findByPlaatsStraatHuisnummer(@Param("plaats") String plaats, @Param("straat") String straat, @Param("huisNummer") String huisNummer);

	
}
