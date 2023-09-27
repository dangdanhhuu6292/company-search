package nl.devoorkant.insolventie.converter;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import nl.devoorkant.sbdr.cir.data.model.CirInsolventie;
import nl.devoorkant.sbdr.cir.data.model.CirPersoon;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.cir.data.model.CirZittingslocatie;
import nl.devoorkant.sbdr.data.service.cir.InsolventieDataService;
import nl.devoorkant.util.FormatUtil;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.util.XMLUtil;
import nl.rechtspraak.namespaces.insolvency.content02.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InspubWebserviceInsolventieConverter {
    private static final Logger ioLogger = LoggerFactory.getLogger(InspubWebserviceInsolventieConverter.class);


    /**
     * Transforms an InspubWebserviceInsolvente.Insolvente Object to a CIR_Insolventie Object.
     *
     * The CIR_Insolventie created, also contains a CIR_Persoon.
     *
     * @return          A CIR_Insolventie Object or null when the transformation fails.
     */
    public static CirInsolventie transformToCIR_Insolventie(InspubWebserviceInsolvente.Insolvente poInsolvente, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_Insolventie.");
        CirInsolventie loResult = null;

        if (poInsolvente != null) {
            loResult = new CirInsolventie();

            if (poInsolvente.getPersoon() != null) loResult.setCirPersoon(transformToCIR_Persoon(poInsolvente.getPersoon()));

            loResult.setNummerInsolventie(poInsolvente.getInsolventienummer());
            loResult.setNummerSchuldSaneringsRegeling(poInsolvente.getSsrNummer());
            loResult.setNummerInsolventiePreHgk(poInsolvente.getPreHGKInsolventieNummer());
            loResult.setCodeBehandelendeInstantie(poInsolvente.getBehandelendeInstantieCode());
            loResult.setNaamBehandelendeInstantie(poInsolvente.getBehandelendeInstantieNaam());
            loResult.setCodeBehandelendeVestiging(poInsolvente.getBehandelendeVestigingCode());
            loResult.setNaamBehandelendeVestiging(poInsolvente.getBehandelendeVestigingNaam());
            loResult.setIndPreHgkgepubliceerd(poInsolvente.isIsPreHGKGepubliceerd());
            loResult.setRechterCommisaris(poInsolvente.getRC());
            loResult.setRechterCommisarisVorige(poInsolvente.getVorigeRC());
            loResult.setNummerInsolventieVorig(poInsolvente.getVorigInsolventienummer());

            if(poInsolvente.getPublicatiegeschiedenis() != null) {
                loResult.setIndInstroomAchterstand(poInsolvente.getPublicatiegeschiedenis().isInstroomLegacy());
            }

            if (poInsolvente.getAdressen() != null) {
                loResult.setIndGeheimAdres(poInsolvente.getAdressen().isGeheimAdres());
                Set<CirAdres> loCIR_Adressen = new HashSet<CirAdres>(poInsolvente.getAdressen().getAdres().size());
                for (InspubWebserviceInsolvente.Insolvente.Adressen.Adres loAdres : poInsolvente.getAdressen().getAdres()) {
                    loCIR_Adressen.add(transformToCIR_Adres(loAdres, insolventieDataService));
                }
                loResult.setCirAdreses(loCIR_Adressen);
            }
            loResult.setDatumEindeVindbaarheid(XMLUtil.convertToSQLDate(poInsolvente.getEindeVindbaarheid()));
        }

        return loResult;
    }

    public static Set<CirPublicatie> transformToCIR_Publicaties(InspubPublicatiegeschiedenis poPublicatieGeschiedenis, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_Publicaties.");
        Set<CirPublicatie> loResult = null;

        if (poPublicatieGeschiedenis != null && poPublicatieGeschiedenis.getPublicatie() != null) {

            loResult = new HashSet<>(poPublicatieGeschiedenis.getPublicatie().size());

            for (InspubPublicatiegeschiedenis.Publicatie loPublicatie : poPublicatieGeschiedenis.getPublicatie()) {
                loResult.add(transformToCIR_Publicatie(loPublicatie, insolventieDataService));
            }

        }
        return loResult;
    }

    public static Set<CirHandelsnaam> transformToCIR_HandelsNamen(List<InspubWebserviceInsolvente.Insolvente.HandelendOnderDeNamen.HandelendOnderDeNaam> poHandelsNamen, CirHandelsnaam cirHandelsnaam, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_HandelsNamen.");
        Set<CirHandelsnaam> loResult = null;

        if (poHandelsNamen != null && poHandelsNamen.size() > 0) {
            loResult = new HashSet<CirHandelsnaam>(poHandelsNamen.size());
            CirHandelsnaam loCIR_HandelsNaam = null;

            for (InspubWebserviceInsolvente.Insolvente.HandelendOnderDeNamen.HandelendOnderDeNaam loHandelendOnderDeNaam : poHandelsNamen) {
                loCIR_HandelsNaam = cirHandelsnaam;

                loCIR_HandelsNaam.setHandelsnaam(loHandelendOnderDeNaam.getHandelsnaam());
                loCIR_HandelsNaam.setNummerKvK(loHandelendOnderDeNaam.getKvKNummer());
                loCIR_HandelsNaam.setPlaatsKvK(loHandelendOnderDeNaam.getKvKPlaats());
                loCIR_HandelsNaam.setIndHandelsnaamVoorheen(loHandelendOnderDeNaam.isVoorheen());

                if (loHandelendOnderDeNaam.getHandelsadressen() != null) {
                    Set<CirAdres> loCIR_Adressen = new HashSet<CirAdres>(loHandelendOnderDeNaam.getHandelsadressen().getHandelsadres().size());
                    for (InspubAdresHandelsnaamElem loAdres : loHandelendOnderDeNaam.getHandelsadressen().getHandelsadres()) {
                        loCIR_Adressen.add(transformToCIR_Adres(loAdres, insolventieDataService));
                    }
                    loCIR_HandelsNaam.setCirAdreses(loCIR_Adressen);
                }
                loResult.add(loCIR_HandelsNaam);
            }

        }
        return loResult;
    }

    public static Set<CirCbv> transformToCIR_CBVs(List<InspubCbvElem> poCBVs, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_CBVs.");
        Set<CirCbv> loResult = null;

        if (poCBVs != null && poCBVs.size() > 0) {
            loResult = new HashSet<CirCbv>(poCBVs.size());
            CirCbv loCIR_CBV = null;

            for (InspubCbvElem loCBV : poCBVs) {
                loCIR_CBV = insolventieDataService.getNewCIR_CBV(loCBV.getCB().value());
                loCIR_CBV.setDatumBegin(XMLUtil.convertToSQLDate(loCBV.getDatumBegin()));
                loCIR_CBV.setDatumEind(XMLUtil.convertToSQLDate(loCBV.getDatumEind()));
                loCIR_CBV.setTitulatuur(loCBV.getTitulatuur());
                loCIR_CBV.setVoorletters(loCBV.getVoorletters());
                loCIR_CBV.setVoorvoegsel(loCBV.getVoorvoegsel());
                loCIR_CBV.setNaam(loCBV.getAchternaam());

                if (loCBV.getAdres() != null) loCIR_CBV.setCirAdres(transformToCIR_Adres(loCBV.getAdres(), insolventieDataService));

                loResult.add(loCIR_CBV);
            }
        }
        return loResult;
    }

    private static CirPersoon transformToCIR_Persoon(InspubPersoonWebsite poPersoon) {
        ioLogger.debug("Method transformToCIR_Persoon.");
        CirPersoon loResult = null;

        if (poPersoon != null) {
            loResult = new CirPersoon();

            loResult.setRechtsPersoonlijkheid(poPersoon.getRechtspersoonlijkheid().value());
            loResult.setVoornaam(poPersoon.getVoornaam());
            loResult.setVoorletters(poPersoon.getVoorletters());
            loResult.setVoorvoegsel(poPersoon.getVoorvoegsel());
            loResult.setNaam(poPersoon.getAchternaam());
            loResult.setDatumGeboren(XMLUtil.convertToSQLDate(poPersoon.getGeboortedatum()));
            loResult.setPlaatsGeboren(poPersoon.getGeboorteplaats());
            loResult.setLandGeboren(poPersoon.getGeboorteland());
            loResult.setDatumOverlijden(XMLUtil.convertToSQLDate(poPersoon.getOverlijdensdatum()));
            loResult.setNummerKvK(poPersoon.getKvKNummer());
            loResult.setPlaatsKvK(poPersoon.getKvKPlaats());
        }
        return loResult;
    }

    /**
     * Transforms an InspubWebserviceInsolvente.Insolvente.Adressen.Adres Object to a CIR_Adres Object.
     *
     * The CIR_Adres created is of a CIR_AdresType.
     *
     * @param poAdres   The InspubWebserviceInsolvente.Insolvente.Adressen.Adres Object to transform
     * @return          A CIR_Adres Object or null when the transformation fails.
     */
    private static CirAdres transformToCIR_Adres(InspubWebserviceInsolvente.Insolvente.Adressen.Adres poAdres, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_Adres. 1");
        CirAdres loResult = null;

        if (poAdres != null) {

            loResult = insolventieDataService.getNewCIR_Adres(poAdres.getAdresType().value());

            if (loResult != null) {
                loResult.setDatumBegin(XMLUtil.convertToSQLDate(poAdres.getDatumBegin()));
                loResult.setStraat(poAdres.getStraat());
                loResult.setHuisNummer(FormatUtil.parseInteger(poAdres.getHuisnummer()));
                loResult.setHuisNummerToevoeging1(poAdres.getHuisnummerToevoeging1());
                loResult.setHuisNummerToevoeging2(poAdres.getHuisnummerToevoeging2());
                loResult.setPostcode(poAdres.getPostcode());
                loResult.setPlaats(poAdres.getPlaats());
            } else {
                //ToDo Create transformationError and throw it.
            }
        }
        return loResult;
    }

    /**
     * Transforms an InspubAdresHandelsnaamElem Object to a CIR_Adres Object.
     *
     * The CIR_Adres created is of a CIR_AdresType.
     *
     * @param poAdres   The InspubAdresHandelsnaamElem Object to transform
     * @return          A CIR_Adres Object or null when the transformation fails.
     */
    private static CirAdres transformToCIR_Adres(InspubAdresHandelsnaamElem poAdres, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_Adres. 2");
        CirAdres loResult = null;

        if (poAdres != null) {

            loResult = insolventieDataService.getNewCIR_Adres(poAdres.getAdresType().value());

            if (loResult != null) {
                loResult.setDatumBegin(XMLUtil.convertToSQLDate(poAdres.getDatumBegin()));
                loResult.setStraat(poAdres.getStraat());
                loResult.setHuisNummer(FormatUtil.parseInteger(poAdres.getHuisnummer()));
                loResult.setHuisNummerToevoeging1(poAdres.getHuisnummerToevoeging1());
                loResult.setHuisNummerToevoeging2(poAdres.getHuisnummerToevoeging2());
                loResult.setPostcode(poAdres.getPostcode());
                loResult.setPlaats(poAdres.getPlaats());
            } else {
                //ToDo Create transformationError and throw it.
            }
        }
        return loResult;
    }

    /**
     * Transforms an InspubAdresCBElem Object to a CIR_Adres Object.
     *
     * @param poAdres   The InspubAdresCBElem Object to transform
     * @return          A CIR_Adres Object or null when the transformation fails.
     */
    private static CirAdres transformToCIR_Adres(InspubAdresCBElem poAdres, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_Adres. 3");
        CirAdres loResult = null;

        if (poAdres != null) {

            loResult = insolventieDataService.getNewCIR_Adres();

            if (loResult != null) {
                loResult.setDatumBegin(XMLUtil.convertToSQLDate(poAdres.getDatumBegin()));
                loResult.setStraat(poAdres.getStraat());
                loResult.setHuisNummer(FormatUtil.parseInteger(poAdres.getHuisnummer()));
                loResult.setHuisNummerToevoeging1(poAdres.getHuisnummerToevoeging1());
                loResult.setHuisNummerToevoeging2(poAdres.getHuisnummerToevoeging2());
                loResult.setPostcode(poAdres.getPostcode());
                loResult.setPlaats(poAdres.getPlaats());
            } else {
                //ToDo Create transformationError and throw it.
            }
        }
        return loResult;
    }

    /**
     * Transforms an InspubPublicatiegeschiedenis.Publicatie Object to a CIR_Adres Object.
     *
     * The CIR_Publicatie created is of a CIR_PublicatieSoort and if applicable a CIR_ZittingsLocatie.
     *
     * @param poPublicatie  The InspubPublicatiegeschiedenis.Publicatie Object to transform
     * @return              A CIR_Publicatie Object or null when the transformation fails.
     */
    private static CirPublicatie transformToCIR_Publicatie(InspubPublicatiegeschiedenis.Publicatie poPublicatie, InsolventieDataService insolventieDataService) {
        ioLogger.debug("Method transformToCIR_Publicatie.");
        CirPublicatie loResult = null;

        if (poPublicatie != null) {

            loResult = insolventieDataService.getNewCIR_Publicatie(poPublicatie.getPublicatieSoortCode());

            if (loResult != null) {
                if (poPublicatie.getZittingslocatie() != null) {
                    InspubZittingslocatie loZittingsLocatie = poPublicatie.getZittingslocatie();

                    CirZittingslocatie loCIR_ZittingsLocatie = insolventieDataService.getCIR_ZittingsLocatie(loZittingsLocatie.getStraat(), loZittingsLocatie.getPlaats(), loZittingsLocatie.getHuisnummer());

                    if (loCIR_ZittingsLocatie != null) {
                        loCIR_ZittingsLocatie.setHuisNummerToevoeging(loZittingsLocatie.getHuisnummerToevoeging());
                        loResult.setCirZittingslocatie(loCIR_ZittingsLocatie);
                    }
                }

                loResult.setDatumPublicatie(XMLUtil.convertToSQLDate(poPublicatie.getPublicatieDatum()));
                loResult.setPublicatieOmschrijving(StringUtil.truncate(poPublicatie.getPublicatieOmschrijving(), 1024));
                loResult.setCodePublicerendeInstantie(poPublicatie.getPublicerendeInstantieCode());
                loResult.setPublicatieKenmerk(poPublicatie.getPublicatieKenmerk());

            } else {
                //ToDo Create transformationError and throw it.
            }
        }

        ioLogger.debug("Method transformToCIR_Publicatie. CIR_Publicatie created is {}", loResult.toString());
        return loResult;
    }


}
