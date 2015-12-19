package fr.si2m.zone.erreurs;

public enum ErreurCode {
	CODE_RETOUR_OK("0000"),
	// TODO:
	// Place your code here

	;

	private String	name	= "";

	ErreurCode(final String pName) {
		this.name = pName;
	}

	@Override
	public String toString() {
		return this.name;
	}
}