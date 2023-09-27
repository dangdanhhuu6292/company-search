package nl.devoorkant.sbdr.data.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.*;
import nl.devoorkant.sbdr.data.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service("companyInfoDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class CompanyInfoDataServiceImpl implements CompanyInfoDataService {

	@Autowired
	CIKvKBestuurderFunctieRepository ciKvKBestuurderFunctieRepository;
	@Autowired
	CIKvKBestuurderRepository ciKvKBestuurderRepository;
	@Autowired
	CIKvKAandeelhouderRepository ciKvKAandeelhouderRepository;
	@Autowired
	CIKvKCuratorRepository ciKvKCuratorRepository;
	@Autowired
	CIKvKHistorieBestuurderRepository ciKvKHistorieBestuurderRepository;
	@Autowired
	CIKvKHistorieAandeelhouderRepository ciKvKHistorieAandeelhouderRepository;
	@Autowired
	CIKvKHistorieCuratorRepository ciKvKHistorieCuratorRepository;
	@Autowired
	CIKvKDossierHistorieRepository ciKvkHistorieRepo;
	@Autowired
	CIKvKDossierRepository ciKvkRepo;
	@Autowired
	CompanyInfoRepository ciRepo;
	@Autowired
	CIKvKHistorieBestuurderFunctieRepository ciKvKHistorieBestuurderFunctieRepository;

	@Override
	@Transactional
	public void deleteCIKvkBestuurder(CIKvKBestuurder bestuurder) throws DataServiceException {
		try {
			CIKvKBestuurder existingBestuurder = ciKvKBestuurderRepository.findByNaamAndKvKDossierId(bestuurder.getNaam(), bestuurder.getCIKvKDossierCikvKdossierId());
			if(existingBestuurder != null) ciKvKBestuurderRepository.delete(existingBestuurder);
		} catch(Exception e) {
			throw new DataServiceException("Error deleting CIKvKBestuurder: " + e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public void deleteCIKvkAandeelhouder(CIKvKAandeelhouder aandeelhouder) throws DataServiceException  {
		try {
			CIKvKAandeelhouder existingAandeelhouder = ciKvKAandeelhouderRepository.findByNaamAndKvKDossierId(aandeelhouder.getNaam(), aandeelhouder.getCIKvKDossierCikvKdossierId());
			if(existingAandeelhouder != null) ciKvKAandeelhouderRepository.delete(existingAandeelhouder);
		} catch(Exception e) {
			throw new DataServiceException("Error deleting CIKvKAandeelhouder: " + e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public void deleteCIKvkCurator(CIKvKCurator curator) throws DataServiceException {
		try {
			CIKvKCurator existingCurator = ciKvKCuratorRepository.findByNaamAndKvKDossierId(curator.getNaam(), curator.getCIKvKDossierCikvKdossierId());
			if(existingCurator != null) ciKvKCuratorRepository.delete(existingCurator);
		} catch(Exception e) {
			throw new DataServiceException("Error deleting CIKvKCurator: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void deleteCIKvkDossier(CIKvKDossier kvkDossier) throws DataServiceException {

		try {
			ciKvkRepo.delete(kvkDossier);
		} catch(Exception e) {
			throw new DataServiceException("Error deleting CIKvKDossier.");
		}
	}

	@Override
	public CIKvKDossier findCIKvkDossierByKvkNumber(String kvkNumber, String subDossier) throws DataServiceException {

		try {
			List<CIKvKDossier> kvkDossiers = ciKvkRepo.findByKvKNummer(kvkNumber, subDossier);

			if(kvkDossiers != null && kvkDossiers.size() == 1) return kvkDossiers.get(0);
			else return null;

		} catch(Exception e) {
			throw new DataServiceException("Error fetching CIKvKDossier with kvkNumber: " + kvkNumber);
		}
	}
	
	@Override
	public CIKvKDossier findByCIKvKDossierId(Integer ciKvKDossierId) throws DataServiceException {
		try {
			CIKvKDossier kvkDossier = ciKvkRepo.findByCIKvKDossier(ciKvKDossierId);

			return kvkDossier;
		} catch(Exception e) {
			throw new DataServiceException("Error fetching CIKvKDossier with ciKvKDossierId: " + ciKvKDossierId);
		}		
	}

	@Override
	public CIKvKDossier findById(Integer ciKvkDossierId) throws DataServiceException {
		try {
			Optional<CIKvKDossier> ciKvKDossier = ciKvkRepo.findById(ciKvkDossierId);
			return ciKvKDossier != null ? ciKvKDossier.get() : null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public CIKvKDossier saveCIKvkDossier(CIKvKDossier kvkDossier) throws DataServiceException {
		CIKvKDossier result = null;

		try {
			Set<CIKvKBestuurder> bestuurders = new HashSet<CIKvKBestuurder>(kvkDossier.getCIKvKBestuurders());
			Set<CIKvKAandeelhouder> aandeelhouders = new HashSet<CIKvKAandeelhouder>(kvkDossier.getCIKvKAandeelhouders());
			Set<CIKvKCurator> curators = new HashSet<CIKvKCurator>(kvkDossier.getCIKvKCurators());
			result = ciKvkRepo.save(kvkDossier);

			for(CIKvKBestuurder bestuurder : bestuurders) {
				bestuurder.setCIKvKDossierCikvKdossierId(result.getCikvKdossierId());

				if(ciKvKBestuurderRepository.findByNaamAndKvKDossierId(bestuurder.getNaam(), bestuurder.getCIKvKDossierCikvKdossierId()) == null) {
					CIKvKBestuurder savedBestuurder = ciKvKBestuurderRepository.save(bestuurder);

					for(CIKvKBestuurderFunctie bestuurderFunctie : bestuurder.getCIKvKBestuurderFuncties()) {
						bestuurderFunctie.setCIKvKBestuurderCikvKbestuurderId(savedBestuurder.getCikvKbestuurderId());
						if(ciKvKBestuurderFunctieRepository.findByFunctieAndBestuurderId(bestuurderFunctie.getFunctie(), bestuurder.getCikvKbestuurderId()) == null) {
							ciKvKBestuurderFunctieRepository.save(bestuurderFunctie);
						}
					}
				}
			}

			for(CIKvKAandeelhouder aandeelhouder : aandeelhouders) {
				aandeelhouder.setCIKvKDossierCikvKdossierId(result.getCikvKdossierId());

				if(ciKvKAandeelhouderRepository.findByNaamAndKvKDossierId(aandeelhouder.getNaam(), aandeelhouder.getCIKvKDossierCikvKdossierId()) == null) {
					CIKvKAandeelhouder savedAandeelhouder = ciKvKAandeelhouderRepository.save(aandeelhouder);
				}
			}
			
			for(CIKvKCurator curator : curators) {
				curator.setCIKvKDossierCikvKdossierId(result.getCikvKdossierId());

				if(ciKvKCuratorRepository.findByNaamAndKvKDossierId(curator.getNaam(), curator.getCIKvKDossierCikvKdossierId()) == null) {
					CIKvKCurator savedCurator = ciKvKCuratorRepository.save(curator);
				}
			}			
		} catch(Exception e) {
			throw new DataServiceException("Error saving CIKvKDossier: " + e.getMessage());
		}

		return result;
	}

	@Override
	@Transactional
	public CIKvKDossierHistorie saveCIKvkDossierHistorie(CIKvKDossierHistorie kvkDossier) throws DataServiceException {
		CIKvKDossierHistorie result = null;

		try {
			Set<CIKvKHistorieBestuurder> histBests = new HashSet<CIKvKHistorieBestuurder>(kvkDossier.getCIKvKHistorieBestuurders());
			Set<CIKvKHistorieAandeelhouder> histAandhs = new HashSet<CIKvKHistorieAandeelhouder>(kvkDossier.getCIKvKHistorieAandeelhouders());
			Set<CIKvKHistorieCurator> histCurators = new HashSet<CIKvKHistorieCurator>(kvkDossier.getCIKvKHistorieCurators());
			result = ciKvkHistorieRepo.save(kvkDossier);

			for(CIKvKHistorieBestuurder historieBestuurder : histBests) {
				historieBestuurder.setCIKvKDossierHistorieCikvKdossierHistorieId(result.getCikvKdossierHistorieId());

				CIKvKHistorieBestuurder savedHistorieBestuurder = ciKvKHistorieBestuurderRepository.save(historieBestuurder);

				for(CIKvKHistorieBestuurderFunctie historieBestuurderfunctie : historieBestuurder.getCIKvKHistorieBestuurderFuncties()) {
					historieBestuurderfunctie.setCIKvKHistorieBestuurderCikvKhistorieBestuurderId(savedHistorieBestuurder.getCikvKhistorieBestuurderId());

					ciKvKHistorieBestuurderFunctieRepository.save(historieBestuurderfunctie);
				}
			}
			
			for(CIKvKHistorieAandeelhouder historieAandeelhouder : histAandhs) {
				historieAandeelhouder.setCIKvKDossierHistorieCikvKdossierHistorieId(result.getCikvKdossierHistorieId());

				CIKvKHistorieAandeelhouder savedHistorieAandeelhouder = ciKvKHistorieAandeelhouderRepository.save(historieAandeelhouder);
			}
			
			for(CIKvKHistorieCurator historieCurator : histCurators) {
				historieCurator.setCIKvKDossierHistorieCikvKdossierHistorieId(result.getCikvKdossierHistorieId());

				CIKvKHistorieCurator savedHistorieCurator = ciKvKHistorieCuratorRepository.save(historieCurator);
			}			

		} catch(Exception e) {
			throw new DataServiceException("Error saving CIKvKDossier: " + e.getMessage());
		}

		return result;
	}


}
