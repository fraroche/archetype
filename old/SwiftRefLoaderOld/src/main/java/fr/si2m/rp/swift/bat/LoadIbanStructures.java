package fr.si2m.rp.swift.bat;

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

import fr.si2m.rp.swift.bat.LoadSwiftRef.ExitValue;
import fr.si2m.rp.swift.inputs.InputIbanStructuresReader;
import fr.si2m.rp.swift.orm.IbanStructures;
import fr.si2m.rp.swift.outputs.OutputIbanStructuresWriter;
import fr.si2m.rp.swift.report.IbanStructuresReporting;

public class LoadIbanStructures {
	private static final Logger 			logger = LoggerFactory.getLogger(LoadIbanStructures.class);

	/* Initialisation */
	private final Configuration			mInputConf;
	private final String					mBatchName;
	private final String					mEncoding;
	private final String					mSeparator;
	private String							mPath;
	private final int						mCommitStep;
	private final IbanStructuresReporting	mReporting;
	private final List<String>				mRowCache;

	public LoadIbanStructures(final Configuration pConfigFile) {
		this.mInputConf = pConfigFile.subset("input.ibanStructure");
		this.mBatchName = pConfigFile.getString("input.batchName");
		this.mEncoding = this.mInputConf.getString("fileEncoding");
		this.mSeparator = this.mInputConf.getString("separator");

		String unzipFolderPath = pConfigFile.getString("input.filePaths.unzipFolder");
		File unzipFolder = new File(unzipFolderPath);
		loop: for (File lFile : unzipFolder.listFiles()) {
			if (lFile.getName().startsWith("IBANSTRUCTURE")) {
				this.mPath = lFile.getAbsolutePath();
				break loop;
			}
		}
		this.mCommitStep = pConfigFile.getInt("output.db.commitStep");
		this.mReporting = new IbanStructuresReporting();
		this.mRowCache = new ArrayList<String>();
	}

	public LoadIbanStructures(final String pConfigFilePath) throws ConfigurationException {
		this(new PropertiesConfiguration(pConfigFilePath));
	}

	public ExitValue run() {
		EntityManagerFactory lEmf = Persistence.createEntityManagerFactory("SwifRefLoader");
		InputIbanStructuresReader lInput = null;
		OutputIbanStructuresWriter lOutput = null;
		IbanStructures lIbanStructuresFromFile = null;

		try {
			lInput = new InputIbanStructuresReader(this.mSeparator, this.mPath, this.mEncoding, this.mReporting, this.mRowCache);
		} catch (FileNotFoundException e) {
			logger.error("Le fichier '{}' n'a pas pu etre ouvert", this.mPath);
			e.printStackTrace();
			return ExitValue.IBANSTRUCTURE_FILE_UNLOADABLE;
		} catch (UnsupportedEncodingException e) {
			logger.error("Le fichier '{}' ne supporte pas l'encodage '{}'", this.mPath, this.mEncoding);
			e.printStackTrace();
			return ExitValue.IBANSTRUCTURE_FILE_ENCODING_NOT_SUPPORTED;
		}
		lOutput = new OutputIbanStructuresWriter(this.mCommitStep, lEmf, this.mReporting, this.mBatchName);

		long t = System.currentTimeMillis();
		while (!lInput.isEof()) {
			try {
				if ((lIbanStructuresFromFile = lInput.getObjectFromLine()) != null) {
					lOutput.processRow(lIbanStructuresFromFile);
					this.mReporting.nbLu++;
				}
			} catch (IOException e) {
				this.mReporting.nbErreur++;
				logger.error("Erreur de lecture de ligne de la ligne '{}'\n{}", lInput.getFileIndex(), e);
			}
		}

		lOutput.inactiveTouteLigneNonLuesDansFichier();

		lOutput.destroy();
		lEmf.close();
		logger.info("Fichier '{}' traité en {} ms.", this.mPath, System.currentTimeMillis() - t);

		return ExitValue.OK;
	}

	public IbanStructuresReporting getReporting() {
		return this.mReporting;
	}

}
