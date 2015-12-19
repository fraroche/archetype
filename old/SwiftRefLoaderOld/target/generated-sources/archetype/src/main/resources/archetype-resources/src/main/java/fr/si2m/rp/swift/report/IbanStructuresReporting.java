#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.report;

public class IbanStructuresReporting {
	String nomTable = "RNDIBST";
	public int nbLu = 0;
	public int nbAjout = 0;
	public int nbInactif = 0;
	public int nbErreur = 0;
	public int nbErrUnicite = 0;
	@Override
	public String toString() {
		return "${symbol_escape}nNom de la table:                                               " + this.nomTable + 
				"${symbol_escape}nNombre d'enregistrements lus dans le fichier:                  " + this.nbLu + 
				"${symbol_escape}nNombre d'enregistrements ajoutés:                              " + this.nbAjout + 
				"${symbol_escape}nNombre d'enregistrements inactivés:                            " + this.nbInactif + 
				"${symbol_escape}nNombre d'enregistrements en erreur:                            " + this.nbErreur + 
				"${symbol_escape}nNombre d'erreurs liées au conrtôle d'unicité post traitement : " + this.nbErrUnicite;
	}
}
