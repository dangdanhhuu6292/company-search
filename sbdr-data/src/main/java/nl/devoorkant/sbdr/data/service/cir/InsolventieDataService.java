package nl.devoorkant.sbdr.data.service.cir;

import java.util.Date;
import java.util.List;
import java.util.Set;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import nl.devoorkant.sbdr.cir.data.model.CirCbv;
import nl.devoorkant.sbdr.cir.data.model.CirHandelsnaam;
import nl.devoorkant.sbdr.cir.data.model.CirInsolventie;
import nl.devoorkant.sbdr.cir.data.model.CirPersoon;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatie;
import nl.devoorkant.sbdr.cir.data.model.CirPublicatiekenmerk;
import nl.devoorkant.sbdr.cir.data.model.CirZittingslocatie;
import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.validation.Result;

public interface InsolventieDataService {

	boolean setTijdLaatsteSynchronizatie(Date poDatumLaatsteSynchronizatie);

	CirInsolventie getCIR_InsolventieByNummerInsolventie(
			String pstrNummerInsolventie);

	Result removeAllInsolventies();

	Result removeInsolventie(CirInsolventie poCIR_Insolventie);

	Result createInsolventie(CirInsolventie poCIR_Insolventie,
			Set<CirPublicatie> poCIR_Publicaties,
			Set<CirHandelsnaam> poCIR_HandelsNamen, Set<CirCbv> poCIR_CBVs)
			throws DataServiceException;

	CirAdres getNewCIR_Adres();

	CirAdres getNewCIR_Adres(String pstrCIR_AdresTypeCode);

	CirCbv getNewCIR_CBV(String pstrCIR_CBVTypeCode);

	CirHandelsnaam getNewCIR_HandelsNaam();

	CirPersoon getCIR_Persoon(Integer poID);

	List<CirPublicatiekenmerk> getCIR_PublicatieKenmerkenByPublicatieKenmerk(
			String pstrPublicatieKenmerk);

	CirPublicatie getNewCIR_Publicatie(String pstrCodePublicatieSoort);

	List<CirPublicatiekenmerk> getUnprocessedCIR_PublicatieKenmerken();

	Result saveCIR_PublicatieKenmerk(
			CirPublicatiekenmerk poCIR_PublicatieKenmerk);

	Result createCIR_Exceptie(String pstrCodeExceptieType);

	Result createCIR_Exceptie(String pstrCodeExceptieType,
			String pstrOmschrijving);

	Result createCIR_PublicatieKenmerk(String pstrPublicatieKenmerk);

	Result createCIR_PublicatieKenmerken(List<String> poPublicatieKenmerken);

	CirZittingslocatie getCIR_ZittingsLocatie(String pstrStraat,
			String pstrPlaats, String pstrHuisNummer);

}
