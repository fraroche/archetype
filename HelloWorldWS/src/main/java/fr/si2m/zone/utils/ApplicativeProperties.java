package fr.si2m.zone.utils;

public enum ApplicativeProperties {
	MSG_ERREURS_PATH("msgErreurs.path"),
	LOG_CONF_PATH("log.conf.path"),
	DATE_FORMAT("dateFormat");

	private String	name	= "";

	ApplicativeProperties(final String pName) {
		this.name = pName;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
