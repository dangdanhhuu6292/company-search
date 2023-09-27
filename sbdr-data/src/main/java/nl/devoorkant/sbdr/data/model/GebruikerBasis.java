package nl.devoorkant.sbdr.data.model;
// Generated Feb 27, 2017 3:05:41 PM by Hibernate Tools 3.2.4.GA


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * GebruikerBasis generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="GebruikerBasis")
@Table(name="GebruikerBasis"
)
@Inheritance(strategy=InheritanceType.JOINED)
public class GebruikerBasis  implements java.io.Serializable {


     private Integer gebruikerId;
     private Date datumAangemaakt;
     private Boolean actief;
     private Set<GebruikerEigenschap> gebruikerEigenschappen = new HashSet<GebruikerEigenschap>(0);

    public GebruikerBasis() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="Gebruiker_ID", unique=true, nullable=false)
    public Integer getGebruikerId() {
        return this.gebruikerId;
    }
    
    public void setGebruikerId(Integer gebruikerId) {
        this.gebruikerId = gebruikerId;
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DatumAangemaakt", length=19)
    public Date getDatumAangemaakt() {
        return this.datumAangemaakt;
    }
    
    public void setDatumAangemaakt(Date datumAangemaakt) {
        this.datumAangemaakt = datumAangemaakt;
    }


    
    @Column(name="Actief")
    public Boolean getActief() {
        return this.actief;
    }
    
    public void setActief(Boolean actief) {
        this.actief = actief;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="gebruikerBasis")
    public Set<GebruikerEigenschap> getGebruikerEigenschappen() {
        return this.gebruikerEigenschappen;
    }
    
    public void setGebruikerEigenschappen(Set<GebruikerEigenschap> gebruikerEigenschappen) {
        this.gebruikerEigenschappen = gebruikerEigenschappen;
    }





}


