#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.bat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

import com.sun.servicetag.UnauthorizedAccessException;

import ${groupId}.bat.LoadIbanBic.Mode;
import ${groupId}.util.DateUtils;
import ${groupId}.util.FolderUnzipper;

public class LoadSwiftRef {
	enum ExitValue {
		OK(0),
		WARNING(4),
		FONCTIONNAL_ERROR(8),
		TECHNICAL_ERROR(12),
		FATAL_ERROR(16),

		CONFIG_FILE_UNLOADABLE(50),
		DATA_FILE_ABSENT(51),
		DATA_FILE_UNREADABLE(52),
		IBANPLUS_FILE_ABSENT(53),
		IBANPLUS_FILE_EMPTY(54),
		IBANPLUS_FILE_UNLOADABLE(55),
		IBANPLUS_FILE_ENCODING_NOT_SUPPORTED(56),
		IBANSTRUCTURE_FILE_ABSENT(57),
		IBANSTRUCTURE_FILE_EMPTY(58),
		IBANSTRUCTURE_FILE_UNLOADABLE(59),
		IBANSTRUCTURE_FILE_ENCODING_NOT_SUPPORTED(60),
		IBANEXCLUDES_FILE_ENCODING_NOT_SUPPORTED(61),
		HIBERNATE_EXCEPTION(62),
		UNDEFINED(100);

		public int value;
		ExitValue(int pValue) {
			this.value = pValue;
		}
	}

	private static final Logger 			logger 					= LoggerFactory.getLogger(LoadSwiftRef.class);
	private static final Logger 			loggerReport 			= LoggerFactory.getLogger("reporting");

	private static final String			sDateFormat			= "yyyyMMdd";

	private final Configuration			config;
	private final Configuration			inputConf;
	private final Configuration			outConf;

	private LoadIbanBic					loadIbanBic;

	public LoadSwiftRef(final String pConfigFilePath) throws ConfigurationException, FileNotFoundException, IOException {
		this(new PropertiesConfiguration(pConfigFilePath));
	}

	public LoadSwiftRef(final Configuration pConfigFile) {
		// assume SLF4J is bound to logback in the current environment
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		// print logback's internal status
		StatusPrinter.print(lc);
		this.config = pConfigFile;
		this.inputConf = this.config.subset("input");
		this.outConf = this.config.subset("output");
	}

	public static ExitValue run(final String pConfPath) {
		logger.info("> run()");

		DateUtils.initialize(sDateFormat);
		LoadSwiftRef swiftRefLoader = null;
		try {
			swiftRefLoader = new LoadSwiftRef(pConfPath);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			logger.error("Impossible de charger de fichier de configuration '{}'", pConfPath);
			System.exit(ExitValue.CONFIG_FILE_UNLOADABLE.value);
		}  catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Impossible de charger de fichier de configuration '{}'", pConfPath);
			System.exit(ExitValue.CONFIG_FILE_UNLOADABLE.value);
		}  catch (IOException e) {
			e.printStackTrace();
			logger.error("Impossible de charger de fichier de configuration '{}'", pConfPath);
			System.exit(ExitValue.CONFIG_FILE_UNLOADABLE.value);
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Erreur technique indéterminée", e);
			System.exit(ExitValue.UNDEFINED.value);
		}


		Configuration lFilePaths = swiftRefLoader.inputConf.subset("filePaths");
		String lZipFilePath = lFilePaths.getString("zipFile");
		String lUnzipFolder = lFilePaths.getString("unzipFolder");
		boolean deleteUnzipFolderOnExit = lFilePaths.getBoolean("deleteUnzipFolderOnExit");

		try {
			unzipIbanPlusZipFile(lZipFilePath, lUnzipFolder, deleteUnzipFolderOnExit);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("Impossible de localiser le fichier des données '{}' specifié par la variable '{}' dans le fichier de configuration", lZipFilePath, "input.filePaths.zipFile");
			System.exit(ExitValue.DATA_FILE_ABSENT.value);
		} catch (UnauthorizedAccessException e) {
			e.printStackTrace();
			logger.error("Impossible de charger le fichier des données '{}' specifié par la variable '{}' dans le fichier de configuration", lZipFilePath, "input.filePaths.zipFile");
			System.exit(ExitValue.DATA_FILE_UNREADABLE.value);
		}

