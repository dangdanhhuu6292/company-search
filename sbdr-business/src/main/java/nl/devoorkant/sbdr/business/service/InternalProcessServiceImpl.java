package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.transfer.BriefBatchTransfer;
import nl.devoorkant.sbdr.business.transfer.InternalProcessTransfer;
import nl.devoorkant.sbdr.business.transfer.PageTransfer;
import nl.devoorkant.sbdr.business.util.*;
import nl.devoorkant.sbdr.business.util.pdf.MergePdf;
import nl.devoorkant.sbdr.business.wrapper.ErrorService;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.service.*;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service("internalProcessService")
@Transactional(readOnly = true)
public class InternalProcessServiceImpl implements InternalProcessService {
	@Autowired
	private BedrijfDataService bedrijfDataService;

	@Autowired
	private BriefBatchDataService briefBatchDataService;
	@Autowired
	private ConfiguratieDataService configuratieDataService;
	@Autowired
	private DocumentDataService documentDataService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private GebruikerDataService gebruikerDataService;
	@Autowired
	private InternalProcessDataService internalProcessDataService;
	@Autowired
	private KlantDataService klantDataService;
	@Autowired
	private MeldingDataService meldingDataService;
	private static final Logger LOGGER = LoggerFactory.getLogger(BriefBatchDataServiceImpl.class);

	@Transactional
	@Override
	public void createCustomerLetterBatchQuartzJob() throws ServiceException {
		/* pseudo code
		 * 1) create new briefBatch object
		 * 2) fill in Type, Status, gebruiker_ID, datumAangemaakt(timestamp)
		 * 3) create a new, empty PDF document
		 * 4) update all Klant objects in database by setting the briefStatus column on 'IBT'
		 * 		4a) and filling in the briefBatch_ID
		 * 5) loop through each Klant object and retrieve its letter(or create it if it doesn't exist yet)
		 * 6) add each letter to the new PDF document
		 * 7) once done
		 * 		7a) save the document to disk
		 * 		7b) add a reference to this document in the Document table
		 * 			7ba) don't forget to add a reference to the briefBatch from the Document table!
		 * 		7c) add a timestamp to the briefBatch object, this is the datumVoltooid column/field
		 * 8) again for each Klant object: set the briefStatus column to 'VWT'
		 * 9) add a new InternalProcess row to the respective table!
		 */

		try {
			DateTime today = new DateTime(new Date());
			Configuratie configObject = configuratieDataService.findByPrimaryKey("KLT_BATCH_VWK");
			DateTime lastProcessDate = new DateTime(configObject.getWaardeDate());
			Integer differenceInDays = Days.daysBetween(lastProcessDate, today).getDays();

			for(Integer i = 0; i <= differenceInDays; i++) {
				Date iterationDate = lastProcessDate.plusDays(i).toDate();

				//1
				BriefBatch newBatch = new BriefBatch();
				List<Klant> customerBatchList = briefBatchDataService.getNewCustomerLetterBatchOfDay(iterationDate);

				if(customerBatchList.size() > 0) {
					Gebruiker systeemGebruiker = gebruikerDataService.findSysteemGebruiker();

					//2
					newBatch.setBriefBatchStatusCode(EBriefBatchStatus.GESTART.getCode());
					newBatch.setBriefBatchTypeCode(EBriefBatchType.KLANT_BATCH.getCode());
					newBatch.setDatumAangemaakt(new Date());
					newBatch.setGebruikerGebruikerId(systeemGebruiker.getGebruikerId());
					BriefBatch savedBatch = briefBatchDataService.save(newBatch);

					//3
					MergePdf batchDocContent = new MergePdf();

					//4-6
					for(Klant k : customerBatchList) {
						Klant savedK = klantDataService.findByGebruikerId(k.getGebruikerId());

						//4
						savedK.setBriefStatusCode(EBriefStatus.IN_BATCH.getCode());

						//4a
						savedK.setBriefBatchBriefBatchId(savedBatch.getBriefBatchId());

						klantDataService.save(savedK);

						//5
						Document document = documentDataService.findNewAccountLetterByBedrijfId(k.getBedrijfBedrijfId());

						//6
						byte[] docContent = documentService.getDocumentContent(EDocumentType.AANMELDBRIEF, EDocumentType.AANMELDBRIEF.getPrefix() + document.getReferentieNummer());

						batchDocContent.add(docContent);
					}

					//7
					batchDocContent.close();

					//7a
					String filename = DocumentUtil.generateFilename(EDocumentType.KLANT_BATCH) + ".pdf";
					DocumentUtil.saveFile(batchDocContent.getMergedPdfByteArray(), filename);

					//7b
					Document batchDocument = new Document();
					batchDocument.setNaam(EDocumentType.KLANT_BATCH.getOmschrijving());
					batchDocument.setFileNaam(filename);
					batchDocument.setDocumentTypeCode(EDocumentType.KLANT_BATCH.getCode());
					batchDocument.setBedrijfByBedrijfIdBedrijfId(bedrijfDataService.findSbdr().getBedrijfId());
					batchDocument.setDatumAangemaakt(new Date());
					batchDocument.setActief(true);
					batchDocument.setGebruikerGebruikerId(systeemGebruiker.getGebruikerId());
					//7ba
					batchDocument.setBriefBatchBriefBatchId(savedBatch.getBriefBatchId());

					Document savedDocument = documentDataService.save(batchDocument);

					//7c
					savedBatch.setDatumVoltooid(new Date());
					savedBatch.setBriefBatchStatusCode(EBriefBatchStatus.VOLTOOID.getCode());
					briefBatchDataService.save(savedBatch);

					//8
					for(Klant k : customerBatchList) {
						Klant savedK = klantDataService.findByGebruikerId(k.getGebruikerId());

						//8
						savedK.setBriefStatusCode(EBriefStatus.DL_READY.getCode());

						klantDataService.save(savedK);
					}

					//9
					InternalProcess batchRow = new InternalProcess();
					batchRow.setDocumentDocumentId(savedDocument.getDocumentId());
					batchRow.setInternalProcessTypeCode(EInternalProcessType.KLANT_BATCH.getCode());
					batchRow.setBriefBatchBriefBatchId(savedBatch.getBriefBatchId());
					batchRow.setDatumAangemaakt(new Date());
					batchRow.setBedrijfBedrijfId(bedrijfDataService.findSbdr().getBedrijfId());
					batchRow.setInternalProcessStatusCode(EInternalProcessStatus.BTH_KLAAR_DL.getCode());

					internalProcessDataService.save(batchRow);
				}

				configObject.setWaardeDate(iterationDate);

				configuratieDataService.save(configObject);
			}
		} catch(DataServiceException e) {
			LOGGER.error("Method customerLetterBatchProcessing: " + e.getMessage());
		}
	}

