package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DocumentDataService {
	/**
	 * @param document
	 * @throws DataServiceException
	 */
	void delete(Document document) throws DataServiceException;

	List<Document> findAllDocumentsOfBedrijf(Integer bedrijfId) throws DataServiceException;

	Document findByReferentieNummerIntern(String referentieNummer) throws DataServiceException;

	/**
	 * @param reference
	 * @return
	 * @throws DataServiceException
	 */
	Document findDocumentByReference(String reference) throws DataServiceException;

	/**
	 * @param bedrijfId
	 * @return
	 * @throws DataServiceException
	 */
	Document findNewAccountLetterByBedrijfId(Integer bedrijfId) throws DataServiceException;

	/**
	 * @param meldingId
	 * @return
	 * @throws DataServiceException
	 */
	Document findNotificationLetterByMeldingId(Integer meldingId) throws DataServiceException;

	Document findById(Integer documentId) throws DataServiceException;

	List<Document> findRequestedReportsOfBedrijfId(Integer bedrijfId) throws DataServiceException;

	/**
	 * @param gebruikerId
	 * @param bedrijfId
	 * @param pageable
	 * @return
	 * @throws DataServiceException
	 */
	Page<Object[]> findRequestedReportsOfGebruikerIdBedrijfId(Integer gebruikerId, Integer bedrijfId, String search, Pageable pageable) throws DataServiceException;

	/**
	 * @param document
	 * @return
	 * @throws DataServiceException
	 */
	Document save(Document document) throws DataServiceException;

}
