package fr.si2m.zone.erreurs;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GestionErreurs {

	private static final Logger			logger		= LoggerFactory.getLogger(GestionErreurs.class);

	private static GestionErreurs			sSingleton	= null;
	private static boolean				sInit		= false;

	private final PropertyResourceBundle	mBundle;

	private GestionErreurs(final PropertyResourceBundle pBundle) {
		this.mBundle = pBundle;
	}

	public static synchronized void initialize(final String pMsgErreursPath) {
		logger.trace(" > initialize()");

		if (sSingleton == null) {
			final ClassLoader lClassLoader = Thread.currentThread().getContextClassLoader();
			final InputStream lInputStream = lClassLoader.getResourceAsStream(pMsgErreursPath);
			if (lInputStream != null) {
				try {
					sSingleton = new GestionErreurs(new PropertyResourceBundle(lInputStream));
					sInit = true;
				} catch (final IOException e) {
					logger.error("Erreur lors de l'initialisation de la couche de gestion des erreurs '" + pMsgErreursPath + "'", e);
				}
			}
		}

		logger.trace(" < initialize()");
	}

	public static boolean isInitialized() {
		return sInit;
	}

	public static GestionErreurs getInstance() {
		return sSingleton;
	}

	public ResourceBundle getBundle() {
		return this.mBundle;
	}

	public String getMessageErreur(final ErreurCode pErreur, final Object... pArgs) {
		logger.trace(" > getMessageErreur()");
		String lOutput = null;
		ResourceBundle lMessages = this.getBundle();
		if (lMessages == null) {
			lOutput = pErreur.toString();
		} else {
			MessageFormat lFormatter = new MessageFormat("");
			lFormatter.applyPattern(lMessages.getString(pErreur.toString()));
			lOutput = lFormatter.format(pArgs);
		}
		logger.trace(" < getMessageErreur()");
		return lOutput;
	}

	public static void main(String[] args) {
		GestionErreurs.initialize("msgErreurs.properties");
		System.out.println(GestionErreurs.getInstance().getMessageErreur(ErreurCode.CODE_RETOUR_OK));
	}
}