	@Transactional
	@Override
	public void createNotificationLetterBatchQuartzJob() throws ServiceException {
		/**pseudo code
		 * 1) create new briefBatch object
		 * 2) fill in Type, Status, gebruiker_ID, datumAangemaakt(timestamp)
		 * 3) create a new, empty PDF document
		 * 4) update all Melding objects in database by setting the briefStatus column on 'IBT'
		 * 		4a) and filling in the briefBatch_ID
		 * 5) loop through each Melding object and retrieve its letter(or create it if it doesn't exist yet)
		 * 		5a) add the letter to a seperate list, only append the new letter if it doesn't already exist
		 * 6) add each letter to the new PDF document
		 * 7) once done
		 * 		7a) save the document to disk
		 * 		7b) add a reference to this document in the Document table
		 * 			7ba) don't forget to add a reference to the briefBatch from the Document table!
		 * 		7c) add a timestamp to the briefBatch object, this is the datumVoltooid column/field
		 * 8) again for each Melding object: set the briefStatus column to 'VWT'
		 * 9) add a new InternalProcess row to the respective table!
		 */
		try {
			DateTime today = new DateTime(new Date());
			Configuratie configObject = configuratieDataService.findByPrimaryKey("MLD_BATCH_VWK");
			DateTime lastProcessDate = new DateTime(configObject.getWaardeDate());
			Integer differenceInDays = Days.daysBetween(lastProcessDate, today).getDays();

			for(Integer i = 0; i <= differenceInDays; i++) {
				Date iterationDate = lastProcessDate.plusDays(i).toDate();

				//1
				BriefBatch newBatch = new BriefBatch();
				List<Melding> nieuweMeldingen = briefBatchDataService.getNewNotificationLetterBatchOfDay(iterationDate);

				if(nieuweMeldingen == null) {throw new ServiceException("Lijst met meldingen mag niet null zijn");}

				if(nieuweMeldingen.size() > 0) {
					List<Document> verwerkteDocumenten = new ArrayList<>();
					Gebruiker systeemGebruiker = gebruikerDataService.findSysteemGebruiker();

					//2
					newBatch.setBriefBatchStatusCode(EBriefBatchStatus.GESTART.getCode());
					newBatch.setBriefBatchTypeCode(EBriefBatchType.MELDING_BATCH.getCode());
					newBatch.setDatumAangemaakt(new Date());
					newBatch.setGebruikerGebruikerId(systeemGebruiker.getGebruikerId());
					BriefBatch savedBatch = briefBatchDataService.save(newBatch);

					//3
					MergePdf batchDocContent = new MergePdf();

					//4-5
					for(Melding m : nieuweMeldingen) {
						Melding savedM = meldingDataService.findById(m.getMeldingId());

						//4
						savedM.setBriefStatusCode(EBriefStatus.IN_BATCH.getCode());

						//4a
						savedM.setBriefBatchBriefBatchId(savedBatch.getBriefBatchId());

						meldingDataService.save(savedM);

						//5
						if(savedM.getDocumentDocumentId() != null) {
							boolean elementExists = false;

							for(Document document : verwerkteDocumenten) {
								if(document.getDocumentId().equals(savedM.getDocumentDocumentId()))
									elementExists = true;
							}

							//5a
							if(!elementExists) {verwerkteDocumenten.add(savedM.getDocument());}
						}
					}

					//6
					for(Document document : verwerkteDocumenten) {
						byte[] docContent = documentService.getDocumentContent(EDocumentType.MELDINGBRIEF, EDocumentType.MELDINGBRIEF.getPrefix() + document.getReferentieNummer());
						batchDocContent.add(docContent);
					}

					//7
					batchDocContent.close();

					//7a
					String filename = DocumentUtil.generateFilename(EDocumentType.MELDING_BATCH) + ".pdf";
					DocumentUtil.saveFile(batchDocContent.getMergedPdfByteArray(), filename);

					//7b
					Document batchDocument = new Document();
					batchDocument.setNaam(EDocumentType.MELDING_BATCH.getOmschrijving());
					batchDocument.setFileNaam(filename);
					batchDocument.setDocumentTypeCode(EDocumentType.MELDING_BATCH.getCode());
					batchDocument.setBedrijfByBedrijfIdBedrijfId(bedrijfDataService.findSbdr().getBedrijfId());
					batchDocument.setDatumAangemaakt(new Date());
					batchDocument.setActief(true);
					batchDocument.setGebruikerGebruikerId(systeemGebruiker.getGebruikerId());
					//7ba
					batchDocument.setBriefBatchBriefBatchId(savedBatch.getBriefBatchId());

					Document savedDocument = documentDataService.save(batchDocument);

					//7c
					savedBatch.setDatumVoltooid(new Date());
					savedBatch.setBriefBatchStatusCode(EBriefBatchStatus.VOLTOOID.getCode());
					briefBatchDataService.save(savedBatch);

					//8
					for(Melding m : nieuweMeldingen) {
						Melding savedM = meldingDataService.findById(m.getMeldingId());

						//8
						savedM.setBriefStatusCode(EBriefStatus.DL_READY.getCode());

						meldingDataService.save(savedM);
					}

					//9
					InternalProcess batchRow = new InternalProcess();
					batchRow.setDocumentDocumentId(savedDocument.getDocumentId());
					batchRow.setInternalProcessTypeCode(EInternalProcessType.MELDING_BATCH.getCode());
					batchRow.setBriefBatchBriefBatchId(savedBatch.getBriefBatchId());
					batchRow.setDatumAangemaakt(new Date());
					batchRow.setBedrijfBedrijfId(bedrijfDataService.findSbdr().getBedrijfId());
					batchRow.setInternalProcessStatusCode(EInternalProcessStatus.BTH_KLAAR_DL.getCode());

					internalProcessDataService.save(batchRow);
				}

				configObject.setWaardeDate(iterationDate);

				configuratieDataService.save(configObject);
			}
		} catch(DataServiceException e) {
			LOGGER.error("Method notificationLetterBatchProcessing: " + e.getMessage());
		}
	}

