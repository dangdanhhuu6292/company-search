package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.BedrijfReportTransfer;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.transfer.RemovedBedrijfOverviewTransfer;
import nl.devoorkant.sbdr.business.transfer.ReportRequestedTransfer;
import nl.devoorkant.sbdr.data.model.Document;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface DocumentService {

	void createFactuurPdf(Integer factuurId) throws ServiceException;

	void createMeldingLetter(Integer vanBedrijfId, Integer overBedrijfId, List<Melding> meldingen, Date date) throws ServiceException;

	byte[] createMonitorDetailPdf(Integer gebruikerId, Integer bedrijfId, Integer monitoringId) throws ServiceException;

	void createNewAccountLetterPdf(Integer klantId) throws ServiceException;

	byte[] createRemovedCompaniesOverview(PageTransfer<RemovedBedrijfOverviewTransfer> removedBedrijven) throws ServiceException;

	Document findByReferentieIntern(String ref) throws ServiceException;

	BedrijfReportTransfer createReportPdf(Integer gebruikerId, Integer bedrijfId, Integer fromBedrijfId) throws ServiceException;

	byte[] getBatchDocument(Integer batchDocumentId) throws ServiceException;

	Document getDocument(Integer document_ID) throws ServiceException;

	byte[] getDocumentContent(EDocumentType doctype, String reference) throws ServiceException;

	byte[] getFactuur(Integer fId) throws ServiceException;

	byte[] getMonitoredCompaniesOverview(Integer bedrijfId) throws ServiceException;

	byte[] getNewAccountLetterContent(Integer bedrijfId) throws ServiceException;

	byte[] getNotificationLetterContent(Integer meldingId) throws ServiceException;

	byte[] getNotifiedCompaniesOverview(Integer bedrijfId) throws ServiceException;

	byte[] getRemovedCompaniesOverview(Integer bedrijfId) throws ServiceException;

	byte[] getReportedCompaniesOverview(Integer gebruikerId, Integer bedrijfId) throws ServiceException;

	PageTransfer<ReportRequestedTransfer> getRequestedReportsByGebruikerIdBedrijfId(Integer gebruikerId, Integer bedrijfId, String search, Pageable pageable) throws ServiceException;

	byte[] getSupportAttachment(Integer sBId) throws ServiceException;

	void removeAllDocumentsOfBedrijf(Integer bedrijfId) throws ServiceException;

	void removeOldNewAccountLetter(Integer bedrijfId) throws ServiceException;

	void removeOldNotificationLetter(Integer meldingId) throws ServiceException;
}