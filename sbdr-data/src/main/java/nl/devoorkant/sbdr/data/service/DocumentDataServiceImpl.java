package nl.devoorkant.sbdr.data.service;

import java.util.List;
import java.util.Optional;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Document;
import nl.devoorkant.sbdr.data.repository.DocumentRepository;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import nl.devoorkant.sbdr.data.util.SearchUtil;
import nl.devoorkant.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service("documentDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class DocumentDataServiceImpl implements DocumentDataService {
	@Autowired
	private DocumentRepository documentRepository;
	
	@Override
	public Document findById(Integer documentId) throws DataServiceException {
		try {
			Optional<Document> document = documentRepository.findById(documentId);
			return document != null ? document.get() : null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
			
	}	
	
	@Override
	public Document findNewAccountLetterByBedrijfId(Integer bedrijfId) throws DataServiceException {
		Document document = null;
		
		try {
			List<Document> docs = documentRepository.findNewAccountLetterByBedrijfId(bedrijfId);
			
			if (docs != null && docs.size() == 1)
				document = docs.get(0);
				
			return document;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}		
	}
		
	
	@Override
	public Document findNotificationLetterByMeldingId(Integer meldingId) throws DataServiceException {
		try {
			return documentRepository.findNotificationLetterByMeldingId(meldingId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}				
	}
	
	@Override
	// Reports requested of certain company with user indication
	public Page<Object[]> findRequestedReportsOfGebruikerIdBedrijfId(Integer gebruikerId, Integer bedrijfId, String search, Pageable pageable) throws DataServiceException {
		Page<Object[]> result = null;
		
		try {
			if (StringUtil.isNotEmptyOrNull(search))
			{
				if (search.length() > 2) {
					if (SearchUtil.isAlfanumeric(search)) {
						String sbdrnr = SearchUtil.sbdrNumber(search);
						String repnr = SearchUtil.repNumber(search);
						
						if (sbdrnr != null)
							result = documentRepository.findRequestedReportsByGebruikerIdBedrijfId_SearchSbdrNummer(bedrijfId, SearchUtil.convertDbSearchString(sbdrnr) + "%", pageable);
						else if (repnr != null)
							result = documentRepository.findRequestedReportsByGebruikerIdBedrijfId_SearchRepNummer(bedrijfId, SearchUtil.convertDbSearchString(repnr) + "%", pageable);
						else
							result = documentRepository.findRequestedReportsByGebruikerIdBedrijfId_SearchName(bedrijfId, SearchUtil.convertDbSearchString(search) + "%", pageable);
					} else {						
//						if (SearchUtil.isKvKNumber(search))
//							result = documentRepository.findRequestedReportsByGebruikerIdBedrijfId_SearchKvkNummer(bedrijfId, SearchUtil.convertDbSearchString(search) + "%", pageable);						
//						else
							result = documentRepository.findRequestedReportsByGebruikerIdBedrijfId_SearchNummer(bedrijfId, SearchUtil.convertDbSearchString(search) + "%", SearchUtil.convertDbSearchString(search) + "%", pageable);
					}
				}
			}
			else
				result = documentRepository.findRequestedReportsByGebruikerIdBedrijfId(bedrijfId, pageable);
			
			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}		
	}	
	
	@Override
	// Reports of a certain user, company requested by any company
	public List<Document> findRequestedReportsOfBedrijfId(Integer bedrijfId) throws DataServiceException {
		
		Document document = null;
		
		try {
			List<Document> docs = documentRepository.findRequestedReportsOfBedrijfId(bedrijfId);
			
			if (docs != null)
				return docs;
			else
				return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}				
		
	}
	
	@Override
	public Document findDocumentByReference(String reference) throws DataServiceException {
		Document document = null;
		
		try {
			List<Document> docs = documentRepository.findDocumentByReference(reference);
			
			if (docs != null && docs.size() == 1)
				document = docs.get(0);
				
			return document;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}		
	}

	@Override
	public List<Document> findAllDocumentsOfBedrijf(Integer bedrijfId) throws DataServiceException{
		try{
			return documentRepository.findDocumentsByBedrijfId(bedrijfId);
		} catch(Exception e){
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public Document findByReferentieNummerIntern(String referentieNummer) throws DataServiceException {
		Document document = null;
		
		try {
			List<Document> documenten = documentRepository.findDocumentByReference(referentieNummer);
			
			if (documenten != null && documenten.size() == 1)
				return documenten.get(0);
			else
				return null;
		} catch (Exception e)
		{
			throw new DataServiceException(e.getMessage());
		}		
	}

	@Override
	@Transactional
	public void delete(Document document) throws DataServiceException {
		try {
			documentRepository.delete(document);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Document save(Document document) throws DataServiceException {
		try {

			return documentRepository.save(document);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}	
}
