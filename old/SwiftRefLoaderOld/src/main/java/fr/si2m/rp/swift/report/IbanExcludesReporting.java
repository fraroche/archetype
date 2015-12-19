package fr.si2m.rp.swift.report;

public class IbanExcludesReporting {
	String nomTable = "RNDIBEX";
	public int nbLu = 0;
	public int nbIgnore = 0;
	public int nbVus = 0;
	public int nbMajCorr = 0;
	public int nbAjout = 0;
	public int nbInactif = 0;
	public int nbErreur = 0;
	public int nbErrUnicite = 0;
	@Override
	public String toString() {
		return "\nNom de la table:                                                    " + this.nomTable + 
				"\nNombre d'enregistrements lus dans le fichier:                       " + this.nbLu + 
				"\nNombre d'enregistrements ignorés:                                   " + this.nbIgnore + 
				"\nNombre d'enregistrements vus sans mise à jour autre qu'NO_ID_BATCH: " + this.nbVus + 
				"\nNombre d'enregistrements mis à jour par correction:                 " + this.nbMajCorr + 
				"\nNombre d'enregistrements ajoutés:                                   " + this.nbAjout + 
				"\nNombre d'enregistrements inactivés:                                 " + this.nbInactif + 
				"\nNombre d'enregistrements en erreur:                                 " + this.nbErreur + 
				"\nNombre d'erreurs liées au conrtôle d'unicité post traitement :      " + this.nbErrUnicite;
	}
}
