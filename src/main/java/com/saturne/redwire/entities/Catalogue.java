package com.saturne.redwire.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "catalogues")
public class Catalogue { // implements Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idCatalogue")
  private int idCatalogue;

  @Column(name = "Title")
  private String titre;

  @Column(name = "Author")
  private String auteur;

  @Column(name = "creationDate")
  private LocalDate dateCreation;

  //1 catalogue --> * formations
  @OneToMany //(cascade=CascadeType.ALL)
  @JoinColumn(name = "catalogueFormation")
  private Set<Formation> formations = new HashSet<Formation>();

  //Constructeur

  public Catalogue() {
    super();
  }

  public Catalogue(String titre, String auteur, LocalDate dateCreation) {
    this.titre = titre;
    this.auteur = auteur;
    this.dateCreation = dateCreation;
  }

  //Setter et Getter

  public int getIdCatalogue() {
    return idCatalogue;
  }

  public void setIdCatalogue(int idCatalogue) {
    this.idCatalogue = idCatalogue;
  }

  public String getTitre() {
    return titre;
  }

  public void setTitre(String titre) {
    this.titre = titre;
  }

  public String getAuteur() {
    return auteur;
  }

  public void setAuteur(String auteur) {
    this.auteur = auteur;
  }

  public LocalDate getDateCreation() {
    return dateCreation;
  }

  public void setDateCreation(LocalDate dateCreation) {
    this.dateCreation = dateCreation;
  }

  public Set<Formation> getFormations() {
    return formations;
  }

  public void setFormations(Set<Formation> formations) {
    this.formations = formations;
  }

  @Override
  public String toString() {
    return ("Catalogue [idCatalogue=" + idCatalogue + ", titre=" + titre + ", auteur=" + auteur + ", dateCreation=" + dateCreation + "]");
  }

	@Override
	public int hashCode() {
		return Objects.hash(auteur, dateCreation, idCatalogue, titre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Catalogue other = (Catalogue) obj;
		return Objects.equals(auteur, other.auteur) && Objects.equals(dateCreation, other.dateCreation)
				&& idCatalogue == other.idCatalogue && Objects.equals(titre, other.titre);
	}
}
