#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.report;

public class IbanBicReporting {
	String nomTable = "RNDIBBC";
	public int nbLu = 0;
	public int nbIgnore = 0;
	public int nbVus = 0;
	public int nbMajEvol = 0;
	public int nbMajCorr = 0;
	public int nbAjout = 0;
	public int nbFermeture = 0;
	public int nbErreur = 0;
	public int nbErrUnicite = 0;
	@Override
	public String toString() {
		return "${symbol_escape}nNom de la table:                                                    " + this.nomTable + 
				"${symbol_escape}nNombre d'enregistrements lus dans le fichier:                       " + this.nbLu + 
				"${symbol_escape}nNombre d'enregistrements ignorés:                                   " + this.nbIgnore + 
				"${symbol_escape}nNombre d'enregistrements vus sans mise à jour autre qu'NO_ID_BATCH: " + this.nbVus + 
				"${symbol_escape}nNombre d'enregistrements mis à jour par évolution:                  " + this.nbMajEvol + 
				"${symbol_escape}nNombre d'enregistrements mis à jour par correction:                 " + this.nbMajCorr + 
				"${symbol_escape}nNombre d'enregistrements ajoutés:                                   " + this.nbAjout + 
				"${symbol_escape}nNombre d'enregistrements fermés:                                    " + this.nbFermeture + 
				"${symbol_escape}nNombre d'enregistrements en erreur:                                 " + this.nbErreur + 
				"${symbol_escape}nNombre d'erreurs liées au conrtôle d'unicité post traitement :      " + this.nbErrUnicite;
	}
}
