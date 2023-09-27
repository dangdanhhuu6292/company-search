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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
@Entity(name="Notitie")
@Table(name="Notitie"
)
public class Notitie implements java.io.Serializable{
	private Integer notitieId;
	private Gebruiker gebruiker;
	private Melding meldingOfNotitieAdmin;
	private Melding meldingOfNotitieGebruiker;
	private ContactMoment contactMomentOfNotitie;
	private Date datum;
	private String notitieType;
	private String notitie;
	private Set<Melding> meldingsForMeldingDoorBedrijfId = new HashSet<Melding>(0);
	
    public Notitie() {
    }

   
    @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="Notitie_ID", unique=true, nullable=false)
    public Integer getNotitieId() {
        return this.notitieId;
    }
    
    public void setNotitieId(Integer notitieId) {
        this.notitieId = notitieId;
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
    
@XmlTransient
@JsonIgnore
@OneToOne(fetch=FetchType.LAZY, mappedBy="notitieGebruiker")
    public Melding getMeldingOfNotitieGebruiker() {
        return this.meldingOfNotitieGebruiker;
    }
    
    public void setMeldingOfNotitieGebruiker(Melding meldingOfNotitieGebruiker) {
        this.meldingOfNotitieGebruiker= meldingOfNotitieGebruiker;
    }
    
@XmlTransient
@JsonIgnore
@OneToOne(fetch=FetchType.LAZY, mappedBy="notitieAdmin")
    public Melding getMeldingOfNotitieAdmin() {
        return this.meldingOfNotitieAdmin;
    }
    
    public void setMeldingOfNotitieAdmin(Melding meldingOfNotitieAdmin) {
        this.meldingOfNotitieAdmin= meldingOfNotitieAdmin;
    }
    
@XmlTransient
@JsonIgnore
@OneToOne(fetch=FetchType.LAZY, mappedBy="notitie")
    public ContactMoment getContactMomentOfNotitie() {
        return this.contactMomentOfNotitie;
    }
    
    public void setContactMomentOfNotitie(ContactMoment contactMomentOfNotitie) {
        this.contactMomentOfNotitie = contactMomentOfNotitie;
    }    

	public Date getDatum() {
		return datum;
	}


	public void setDatum(Date datum) {
		this.datum = datum;
	}


	public String getNotitieType() {
		return notitieType;
	}


	public void setNotitieType(String notitieType) {
		this.notitieType = notitieType;
	}


	public String getNotitie() {
		return notitie;
	}


	public void setNotitie(String notitie) {
		this.notitie = notitie;
	}


    
    	
}
