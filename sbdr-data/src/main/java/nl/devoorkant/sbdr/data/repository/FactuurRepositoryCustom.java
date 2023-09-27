package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Factuur;
import nl.devoorkant.sbdr.data.model.FactuurRegel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Extension for ProductRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Jan Meekel
 * @version %I%
 */

public interface FactuurRepositoryCustom {

	@Query("SELECT p.code, p.omschrijving, " +
			"COUNT(fr.factuurRegelId), " +
			"fr.bedrag, " +
			"SUM(fr.bedrag) " +
			"FROM Factuur f " +
			"JOIN f.factuurRegels fr " +
			"JOIN fr.product p " +
			"WHERE f.factuurId = :factuurId " +
			"GROUP BY p.code, fr.bedrag ")
	List<Object[]> factuurRegelAggregate(@Param("factuurId") Integer factuurId);

	@Query("FROM Factuur WHERE datumFactuur is not null AND datumVerwerktExactOnline is null")
	List<Factuur> findAllFacturenToProcessExactOnline();

	@Query("FROM Factuur WHERE datumFactuur is null")
	List<Factuur> findAllOpenFacturen();

	// All open invoices with only invoice entries with date <= invoice date
	@Query("FROM Factuur f WHERE f.datumFactuur is null AND NOT EXISTS (SELECT 1 FROM FactuurRegel r WHERE r.factuur = f AND r.datum > :datumFactuur)")
	List<Factuur> findAllOpenFacturen(@Param("datumFactuur") Date datumFactuur);
	
	@Query("FROM Factuur WHERE factuurId = :fId")
	Factuur findByFactuurId(@Param("fId") Integer fId);

	@Query("FROM Factuur WHERE referentie = :referentie")
	List<Factuur> findByReferentie(@Param("referentie") String referentie);

	@Query("FROM Factuur f JOIN f.factuurRegels r WHERE f.bedrijf.bedrijfId = :bedrijfId AND r.product.code = :productcode")
	FactuurRegel findFactuurRegelByProductCodeBedrijfId(@Param("productcode") String productCode, @Param("bedrijfId") Integer bedrijfId);

	@Query("FROM FactuurRegel WHERE factuur.factuurId = :factuurId AND product.code = 'RAP' ORDER BY datum ASC")
	List<FactuurRegel> findRapportFactuurRegelsByFactuurId(@Param("factuurId") Integer factuurId);

	@Query("FROM FactuurRegel WHERE factuur.factuurId = :factuurId AND product.code = 'VER' ORDER BY datum ASC")
	List<FactuurRegel> findVermeldingFactuurRegelsByFactuurId(@Param("factuurId") Integer factuurId);
	
	@Query("FROM FactuurRegel WHERE factuur.factuurId = :factuurId ORDER BY datum ASC")
	List<FactuurRegel> findFactuurRegelsByFactuurId(@Param("factuurId") Integer factuurId);
	
	@Query("FROM Factuur f WHERE f.bedrijf.bedrijfId = :bedrijfId AND f.datumFactuur is null ORDER BY f.factuurId ASC")
	List<Factuur> findOpenFactuurByBedrijfId(@Param("bedrijfId") Integer bedrijfId);
	
	@Query("FROM Factuur f WHERE f.bedrijf.bedrijfId = :bedrijfId ORDER BY f.factuurId ASC")
	List<Factuur> findAllFacturenByBedrijfId(@Param("bedrijfId") Integer bedrijfId);
}
