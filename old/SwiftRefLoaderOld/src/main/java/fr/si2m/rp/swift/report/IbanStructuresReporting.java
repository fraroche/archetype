package fr.si2m.rp.swift.report;

public class IbanStructuresReporting {
	String nomTable = "RNDIBST";
	public int nbLu = 0;
	public int nbAjout = 0;
	public int nbInactif = 0;
	public int nbErreur = 0;
	public int nbErrUnicite = 0;
	@Override
	public String toString() {
		return "\nNom de la table:                                               " + this.nomTable + 
				"\nNombre d'enregistrements lus dans le fichier:                  " + this.nbLu + 
				"\nNombre d'enregistrements ajoutés:                              " + this.nbAjout + 
				"\nNombre d'enregistrements inactivés:                            " + this.nbInactif + 
				"\nNombre d'enregistrements en erreur:                            " + this.nbErreur + 
				"\nNombre d'erreurs liées au conrtôle d'unicité post traitement : " + this.nbErrUnicite;
	}
}
