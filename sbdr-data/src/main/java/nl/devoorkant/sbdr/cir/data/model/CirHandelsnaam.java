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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * CirHandelsnaam generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="CirCirHandelsnaam")
@Table(name="Cir_Handelsnaam"
)
public class CirHandelsnaam  implements java.io.Serializable {


     private Integer id;
     private String handelsnaam;
     private String nummerKvK;
     private String plaatsKvK;
     private Boolean indHandelsnaamVoorheen;
     private Set<CirInsolventie> cirInsolventies = new HashSet<CirInsolventie>(0);
     private Set<CirAdres> cirAdreses = new HashSet<CirAdres>(0);

    public CirHandelsnaam() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="ID", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }


    
    @Column(name="Handelsnaam", nullable=false, length=200)
    public String getHandelsnaam() {
        return this.handelsnaam;
    }
    
    public void setHandelsnaam(String handelsnaam) {
        this.handelsnaam = handelsnaam;
    }


    
    @Column(name="NummerKvK", length=8)
    public String getNummerKvK() {
        return this.nummerKvK;
    }
    
    public void setNummerKvK(String nummerKvK) {
        this.nummerKvK = nummerKvK;
    }


    
    @Column(name="PlaatsKvK", length=40)
    public String getPlaatsKvK() {
        return this.plaatsKvK;
    }
    
    public void setPlaatsKvK(String plaatsKvK) {
        this.plaatsKvK = plaatsKvK;
    }


    
    @Column(name="IndHandelsnaamVoorheen")
    public Boolean getIndHandelsnaamVoorheen() {
        return this.indHandelsnaamVoorheen;
    }
    
    public void setIndHandelsnaamVoorheen(Boolean indHandelsnaamVoorheen) {
        this.indHandelsnaamVoorheen = indHandelsnaamVoorheen;
    }


@XmlTransient
@JsonIgnore
@ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Cir_Insolventie_Cir_Handelsnaam", catalog="cir", joinColumns = { 
        @JoinColumn(name="Handelsnaam_ID", nullable=false, updatable=false) }, inverseJoinColumns = { 
        @JoinColumn(name="Insolventie_ID", nullable=false, updatable=false) })
    public Set<CirInsolventie> getCirInsolventies() {
        return this.cirInsolventies;
    }
    
    public void setCirInsolventies(Set<CirInsolventie> cirInsolventies) {
        this.cirInsolventies = cirInsolventies;
    }


@XmlTransient
@JsonIgnore
@ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Cir_Handelsnaam_Cir_Adres", catalog="cir", joinColumns = { 
        @JoinColumn(name="Handelsnaam_ID", nullable=false, updatable=false) }, inverseJoinColumns = { 
        @JoinColumn(name="Adres_ID", nullable=false, updatable=false) })
    public Set<CirAdres> getCirAdreses() {
        return this.cirAdreses;
    }
    
    public void setCirAdreses(Set<CirAdres> cirAdreses) {
        this.cirAdreses = cirAdreses;
    }





}