	@Override
	public PageTransfer<InternalProcessTransfer> findAllNewProcessRows(Pageable p) throws ServiceException {
		try {
			return ConvertUtil.convertInternalProcessPageToInternalProcessPageTransfer(internalProcessDataService.findAllNewProcessRows(p));
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public InternalProcess findById(Integer internalProcessId) throws ServiceException {
		if(internalProcessId != null) {
			try {
				return internalProcessDataService.findById(internalProcessId);
			} catch(Exception e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException("Integer internalProcessId cannot be null");
	}

	@Override
	public PageTransfer<BriefBatchTransfer> findNewBriefBatches(Pageable p) throws ServiceException {
		try {
			return ConvertUtil.convertBriefBatchPageToBriefBatchPageTransfer(briefBatchDataService.findProcessedBriefBatches(p));
		} catch(Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	@Transactional
	public void printCustomerLetter(Integer klantId) throws ServiceException {
		if(klantId != null) {
			try {
				InternalProcess iP = new InternalProcess();
				Klant k = klantDataService.findByGebruikerId(klantId);
				Document d = documentDataService.findNewAccountLetterByBedrijfId(k.getBedrijfBedrijfId());

				iP.setInternalProcessTypeCode(EInternalProcessType.KLANT_BRIEF.getCode());
				iP.setBedrijfBedrijfId(k.getBedrijfBedrijfId());
				iP.setInternalProcessStatusCode(EInternalProcessStatus.BRF_AFGEDRUKT.getCode());
				iP.setDatumAangemaakt(new Date());
				iP.setDocumentDocumentId(d.getDocumentId());
				iP.setKlantGebruikerId(klantId);
				internalProcessDataService.save(iP);

				k.setBriefStatusCode(EBriefStatus.GEDOWNLOAD.getCode());
				klantDataService.save(k);
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException("Integer klantId cannot be null");
	}

	@Override
	@Transactional
	public void printNotificationLetter(Integer meldingId) throws ServiceException {
		if(meldingId != null) {
			try {
				InternalProcess iP = new InternalProcess();

				List<Melding> meldingen = meldingDataService.findMeldingenOfDocumentByMeldingId(meldingId);

				Melding melding = meldingen.get(0);
				Document d = documentDataService.findNotificationLetterByMeldingId(meldingId);

				iP.setInternalProcessTypeCode(EInternalProcessType.MELDING_BRIEF.getCode());
				iP.setBedrijfBedrijfId(melding.getBedrijfByMeldingOverBedrijfIdBedrijfId());
				iP.setInternalProcessStatusCode(EInternalProcessStatus.BRF_AFGEDRUKT.getCode());
				iP.setDatumAangemaakt(new Date());
				iP.setDocumentDocumentId(d.getDocumentId());
				iP.setMeldingMeldingId(meldingId);
				internalProcessDataService.save(iP);

				for(Melding m : meldingen) {
					m.setBriefStatusCode(EBriefStatus.GEDOWNLOAD.getCode());
					meldingDataService.save(m);
				}
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException("Integer meldingId cannot be null");
	}

	@Override
	@Transactional
	public InternalProcess save(InternalProcess iP) throws ServiceException {
		if(iP != null) {
			try {
				return internalProcessDataService.save(iP);
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException("InternalProcess object cannot be null");
	}

	@Override
	@Transactional
	public void setBatchAsDownloaded(Integer briefBatchId) throws ServiceException {
		if(briefBatchId != null) {
			try {
				InternalProcess iP = internalProcessDataService.findByBriefBatchId(briefBatchId);
				if(iP != null) {
					iP.setInternalProcessStatusCode(EInternalProcessStatus.BTH_DOWNLOADED.getCode());

					if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.MELDING_BATCH.getCode())) {
						for(Melding m : internalProcessDataService.findAllNotificationsOfBatch(iP.getBriefBatchBriefBatchId())) {
							m.setBriefStatusCode(EBriefStatus.GEDOWNLOAD.getCode());
							meldingDataService.save(m);
						}
					} else if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.KLANT_BATCH.getCode())) {
						for(Klant k : internalProcessDataService.findAllCustomersOfBatch(iP.getBriefBatchBriefBatchId())) {
							k.setBriefStatusCode(EBriefStatus.GEDOWNLOAD.getCode());
							klantDataService.save(k);
						}
					}
					internalProcessDataService.save(iP);
				}
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException("Parameter cannot be null");
	}

	@Override
	@Transactional
	public void removeInternalProcessRow(Integer internalProcessId) throws ServiceException{
		if(internalProcessId!=null){
			try{
				InternalProcess internalProcess = internalProcessDataService.findById(internalProcessId);
				if (internalProcess != null)
					internalProcessDataService.delete(internalProcess);
			} catch(DataServiceException e){
				LOGGER.error("Unable to remove internalProcess with Id " + internalProcessId);
				throw new ServiceException(e);
			}
		} else {throw new ServiceException(new ErrorService(ErrorService.PARAMETER_IS_EMPTY).getErrorMsg());}
	}

	@Override
	@Transactional
	public void setInternalProcessRowAsSent(Integer internalProcessId, Integer gebruikerId) throws ServiceException {
		if(internalProcessId != null && gebruikerId != null) {
			try {
				InternalProcess iP = internalProcessDataService.findById(internalProcessId);
				if(iP != null && iP.getInternalProcessTypeCode() != null) {
					if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.MELDING_BATCH.getCode()) || iP.getInternalProcessTypeCode().equals(EInternalProcessType.KLANT_BATCH.getCode())) {
						iP.setInternalProcessStatusCode(EInternalProcessStatus.BTH_VERWERKT.getCode());

						if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.MELDING_BATCH.getCode())) {
							for(Melding m : internalProcessDataService.findAllNotificationsOfBatch(iP.getBriefBatchBriefBatchId())) {
								m.setBriefStatusCode(EBriefStatus.VERWERKT.getCode());
								meldingDataService.save(m);
							}
						} else if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.KLANT_BATCH.getCode())) {
							for(Klant k : internalProcessDataService.findAllCustomersOfBatch(iP.getBriefBatchBriefBatchId())) {
								k.setBriefStatusCode(EBriefStatus.VERWERKT.getCode());
								klantDataService.save(k);
							}
						}
					} else if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.MELDING_BRIEF.getCode()) || iP.getInternalProcessTypeCode().equals(EInternalProcessType.KLANT_BRIEF.getCode())) {
						iP.setInternalProcessStatusCode(EInternalProcessStatus.BRF_VERWERKT.getCode());

						if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.MELDING_BRIEF.getCode())) {
							Melding m = internalProcessDataService.findMeldingOfInternalProcess(iP.getInternalProcessId());
							List<Melding> meldingen = meldingDataService.findMeldingenOfDocumentByMeldingId(m.getMeldingId());

							for(Melding melding : meldingen) {
								melding.setBriefStatusCode(EBriefStatus.VERWERKT.getCode());
								meldingDataService.save(melding);
							}
						} else if(iP.getInternalProcessTypeCode().equals(EInternalProcessType.KLANT_BRIEF.getCode())) {
							Klant k = internalProcessDataService.findKlantOfInternalProcess(iP.getInternalProcessId());
							k.setBriefStatusCode(EBriefStatus.VERWERKT.getCode());

							klantDataService.save(k);
						}
					} else {
						throw new ServiceException("Onbekend type gevonden: " + iP.getInternalProcessTypeCode());
					}

					iP.setVerwerktDoorGebruikerGebruikerId(gebruikerId);
					iP.setDatumVerwerkt(new Date());

					internalProcessDataService.save(iP);
				}
			} catch(DataServiceException e) {
				throw new ServiceException(e);
			}
		} else throw new ServiceException("Parameters cannot be null");
	}
}
