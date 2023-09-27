package nl.devoorkant.sbdr.cir.data.repository;

import java.util.Date;
import java.util.List;

import nl.devoorkant.sbdr.cir.data.model.CirInsolventie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CirInsolventieRepositoryCustom {

	@Query("SELECT i.id, p, h FROM CirCirPublicatie p JOIN p.cirInsolventie i JOIN i.cirPersoon h WHERE p.datumPublicatie >= :fromDate AND p.cirPublicatiesoort.code in ('1300', '1301', '1302', '1303', '1305', '1306') GROUP BY h.naam, h.nummerKvK ORDER BY p.datumPublicatie DESC, h.naam")
	Page<Object[]> findAantalNieuweFaillissementen(@Param("fromDate") Date fromDate, Pageable pageable);

	@Query("FROM CirCirInsolventie WHERE nummerInsolventie = :nummerInsolventie")
	List<CirInsolventie> findByNummerInsolventie(@Param("nummerInsolventie") String nummerInsolventie);

	@Query("SELECT COUNT(*) FROM CirCirPublicatie AS c WHERE c.cirPublicatiesoort.code IN ('1300', '1301', '1302', '1303', '1305', '1306') AND c.datumPublicatie >= :date")
	long findFaillissementenAfterDate(@Param("date") Date date);

	@Query("SELECT COUNT(*) FROM CirCirPublicatie AS c WHERE c.cirPublicatiesoort.code IN ('2306', '3100', '330', '3301', '3302', '3303') AND c.datumPublicatie >= :date")
	long findSurseancesAfterDate(@Param("date") Date date);

	//@Query("SELECT 100 as nrOf")
}
