package fr.si2m.zone.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader implements Configuration {
	private static final Logger		logger		= LoggerFactory.getLogger(PropertiesLoader.class);
	private static PropertiesLoader	sSingleton	= null;
	private static boolean			sInit		= false;

	private Configuration				mConfig		= null;

	public static boolean isInitialized() {
		return sInit;
	}

	public static synchronized void initialize(String pAppPropertiesPath) {
		try {
			sSingleton = new PropertiesLoader(new PropertiesConfiguration(pAppPropertiesPath));
		} catch (ConfigurationException e) {
			logger.error("Erreur lors de l'initialisation de la couche de gestion de proprietes '" + pAppPropertiesPath + "'", e);
		}
		sInit = true;
	}

	public static PropertiesLoader getInstance() {
		return sSingleton;
	}


	public Configuration subset(String pPrefix) {
		return this.mConfig.subset(pPrefix);
	}


	public boolean isEmpty() {
		return this.mConfig.isEmpty();
	}


	public boolean containsKey(String pKey) {
		return this.mConfig.containsKey(pKey);
	}


	public void addProperty(String pKey, Object pValue) {
		this.mConfig.addProperty(pKey, pValue);
	}


	public void setProperty(String pKey, Object pValue) {
		this.mConfig.setProperty(pKey, pValue);
	}


	public void clearProperty(String pKey) {
		this.mConfig.clearProperty(pKey);
	}


	public void clear() {
		this.mConfig.clear();
	}


	public Object getProperty(String pKey) {
		return this.mConfig.getProperty(pKey);
	}


	public Iterator<String> getKeys(String pPrefix) {
		return this.mConfig.getKeys(pPrefix);
	}


	public Iterator<String> getKeys() {
		return this.mConfig.getKeys();
	}


	public Properties getProperties(String pKey) {
		return this.mConfig.getProperties(pKey);
	}


	public boolean getBoolean(String pKey) {
		return this.mConfig.getBoolean(pKey);
	}


	public boolean getBoolean(String pKey, boolean pDefaultValue) {
		return this.mConfig.getBoolean(pKey, pDefaultValue);
	}


	public Boolean getBoolean(String pKey, Boolean pDefaultValue) {
		return this.mConfig.getBoolean(pKey, pDefaultValue);
	}


	public byte getByte(String pKey) {
		return this.mConfig.getByte(pKey);
	}


	public byte getByte(String pKey, byte pDefaultValue) {
		return this.mConfig.getByte(pKey, pDefaultValue);
	}


	public Byte getByte(String pKey, Byte pDefaultValue) {
		return this.mConfig.getByte(pKey, pDefaultValue);
	}


	public double getDouble(String pKey) {
		return this.mConfig.getDouble(pKey);
	}


	public double getDouble(String pKey, double pDefaultValue) {
		return this.mConfig.getDouble(pKey, pDefaultValue);
	}


	public Double getDouble(String pKey, Double pDefaultValue) {
		return this.mConfig.getDouble(pKey, pDefaultValue);
	}


	public float getFloat(String pKey) {
		return this.mConfig.getFloat(pKey);
	}


	public float getFloat(String pKey, float pDefaultValue) {
		return this.mConfig.getFloat(pKey, pDefaultValue);
	}


	public Float getFloat(String pKey, Float pDefaultValue) {
		return this.mConfig.getFloat(pKey, pDefaultValue);
	}


	public int getInt(String pKey) {
		return this.mConfig.getInt(pKey);
	}


	public int getInt(String pKey, int pDefaultValue) {
		return this.mConfig.getInt(pKey, pDefaultValue);
	}


	public Integer getInteger(String pKey, Integer pDefaultValue) {
		return this.mConfig.getInteger(pKey, pDefaultValue);
	}


	public long getLong(String pKey) {
		return this.mConfig.getLong(pKey);
	}


	public long getLong(String pKey, long pDefaultValue) {
		return this.mConfig.getLong(pKey, pDefaultValue);
	}


	public Long getLong(String pKey, Long pDefaultValue) {
		return this.mConfig.getLong(pKey, pDefaultValue);
	}


	public short getShort(String pKey) {
		return this.mConfig.getShort(pKey);
	}


	public short getShort(String pKey, short pDefaultValue) {
		return this.mConfig.getShort(pKey, pDefaultValue);
	}


	public Short getShort(String pKey, Short pDefaultValue) {
		return this.mConfig.getShort(pKey, pDefaultValue);
	}


	public BigDecimal getBigDecimal(String pKey) {
		return this.mConfig.getBigDecimal(pKey);
	}


	public BigDecimal getBigDecimal(String pKey, BigDecimal pDefaultValue) {
		return this.mConfig.getBigDecimal(pKey, pDefaultValue);
	}


	public BigInteger getBigInteger(String pKey) {
		return this.mConfig.getBigInteger(pKey);
	}


	public BigInteger getBigInteger(String pKey, BigInteger pDefaultValue) {
		return this.mConfig.getBigInteger(pKey, pDefaultValue);
	}


	public String getString(String pKey) {
		return this.mConfig.getString(pKey);
	}


	public String getString(String pKey, String pDefaultValue) {
		return this.mConfig.getString(pKey, pDefaultValue);
	}


	public String[] getStringArray(String pKey) {
		return this.mConfig.getStringArray(pKey);
	}


	public List<Object> getList(String pKey) {
		return this.mConfig.getList(pKey);
	}


	public List<Object> getList(String pKey, List<Object> pDefaultValue) {
		return this.mConfig.getList(pKey, pDefaultValue);
	}

	private PropertiesLoader(Configuration pConfig) {
		this.mConfig = pConfig;
	}
}
