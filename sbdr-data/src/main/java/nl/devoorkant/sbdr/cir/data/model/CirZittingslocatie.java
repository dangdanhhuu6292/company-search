package nl.devoorkant.sbdr.cir.data.model;
// Generated Apr 14, 2016 7:26:47 PM by Hibernate Tools 3.2.4.GA


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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * CirZittingslocatie generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="CirCirZittingslocatie")
@Table(name="Cir_Zittingslocatie"
)
public class CirZittingslocatie  implements java.io.Serializable {


     private Integer id;
     private String straat;
     private String huisNummer;
     private String huisNummerToevoeging;
     private String plaats;
     private Set<CirPublicatie> cirPublicaties = new HashSet<CirPublicatie>(0);

    public CirZittingslocatie() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="ID", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }


    
    @Column(name="Straat", nullable=false, length=35)
    public String getStraat() {
        return this.straat;
    }
    
    public void setStraat(String straat) {
        this.straat = straat;
    }


    
    @Column(name="HuisNummer", nullable=false, length=20)
    public String getHuisNummer() {
        return this.huisNummer;
    }
    
    public void setHuisNummer(String huisNummer) {
        this.huisNummer = huisNummer;
    }


    
    @Column(name="HuisNummerToevoeging", length=10)
    public String getHuisNummerToevoeging() {
        return this.huisNummerToevoeging;
    }
    
    public void setHuisNummerToevoeging(String huisNummerToevoeging) {
        this.huisNummerToevoeging = huisNummerToevoeging;
    }


    
    @Column(name="Plaats", nullable=false, length=40)
    public String getPlaats() {
        return this.plaats;
    }
    
    public void setPlaats(String plaats) {
        this.plaats = plaats;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="cirZittingslocatie")
    public Set<CirPublicatie> getCirPublicaties() {
        return this.cirPublicaties;
    }
    
    public void setCirPublicaties(Set<CirPublicatie> cirPublicaties) {
        this.cirPublicaties = cirPublicaties;
    }





}

