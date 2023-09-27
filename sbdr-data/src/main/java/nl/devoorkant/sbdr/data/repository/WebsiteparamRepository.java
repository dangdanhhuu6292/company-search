package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Websiteparam;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for WebsiteParam.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

public interface WebsiteparamRepository extends JpaRepository<Websiteparam, String>, WebsiteparamRepositoryCustom {

}
