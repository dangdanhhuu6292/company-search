package nl.devoorkant.sbdr.data.repository;


import nl.devoorkant.sbdr.data.model.MobileGebruiker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobileGebruikerRepository extends JpaRepository<MobileGebruiker, String>, MobileGebruikerRepositoryCustom{

}