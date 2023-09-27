package nl.devoorkant.sbdr.data.model;
// Generated Feb 27, 2017 3:05:41 PM by Hibernate Tools 3.2.4.GA


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * CustomMeldingStatus generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="CustomMeldingStatus")
@Table(name="CustomMeldingStatus"
)
public class CustomMeldingStatus  implements java.io.Serializable {


     private String code;
     private String omschrijving;
     private boolean actief;
     private Set<CustomMelding> customMeldings = new HashSet<CustomMelding>(0);

    public CustomMeldingStatus() {
    }

   
     @Id 

    
    @Column(name="Code", unique=true, nullable=false, length=3)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }


    
    @Column(name="Omschrijving", length=100)
    public String getOmschrijving() {
        return this.omschrijving;
    }
    
    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }


    
    @Column(name="Actief", nullable=false)
    public boolean isActief() {
        return this.actief;
    }
    
    public void setActief(boolean actief) {
        this.actief = actief;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="customMeldingStatus")
    public Set<CustomMelding> getCustomMeldings() {
        return this.customMeldings;
    }
    
    public void setCustomMeldings(Set<CustomMelding> customMeldings) {
        this.customMeldings = customMeldings;
    }





}


