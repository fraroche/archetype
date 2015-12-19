package fr.si2m.white.batch.bat;

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
import fr.si2m.white.batch.util.DateUtils;

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
		HIBERNATE_EXCEPTION(62),
		UNDEFINED(100);

		public int value;
		ExitValue(int pValue) {
			this.value = pValue;
		}
	}

	private static final Logger 			logger 					= LoggerFactory.getLogger(LoadSwiftRef.class);

	private static final String			sDateFormat			= "yyyyMMdd";

	private final Configuration			config;
	private final Configuration			inputConf;
	private final Configuration			outConf;

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

		Configuration lFilePaths = swiftRefLoader.inputConf.subset("...");

		//...
		// INSERT YOUR CODE HERE
		//...

		logger.info("< run()");
		return ExitValue.OK;
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
}
