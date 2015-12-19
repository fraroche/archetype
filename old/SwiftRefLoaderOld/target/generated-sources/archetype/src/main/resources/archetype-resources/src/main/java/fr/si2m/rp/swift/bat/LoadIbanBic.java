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

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TransactionRequiredException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${groupId}.bat.LoadSwiftRef.ExitValue;
import ${groupId}.inputs.InputIbanBicReader;
import ${groupId}.orm.IbanBic;
import ${groupId}.outputs.OutputIbanBicWriter;
import ${groupId}.report.IbanBicReporting;

public class LoadIbanBic {
	private static final Logger 	logger 					= LoggerFactory.getLogger(LoadIbanBic.class);
	private static final Logger 	processedLogger 		= LoggerFactory.getLogger("IbanPlusProcessed");
	private static final Logger 	unprocessedLogger 	= LoggerFactory.getLogger("IbanPlusUnprocessed");

	enum Mode {
		FULL("FULL"),
		DELTA("DELTA");
		private final String mValue;
		String getValue() {
			return this.mValue;
		}
		Mode(final String pValue) {
			this.mValue = pValue;
		}

	}

	/* Initialisation */
	private final Configuration		mInputConf;
	private final String				mBatchName;
	private final String				mEncoding;
	private final String				mSeparator;
	private String						mPath;
	private final int					mCommitStep;
	private final IbanBicReporting		mReporting;
	private final List<String> 		mRowCache;

	public LoadIbanBic(final Configuration pConfigFile, final Mode pMode) {
		this.mInputConf = pConfigFile.subset("input.ibanPlusFull");
		this.mBatchName = pConfigFile.getString("input.batchName");
		this.mEncoding = this.mInputConf.getString("fileEncoding");
		this.mSeparator = this.mInputConf.getString("separator");

		String lUnzipFolderPath = pConfigFile.getString("input.filePaths.unzipFolder");
		File lUnzipFolder = new File(lUnzipFolderPath);
		for (File lFile : lUnzipFolder.listFiles()) {
			if (lFile.getName().startsWith("IBANPLUS")) {
				if (lFile.getName().contains(pMode.getValue())) {
					this.mPath = lFile.getAbsolutePath();
				}
			}
		}
		if (this.mPath == null) {
			loop: for (File lFile : lUnzipFolder.listFiles()) {
				if (lFile.getName().startsWith("IBANPLUS")) {
					this.mPath = lFile.getAbsolutePath();
					break loop;
				}
			}
		}
		this.mCommitStep = pConfigFile.getInt("output.db.commitStep");
		this.mReporting = new IbanBicReporting();
		this.mRowCache = new ArrayList<String>();
	}

	public LoadIbanBic(final String pConfigFilePath, final Mode pMode) throws ConfigurationException {
		this(new PropertiesConfiguration(pConfigFilePath), pMode);
	}

	public ExitValue run() {
		EntityManagerFactory lEmf = Persistence.createEntityManagerFactory("${artifactId}");
		InputIbanBicReader lInput = null;
		OutputIbanBicWriter lOutput = null;
		IbanBic lIbanBicFromFile = null;

		try {
			lInput = new InputIbanBicReader(this.mSeparator, this.mPath, this.mEncoding, this.mReporting, this.mRowCache);
		} catch (FileNotFoundException e) {
			logger.error("Le fichier '{}' n'a pas pu etre ouvert", this.mPath);
			e.printStackTrace();
			return ExitValue.IBANPLUS_FILE_UNLOADABLE;
		} catch (UnsupportedEncodingException e) {
			logger.error("Le fichier '{}' ne supporte pas l'encodage '{}'", this.mPath, this.mEncoding);
			e.printStackTrace();
			return ExitValue.IBANPLUS_FILE_ENCODING_NOT_SUPPORTED;
		}
		lOutput = new OutputIbanBicWriter(this.mCommitStep, lEmf, this.mReporting, this.mBatchName);

		long t = System.currentTimeMillis();
		while (!lInput.isEof()) {
			try {
				if ((lIbanBicFromFile = lInput.getObjectFromLine()) != null) {
					try {
						lOutput.processRow(lIbanBicFromFile);
						this.mReporting.nbLu++;
					} catch (IllegalArgumentException e) {
						this.mReporting.nbErreur++;
					} catch (TransactionRequiredException e) {
						this.mReporting.nbErreur++;
					} catch (EntityExistsException e) {
						this.mReporting.nbErreur++;
					}
					this.flushProcessedLineToFile(lInput, lOutput);
				}
			} catch (IOException e) {
				this.mReporting.nbErreur++;
				int lIndex = lInput.getFileIndex();
				String lLigneInError = this.mRowCache.remove(this.mRowCache.size()-1);
				unprocessedLogger.info("l.{}${symbol_escape}t{}", lIndex, lLigneInError);
				logger.error("Erreur de lecture de ligne de la ligne '{}${symbol_escape}t{}'${symbol_escape}n{}", lIndex, lLigneInError, e);
			}
		}

		lOutput.fermeTouteLigneHorsFichier();

		this.flushProcessedLineToFile(lInput, lOutput);
		lOutput.destroy();
		lEmf.close();
		logger.info("Fichier '{}' traité en {} ms.", this.mPath, System.currentTimeMillis() - t);
		return ExitValue.OK;
	}

