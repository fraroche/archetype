#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.bat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${groupId}.bat.LoadSwiftRef.ExitValue;
import ${groupId}.inputs.InputIbanExcludesReader;
import ${groupId}.orm.IbanExcludes;
import ${groupId}.outputs.OutputIbanExcludesWriter;
import ${groupId}.report.IbanExcludesReporting;

public class LoadIbanExcludes {
	private static final Logger logger = LoggerFactory.getLogger(LoadIbanExcludes.class);

	/* Initialisation */
	private final Configuration			mInputConf;
	private final String					mBatchName;
	private final String					mEncoding;
	private final String					mSeparator;
	private String							mPath;
	private final int						mCommitStep;
	private final IbanExcludesReporting	mReporting;
	private final List<String>				mRowCache;

	public LoadIbanExcludes(final Configuration pConfigFile) {
		this.mInputConf = pConfigFile.subset("input.exclusionListe");
		this.mBatchName = pConfigFile.getString("input.batchName");
		this.mEncoding = this.mInputConf.getString("fileEncoding");
		this.mSeparator = this.mInputConf.getString("separator");

		String lUnzipFolderPath = pConfigFile.getString("input.filePaths.unzipFolder");
		File lUnzipFolder = new File(lUnzipFolderPath);
		loop: for (File lFile : lUnzipFolder.listFiles()) {
			if (lFile.getName().startsWith("EXCLUSIONLIST")) {
				this.mPath = lFile.getAbsolutePath();
				break loop;
			}
		}
		this.mCommitStep = pConfigFile.getInt("output.db.commitStep");
		this.mReporting = new IbanExcludesReporting();
		this.mRowCache = new ArrayList<String>();
	}

	public LoadIbanExcludes(final String pConfigFilePath) throws ConfigurationException {
		this(new PropertiesConfiguration(pConfigFilePath));
	}

	public ExitValue run() {
		EntityManagerFactory lEmf = Persistence.createEntityManagerFactory("${artifactId}");
		InputIbanExcludesReader lInput = null;
		OutputIbanExcludesWriter lOutput = null;
		IbanExcludes lIbanExcFromFile = null;
		boolean lIsFilePresent = true;

		try {
			lInput = new InputIbanExcludesReader(this.mSeparator, this.mPath, this.mEncoding, this.mReporting, this.mRowCache);
		} catch (FileNotFoundException e) {
			// Conformement a la SFD, si le fichier des exclusions est absent, on desactive toutes les exclusions de la table.
			logger.debug("Le fichier des exclusions '{}' n'a pas pu etre ouvert. Toutes les lignes de la table des exclusions vont être inactivées.", this.mPath);
			lIsFilePresent = false;
		} catch (UnsupportedEncodingException e) {
			logger.error("Le fichier '{}' ne supporte pas l'encodage '{}'", this.mPath, this.mEncoding);
			e.printStackTrace();
			return ExitValue.IBANEXCLUDES_FILE_ENCODING_NOT_SUPPORTED;
		}
		lOutput = new OutputIbanExcludesWriter(this.mCommitStep, lEmf, this.mReporting, this.mBatchName);

		long t = System.currentTimeMillis();

		if (lIsFilePresent) {
			while (!lInput.isEof()) {
				try {
					if ((lIbanExcFromFile = lInput.getObjectFromLine()) != null) {
						lOutput.processRow(lIbanExcFromFile);
						this.mReporting.nbLu++;
					}
				} catch (IOException e) {
					this.mReporting.nbErreur++;
					logger.error("Erreur de lecture de ligne de la ligne '{}'${symbol_escape}n{}", lInput.getFileIndex(), e);
				}
			}
		}

		lOutput.inactiveTouteLigneNonLuesDansFichier();

		lOutput.destroy();
		lEmf.close();
		if (lIsFilePresent) {
			logger.info("Fichier '{}' traité en {} ms.", this.mPath, System.currentTimeMillis() - t);
		} else {
			logger.info("Inactivation des champs de la table des exclusions 'RNDIBEX' traité en {} ms.", System.currentTimeMillis() - t);
		}
		return ExitValue.OK;
	}

	public IbanExcludesReporting getReporting() {
		return this.mReporting;
	}

}
