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
 * SoapBerichtType generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="SoapBerichtType")
@Table(name="SoapBerichtType"
)
public class SoapBerichtType  implements java.io.Serializable {


     private String code;
     private String omschrijving;
     private boolean actief;
     private Set<SoapTransactie> soapTransacties = new HashSet<SoapTransactie>(0);

    public SoapBerichtType() {
    }

   
     @Id 

    
    @Column(name="Code", unique=true, nullable=false, length=3)
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }


    
    @Column(name="Omschrijving", nullable=false, length=64)
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
@OneToMany(fetch=FetchType.LAZY, mappedBy="soapBerichtType")
    public Set<SoapTransactie> getSoapTransacties() {
        return this.soapTransacties;
    }
    
    public void setSoapTransacties(Set<SoapTransactie> soapTransacties) {
        this.soapTransacties = soapTransacties;
    }





}