	/**
	 * Description:
	 * 
	 * @param pFileReader
	 * @param pDbWriter
	 */
	private void flushProcessedLineToFile(InputIbanBicReader pFileReader, OutputIbanBicWriter pDbWriter) {
		if (pDbWriter.isOnCommitStep() || pFileReader.isEof()) {
			for (String lRow : this.mRowCache) {
				processedLogger.info(lRow);
			}
			this.mRowCache.clear();
		}
	}

	public IbanBicReporting getReporting() {
		return this.mReporting;
	}

	public static void main(String[] args) {
		try {
			LoadIbanBic lSwiftRefLoader = new LoadIbanBic("/home/nta/views/svn/${batchName}/${artifactId}/src/main/resources/db.properties", Mode.FULL);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}



	/*
	public static void main(String[] args) throws ConfigurationException {

		Configuration config = new PropertiesConfiguration("db.properties");
		Configuration mInputConf = config.subset("input");
		Configuration outConf = config.subset("output");
		Configuration dbConf = outConf.subset("db");
		Configuration outputConf = outConf.subset("filePath");
		String mPath = mInputConf.getString("filePath");
		String mEncoding = mInputConf.getString("fileEncoding");
		String mSeparator = mInputConf.getString("mSeparator");
		String firstLine = mInputConf.getString("firstLine");
		String lineDelimiter = outConf.getString("lineDelimiter");
		int mCommitStep = dbConf.getInt("mCommitStep");
		IbanBicReporting mReporting = new IbanBicReporting();
		List<List<String>> mRowCache = new ArrayList<List<String>>();

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("${artifactId}");
		InputFileReader input = new InputFileReader(mSeparator, firstLine, mPath, mEncoding);
		OutputIbanBicWriter output = new OutputIbanBicWriter(mCommitStep, emf, mReporting);

		// checkFile a traiter (verif presence zip, verif presences fichiers dans zip, seul le fichier exclusion peut etre absent=
		// checkFileContent (seul le fichier exclusion peut etre vide)
		// traitements
		// getIdBatchEncours
		//

		List row = null;
		int i = 0;

		long t = System.currentTimeMillis();
		while (!input.isEof()) {
			i++;
			try {
				if ((row = input.getListElementsFromLine()) != null) {
					output.processRow(row);
					mRowCache.add(row);
					// flush to file
					if (output.isOnCommitStep()) {
						StringBuilder outLine = new StringBuilder();
						for (List<String> lRow : mRowCache) {
							for (Iterator<String> lIterator = lRow.iterator(); lIterator.hasNext();) {
								String lString = lIterator.next();
								outLine.append(lString);
								if (lIterator.hasNext()) {
									outLine.append(mSeparator);
								} else {
									outLine.append(lineDelimiter);
								}
							}
							// flush to file
							// TODO

						}
						mRowCache.clear();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		output.destroy();
		emf.close();
		System.out.println(System.currentTimeMillis() - t);
		System.exit(0);
	}
	 */
}
