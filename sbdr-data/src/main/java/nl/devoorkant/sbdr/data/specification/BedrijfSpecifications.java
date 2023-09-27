package nl.devoorkant.sbdr.data.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import nl.devoorkant.sbdr.data.model.Bedrijf;

public class BedrijfSpecifications {
    /**
     * Creates a specification used to find persons whose last name begins with
     * the given search term. This search is case insensitive.
     * @param searchTerm
     * @return
     */
    public static Specification<Bedrijf> bedrijfNaamIsLike(final String searchTerm) {
         
        return new Specification<Bedrijf>() {
            @Override
            public Predicate toPredicate(Root<Bedrijf> bedrijfRoot, CriteriaQuery<?> query, CriteriaBuilder cb) {
                String likePattern = getLikePattern(searchTerm);                
                return cb.like(cb.lower(bedrijfRoot.<String>get(Bedrijf_.bedrijfsNaam)), likePattern);
            }
             
            private String getLikePattern(final String searchTerm) {
                StringBuilder pattern = new StringBuilder();
                pattern.append(searchTerm.toLowerCase());
                pattern.append("%");
                return pattern.toString();
            }
        };
    }
}
