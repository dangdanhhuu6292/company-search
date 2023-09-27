package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.InternalProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalProcessRepository extends JpaRepository<InternalProcess, Integer>, InternalProcessRepositoryCustom{
}
