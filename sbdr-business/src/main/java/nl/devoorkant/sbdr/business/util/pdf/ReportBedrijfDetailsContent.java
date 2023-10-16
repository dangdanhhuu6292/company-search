package nl.devoorkant.sbdr.business.util.pdf;

import java.awt.Color;

import nl.devoorkant.sbdr.business.transfer.AandeelhouderTransfer;
import nl.devoorkant.sbdr.business.transfer.BedrijfReportTransfer;
import nl.devoorkant.sbdr.business.transfer.CuratorTransfer;
import nl.devoorkant.sbdr.business.transfer.KvkBestuurderFunctieTransfer;
import nl.devoorkant.sbdr.business.transfer.KvkBestuurderTransfer;
import nl.devoorkant.sbdr.business.transfer.KvkDossierTransfer;
import nl.devoorkant.sbdr.business.util.EBedrijfType;
import nl.devoorkant.util.FormatUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;

public class ReportBedrijfDetailsContent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportFromBedrijfContent.class);

    private static PdfPTable createTableBedrijf() {
        PdfPTable result = null;

        try {
            result = new PdfPTable(3);
            result.setWidths(new float[]{3, 5, 1});
            result.setWidthPercentage(100);
            result.setKeepTogether(false);
        } catch (DocumentException e1) {
            LOGGER.error("Cannot set document table parameters", e1);
        }

		return result;
	}

	public static PdfPTable createContentRechtspersoon(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		KvkDossierTransfer bedrijf = report.getKvkDossierTransfer();
		// BA nummer
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "BA-nummer"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getSbdrNummer()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

		// Kvk nummer
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "KvK-nummer"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getKvkNummer()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

		// RSIN
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "RSIN nummer"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getRsin()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Vennootschapsnaam
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Statutair naam"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getVennootschapsNaam()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

		// Handelsnaam
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Handelsnaam"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getHandelsNaam()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		

		// Rechtsvorm
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Rechtsvorm"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getRechtsvorm()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
				

		// Adres
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Adres"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getStraat() + " " + bedrijf.getHuisnummer() + " " + bedrijf.getHuisnummerToevoeging()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		if (bedrijf.getPostcode() != null)
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getPostcode() + " " + bedrijf.getPlaats()));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "- " + bedrijf.getPlaats()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));					
		
		
		
		// Hoofdvestiging
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Vestiging"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, EBedrijfType.get(bedrijf.getHoofdNeven()).getOmschrijving()));
		//contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getHoofdNeven()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// BTW nummer
		if (bedrijf.getBtwnummer() != null && !bedrijf.getBtwnummer().equals("")) {		
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "BTW nummer"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getSbdrNummer()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		}
				
		// Gestort kapitaal
		String kapitaalBedrag = "onbekend";
		if (bedrijf.getGestortKapitaal() != null && bedrijf.getGestortKapitaal()>0) {
			
			if (bedrijf.getGestortKapitaal() != null) {
				kapitaalBedrag = "\u20AC " + PrintUtil.formatCurrency(bedrijf.getGestortKapitaal().doubleValue());			    		
			}			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Gestort kapitaal"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, kapitaalBedrag));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		}
		
		// Nominaal aandelenkapitaal
//		if (bedrijf.getNominaalAandelenKapitaal() != null) {
//			kapitaalBedrag = "onbekend";
//			if (bedrijf.getNominaalAandelenKapitaal() != null) {
//				kapitaalBedrag = "\u20AC " + FormatUtil.formatCurrency(bedrijf.getNominaalAandelenKapitaal().doubleValue(), 2);			    		
//			}			
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Nominaal aandelenkapitaal"));
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, kapitaalBedrag));
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
//		}
		
		// Empty regel
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
				
		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}
	
	public static PdfPTable createContentOnderneming(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		KvkDossierTransfer bedrijf = report.getKvkDossierTransfer();

		// Datum vestiging
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Oprichtingsdatum"));
		if (bedrijf.getDatumInschrijving() != null)			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, FormatUtil.formatDate(bedrijf.getDatumInschrijving())));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "n.v.t."));			
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
				
		// Datum voortzetting
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Datum voortzetting"));
		if (bedrijf.getDatumVoorzetting() != null)			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, FormatUtil.formatDate(bedrijf.getDatumVoorzetting())));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "n.v.t."));	
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
				
		// Hoofd SBI
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Hoofdactiviteit (SBI)"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getHoofdactiviteit()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
				
		// SBI 1
		if (bedrijf.getNevenactiviteit1() != null && !bedrijf.getNevenactiviteit1().equals("")) {
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Nevenactiviteit 1 (SBI)"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getNevenactiviteit1()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		}
		
		// SBI 2
		if (bedrijf.getNevenactiviteit2() != null && !bedrijf.getNevenactiviteit2().equals("")) {		
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Nevenactiviteit 2 (SBI)"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getNevenactiviteit2()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		}
		
		// Aantal vestigingen
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Aantal vestigingen"));
//		if (bedrijf.getDochters() != null) {
//			if (bedrijf.getDochters() < 2)
//				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "1 hoofdvestiging"));
//			else
//				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getDochters().toString() + " vestigingen"));
//		}
//		else
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "onbekend"));			
//		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Aantal medewerkers
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Aantal medewerkers"));
		if (bedrijf.getMedewerkers() != null && !bedrijf.getMedewerkers().equals(new Integer(0)))
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getMedewerkers().toString()));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "onbekend"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Import/export
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Import/Export"));
		if ((bedrijf.getImportHandel() != null && bedrijf.getImportHandel().equals("J")) ||
				(bedrijf.getExportHandel() != null && bedrijf.getExportHandel().equals("J")))
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Ja"));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Nee"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Domeinnaam
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Domeinnaam"));
		if (bedrijf.getDomeinnaam() != null && !bedrijf.getDomeinnaam().equals(""))
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getDomeinnaam()));
		else
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "onbekend"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Deponering jaarstukken
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Deponering jaarstukken"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getDeponeringJaarstukken()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		
		// Klant bij betalingsachterstand.nl
		//contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Klant bij CRZB"));
		//if (bedrijf.isKlant())
		//	contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Ja"));
		//else
		//	contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Nee"));			
		//contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
				
		// Empty regel
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}	
	
	public static PdfPTable createContentMoederMaatschappijen(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		KvkDossierTransfer bedrijf = report.getKvkDossierTransfer();
		
		// line
		LineSeparator ls = new LineSeparator();
		ls.setLineColor(Color.LIGHT_GRAY);
		
		PdfPCell lineCell = new PdfPCell();
		lineCell.setColspan(2);
		lineCell.setPadding(0);
		lineCell.setBorder(PdfPCell.NO_BORDER);
		lineCell.addElement(new Chunk(ls));			
		
		// Moeder maatschappijen
		
		if (report.getUltimateParent() != null) {
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Hoogste moedermaatschappij"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getUltimateParent().getBedrijfsNaam()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Adres"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getUltimateParent().getStraat() + " " + report.getUltimateParent().getHuisNummer()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, ""));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getUltimateParent().getPostcode() + " " + report.getUltimateParent().getPlaats()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "KvK-nummer"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getUltimateParent().getKvKnummer()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			if (report.getParent() != null) {
				contentBedrijf.addCell(lineCell);
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			} else
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(3, " "));				
			
		}
		
		if (report.getParent() != null) {
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Hoofdvestiging"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getParent().getBedrijfsNaam()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Adres"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getParent().getStraat() + " " + report.getParent().getHuisNummer()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, ""));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getParent().getPostcode() + " " + report.getParent().getPlaats()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "KvK-nummer"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, report.getParent().getKvKnummer()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(3, " "));
		}			
	
		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}	

	public static PdfPTable createContentBestuurders(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		KvkDossierTransfer bedrijf = report.getKvkDossierTransfer();
		
		// line
		LineSeparator ls = new LineSeparator();
		ls.setLineColor(Color.LIGHT_GRAY);
		
		PdfPCell lineCell = new PdfPCell();
		lineCell.setColspan(2);
		lineCell.setPadding(0);
		lineCell.setBorder(PdfPCell.NO_BORDER);
		lineCell.addElement(new Chunk(ls));			
		
		// Bestuurders				
			
		if(bedrijf.getKvkBestuurderTransfer()!=null)
			if(bedrijf.getKvkBestuurderTransfer().size()>0) {
				//if (bedrijf.isKvkContact()) {
				for(KvkBestuurderTransfer bestTransf : bedrijf.getKvkBestuurderTransfer()) {
					contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Naam"));
					contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bestTransf.getNaam()));
					contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
										
					if (bestTransf.getFuncties() != null && !bestTransf.getFuncties().isEmpty()) {												
						if (bestTransf.getFuncties().size() == 1)
							contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Functie"));
						else
							contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Functies"));
						
						String functies = null;
						for (KvkBestuurderFunctieTransfer bestFunctie : bestTransf.getFuncties()) {
							if (bestFunctie.getFunctie() != null)
								functies = functies == null ? bestFunctie.getFunctie() : functies + ", " + bestFunctie.getFunctie();
						}
						
						if (functies != null) {
							contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, functies));
						} else
							contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "onbekend"));					

						contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
					}
					
					contentBedrijf.addCell(lineCell);			
					contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));					
				}
			}

			//			// voorletters
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Voorletters"));
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getVoorlettersKvk()));
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			//
			//			// voorvoegsel
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Tussenvoegsel"));
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getVoorvoegselKvk()));
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			//
			//			// achternaam
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Achternaam"));
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getAchternaamKvk()));
			//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
	
		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}	
	
	public static PdfPTable createContentAandeelhouders(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		KvkDossierTransfer bedrijf = report.getKvkDossierTransfer();
		
		// Aandeelhouders
					
		if(bedrijf.getAandeelhoudersTransfer()!=null)
			if(bedrijf.getAandeelhoudersTransfer().size()>0) {
				for(AandeelhouderTransfer aandTransf : bedrijf.getAandeelhoudersTransfer()) {
					contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Naam"));
					contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, aandTransf.getNaam()));
					contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

					if (aandTransf.getAdres() != null) {
						contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Adres"));
						contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, aandTransf.getAdres()));
						contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

						if (aandTransf.getPostcodeplaats() != null) {
							contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Postcode en plaats"));
							contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, aandTransf.getPostcodeplaats()));
							contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
						}
					}					
					
				}
			}			
	
		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}	
	
	public static PdfPTable createContentCurator(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		KvkDossierTransfer bedrijf = report.getKvkDossierTransfer();
		
		// Curator
					
		if(bedrijf.getCuratorTransfer()!=null) {
			CuratorTransfer curatorTransf = bedrijf.getCuratorTransfer();
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Naam"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, curatorTransf.getNaam()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));

			if (curatorTransf.getAdres() != null) {
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Adres"));
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, curatorTransf.getAdres()));
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			}										
		}			
	
		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}	

	
	public static PdfPTable createContentContactgegevens(BedrijfReportTransfer report) {
        PdfPTable result = PrintUtil.createMasterTable();
        
		// bedrijf master cell
		PdfPCell bedrijfcell = new PdfPCell();		
		bedrijfcell.setPadding(0);
		bedrijfcell.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable contentBedrijf = createTableBedrijf();
				
		KvkDossierTransfer bedrijf = report.getKvkDossierTransfer();

		// Handelsnaam
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Handelsnaam"));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getHandelsNaam()));
		contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));		
		
		// Postadres
		if (bedrijf.getPost_postbus() != null && !bedrijf.getPost_postbus().equals("")) {
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Postadres"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getPost_postbus()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getPost_postcode() + " " + bedrijf.getPost_plaats()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		} else {
			// Adres
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Adres"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getStraat() + " " + bedrijf.getHuisnummer() + " " + bedrijf.getHuisnummerToevoeging()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			if (bedrijf.getPostcode() != null)
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getPostcode() + " " + bedrijf.getPlaats()));
			else
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "- " + bedrijf.getPlaats()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));					
		}
		
		
		
		// telefoon
		//boolean telefoon = false;
		if (bedrijf.getTelefoonKvk() != null && !bedrijf.getTelefoonKvk().equals("")) {
			//telefoon = true;
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Telefoon"));
			if (bedrijf.getTelefoonKvk() != null && !bedrijf.getTelefoonKvk().equals(""))
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getTelefoonKvk()));
			else
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "onbekend"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		}
		
		// mobiel
		if (bedrijf.getMobielKvk() != null && bedrijf.getMobielKvk().equals("")) {
			//telefoon = true;
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Mobiel"));
			if (bedrijf.getMobielKvk() != null && bedrijf.getMobielKvk().equals(""))
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getMobielKvk()));
			else
				contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "onbekend"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
		}
		
		// Contact CRZB
		if (false && bedrijf.isKlant()) {
			// voorletters
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Voornaam"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getVoornaam()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			// voorvoegsel
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Achternaam"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getAchternaam()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
						
			// telefoon
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Telefoon"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getTelefoon()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));
			
			// email
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Email"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getEmail()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));			

			// Functie
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Functie"));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, bedrijf.getFunctie()));
			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));			
		}
		
		// No contact
//		if ((true || !bedrijf.isKlant()) && !telefoon) {
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Contact"));
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, "Er zijn geen contactgegevens bekend."));
//			contentBedrijf.addCell(PrintUtil.createCellWithColspan(1, " "));			
//		}
	
		// For last row
		contentBedrijf.completeRow();

        bedrijfcell.addElement(contentBedrijf);
        result.addCell(bedrijfcell);
		
		return result;
		
	}		
}
