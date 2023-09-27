package nl.devoorkant.sbdr.data.model;
// Generated Feb 27, 2017 3:05:41 PM by Hibernate Tools 3.2.4.GA


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Bevoegdheid generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="Bevoegdheid")
@Table(name="Bevoegdheid"
    , uniqueConstraints = @UniqueConstraint(columnNames="Code") 
)
public class Bevoegdheid  implements java.io.Serializable {


     private Integer bevoegdheidId;
     private String code;
     private String omschrijving;
     private boolean actief;
     private Set<Rol> rollen = new HashSet<Rol>(0);

    public Bevoegdheid() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="Bevoegdheid_ID", unique=true, nullable=false)
    public Integer getBevoegdheidId() {
        return this.bevoegdheidId;
    }
    
    public void setBevoegdheidId(Integer bevoegdheidId) {
        this.bevoegdheidId = bevoegdheidId;
    }


    
    @Column(name="Code", unique=true, nullable=false, length=10)
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
@ManyToMany(fetch=FetchType.LAZY, mappedBy="bevoegdheden")
    public Set<Rol> getRollen() {
        return this.rollen;
    }
    
    public void setRollen(Set<Rol> rollen) {
        this.rollen = rollen;
    }





}


