package nl.devoorkant.sbdr.data.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
//@org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="BedrijfManaged")
@Table(name="BedrijfManaged"
)
public class BedrijfManaged implements java.io.Serializable{

	private Integer bedrijfManagedId;
	private Bedrijf bedrijf;
	private Gebruiker gebruiker;
	private String voornaam;
	private String naam;
	private String emailAdres;
	private String functie;
	private String afdeling;
	private String telefoonNummer;
	private String geslacht;
	private Set<Rol> rollen = new HashSet<Rol>(0);
	private Date datumAangemaakt;
	private Date datumVerwijderd;
	private String gebruikerStatus;
	private boolean actief;
	private String activatieCode;
	private boolean activatieReminderSent;
	
    public BedrijfManaged() {
    }

   
    @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="BedrijfManaged_ID", unique=true, nullable=false)
    public Integer getBedrijfManagedId() {
        return this.bedrijfManagedId;
    }
    
    public void setBedrijfManagedId(Integer bedrijfManagedId) {
        this.bedrijfManagedId = bedrijfManagedId;
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Bedrijf_ID")
    public Bedrijf getBedrijf() {
		return bedrijf;
	}

	public void setBedrijf(Bedrijf bedrijf) {
		this.bedrijf = bedrijf;
	}
	
	@Transient
	public Integer getBedrijfBedrijfId() {
	    return this.bedrijf == null ? null : this.bedrijf.getBedrijfId();
	}
	
	public void setBedrijfBedrijfId(Integer bedrijfId) {
	    if (bedrijfId == null) {
	        this.bedrijf = null;
	    } else {
	        Bedrijf obj = new Bedrijf();
	        obj.setBedrijfId(bedrijfId);
	        this.bedrijf = obj;
	    }
	}	


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Gebruiker_ID")	
	public Gebruiker getGebruiker() {
		return gebruiker;
	}


	public void setGebruiker(Gebruiker gebruiker) {
		this.gebruiker = gebruiker;
	}
	
    @Transient
    public Integer getGebruikerId() {
        return this.gebruiker == null ? null : this.gebruiker.getGebruikerId();
    }

    public void setGebruikerGebruikerId(Integer gebruikerId) {
        if (gebruikerId == null) {
            this.gebruiker = null;
        } else {
            Gebruiker obj = new Gebruiker();
            obj.setGebruikerId(gebruikerId);
            this.gebruiker = obj;
        }
    }
    
    @Column(name="Voornaam", nullable=false, length=50)
    public String getVoornaam() {
        return this.voornaam;
    }
    
    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }


    
    @Column(name="Naam", nullable=false, length=50)
    public String getNaam() {
        return this.naam;
    }
    
    public void setNaam(String naam) {
        this.naam = naam;
    }    
    
    @Column(name="EmailAdres", length=50)
    public String getEmailAdres() {
        return this.emailAdres;
    }
    
    public void setEmailAdres(String emailAdres) {
        this.emailAdres = emailAdres;
    }    
    
    @Column(name="Functie", nullable=false, length=50)
    public String getFunctie() {
        return this.functie;
    }
    
    public void setFunctie(String functie) {
        this.functie = functie;
    }    
    
    @Column(name="Afdeling", length=50)
    public String getAfdeling() {
        return this.afdeling;
    }
    
    public void setAfdeling(String afdeling) {
        this.afdeling = afdeling;
    }


    
    @Column(name="TelefoonNummer", length=40)
    public String getTelefoonNummer() {
        return this.telefoonNummer;
    }
    
    public void setTelefoonNummer(String telefoonNummer) {
        this.telefoonNummer = telefoonNummer;
    }   
    
    @Column(name="Geslacht", nullable=false, length=1)
    public String getGeslacht() {
        return this.geslacht;
    }
    
    public void setGeslacht(String geslacht) {
        this.geslacht = geslacht;
    }    

	@XmlTransient
	@JsonIgnore
	@ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="BedrijfManaged_Rol", catalog="sbdr", joinColumns = { 
    @JoinColumn(name="BedrijfManaged_ID", nullable=false, updatable=false) }, inverseJoinColumns = { 
    @JoinColumn(name="Rol_ID", nullable=false, updatable=false) })
    public Set<Rol> getRollen() {
        return this.rollen;
    }
    
    public void setRollen(Set<Rol> rollen) {
        this.rollen = rollen;
    }    


	public Date getDatumAangemaakt() {
		return datumAangemaakt;
	}


	public void setDatumAangemaakt(Date datumAangemaakt) {
		this.datumAangemaakt = datumAangemaakt;
	}


	public Date getDatumVerwijderd() {
		return datumVerwijderd;
	}


	public void setDatumVerwijderd(Date datumVerwijderd) {
		this.datumVerwijderd = datumVerwijderd;
	}


	public String getGebruikerStatus() {
		return gebruikerStatus;
	}


	public void setGebruikerStatus(String gebruikerStatus) {
		this.gebruikerStatus = gebruikerStatus;
	}


	public boolean isActief() {
		return actief;
	}


	public void setActief(boolean actief) {
		this.actief = actief;
	}


	public String getActivatieCode() {
		return activatieCode;
	}


	public void setActivatieCode(String activatieCode) {
		this.activatieCode = activatieCode;
	}


	public boolean isActivatieReminderSent() {
		return activatieReminderSent;
	}


	public void setActivatieReminderSent(boolean activatieReminderSent) {
		this.activatieReminderSent = activatieReminderSent;
	}	
    
    
}