		// Verification de la présence des fichiers IBAN plus ou IBAN structure
		// Verification du contenu non vide des fichiers IBAN plus et IBAN structure
		ExitValue checkFiles = checkFilesPresenceNotEmpty(lUnzipFolder);
		if (checkFiles != ExitValue.OK) {
			return checkFiles;
		}

		// Traitement IBBC
		LoadIbanBic loadIbanBic = new LoadIbanBic(swiftRefLoader.config, Mode.FULL);
		ExitValue loadBicPlusIbanResult = loadIbanBic.run();
		if (loadBicPlusIbanResult != ExitValue.OK) {
			return loadBicPlusIbanResult;
		}

		// Traitement IBST
		LoadIbanStructures loadIbanStructures = new LoadIbanStructures(swiftRefLoader.config);
		ExitValue loadIbanStructuresResult = loadIbanStructures.run();
		if (loadIbanStructuresResult != ExitValue.OK) {
			return loadIbanStructuresResult;
		}

		// Traitement IBEX
		LoadIbanExcludes loadIbanExcludes = new LoadIbanExcludes(swiftRefLoader.config);
		ExitValue loadIbanExcludesResult = loadIbanExcludes.run();
		if (loadIbanExcludesResult != ExitValue.OK) {
			return loadIbanExcludesResult;
		}

		loggerReport.info(loadIbanBic.getReporting().toString());
		loggerReport.info(loadIbanStructures.getReporting().toString());
		loggerReport.info(loadIbanExcludes.getReporting().toString());


		logger.info("< run()");
		return ExitValue.OK;
	}

	private static ExitValue checkFilesPresenceNotEmpty(final String pUnzipFolder) {
		logger.info("> checkFilesPresenceNotEmpty()");

		boolean ibanPlusPresent = false;
		boolean ibanStructuresPresent = false;
		File unzipFolder = new File(pUnzipFolder);

		File[] filesInUnzipFolder = unzipFolder.listFiles();
		for (File lFile : filesInUnzipFolder) {
			if (lFile.getName().startsWith("IBANPLUS")) {
				if (isFileEmpty(lFile)) {
					logger.error("Le fichier IBANPLUS '{}' est vide", lFile);
					return ExitValue.IBANPLUS_FILE_EMPTY;
				}
				ibanPlusPresent = true;
			} else if (lFile.getName().startsWith("IBANSTRUCTURE")) {
				if (isFileEmpty(lFile)) {
					logger.error("Le fichier IBANSTRUCTURE '{}' est vide", lFile);
					return ExitValue.IBANSTRUCTURE_FILE_EMPTY;
				}
				ibanStructuresPresent = true;
			}
		}

		logger.info("< checkFilesPresenceNotEmpty()");
		if (ibanPlusPresent && ibanStructuresPresent) {
			return ExitValue.OK;
		} else if (!ibanPlusPresent) {
			logger.error("Le fichier IBANPLUS est absent");
			return ExitValue.IBANPLUS_FILE_ABSENT;
		} else {
			logger.error("Le fichier IBANSTRUCTURE est absent");
			return ExitValue.IBANSTRUCTURE_FILE_ABSENT;
		}

	}

	private static boolean isFileEmpty(final File lFile) {
		InputStream is;
		try {
			is = new FileInputStream(lFile.getAbsolutePath());
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String str = br.readLine();
			if ((str == null) || (str.length() == 0)) {
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	private static void unzipIbanPlusZipFile(final String pZipFilePath, final String pUnzipFolder, final boolean pDeleteUnzipFolderOnExit) throws FileNotFoundException {
		FolderUnzipper folderUnzipper = new FolderUnzipper(pZipFilePath, pDeleteUnzipFolderOnExit);
		File unzipFolder = new File(pUnzipFolder);
		folderUnzipper.unzipToFolder(unzipFolder);
	}
}
