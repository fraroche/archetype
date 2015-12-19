package fr.si2m.rp.swift.inputs;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.si2m.rp.swift.orm.IbanBic;
import fr.si2m.rp.swift.report.IbanBicReporting;

public class InputIbanBicReader extends InputFileReader<IbanBic> {
	private static final Logger logger = LoggerFactory.getLogger(InputIbanBicReader.class);

	enum IbanBicFileFieldName {
		MODIFICATION_FLAG("MODIFICATION FLAG"),
		RECORD_KEY("RECORD KEY"),
		INSTITUTION_NAME("INSTITUTION NAME"),
		COUNTRY_NAME("COUNTRY NAME"),
		ISO_COUNTRY_CODE("ISO COUNTRY CODE"),
		IBAN_ISO_COUNTRY_CODE("IBAN ISO COUNTRY CODE"),
		IBAN_BIC("IBAN BIC"),
		ROUTING_BIC("ROUTING BIC"),
		IBAN_NATIONAL_ID("IBAN NATIONAL ID"),
		SERVICE_CONTEXT("SERVICE CONTEXT"),
		FIELD_A("FIELD A"),
		FIELD_B("FIELD B");

		final String value;
		int ordre;

		IbanBicFileFieldName(final String pValue) {
			this.value = pValue;
		}

		void setOrdre(final int pOrdre) {
			this.ordre = pOrdre;
		}

		public static IbanBicFileFieldName getEnum(final String pValue) {
			if (pValue.equals(MODIFICATION_FLAG.value)) {
				return MODIFICATION_FLAG;
			}
			if (pValue.equals(RECORD_KEY.value)) {
				return RECORD_KEY;
			}
			if (pValue.equals(INSTITUTION_NAME.value)) {
				return INSTITUTION_NAME;
			}
			if (pValue.equals(COUNTRY_NAME.value)) {
				return COUNTRY_NAME;
			}
			if (pValue.equals(ISO_COUNTRY_CODE.value)) {
				return ISO_COUNTRY_CODE;
			}
			if (pValue.equals(IBAN_ISO_COUNTRY_CODE.value)) {
				return IBAN_ISO_COUNTRY_CODE;
			}
			if (pValue.equals(IBAN_BIC.value)) {
				return IBAN_BIC;
			}
			if (pValue.equals(ROUTING_BIC.value)) {
				return ROUTING_BIC;
			}
			if (pValue.equals(IBAN_NATIONAL_ID.value)) {
				return IBAN_NATIONAL_ID;
			}
			if (pValue.equals(SERVICE_CONTEXT.value)) {
				return SERVICE_CONTEXT;
			}
			if (pValue.equals(FIELD_A.value)) {
				return FIELD_A;
			}
			if (pValue.equals(FIELD_B.value)) {
				return FIELD_B;
			}
			return null;
		}

		public static IbanBicFileFieldName getEnum(final int pOrdre) {
			if (pOrdre == MODIFICATION_FLAG.ordre) {
				return MODIFICATION_FLAG;
			}
			if (pOrdre == RECORD_KEY.ordre) {
				return RECORD_KEY;
			}
			if (pOrdre == INSTITUTION_NAME.ordre) {
				return INSTITUTION_NAME;
			}
			if (pOrdre == COUNTRY_NAME.ordre) {
				return COUNTRY_NAME;
			}
			if (pOrdre == ISO_COUNTRY_CODE.ordre) {
				return ISO_COUNTRY_CODE;
			}
			if (pOrdre == IBAN_ISO_COUNTRY_CODE.ordre) {
				return IBAN_ISO_COUNTRY_CODE;
			}
			if (pOrdre == IBAN_BIC.ordre) {
				return IBAN_BIC;
			}
			if (pOrdre == ROUTING_BIC.ordre) {
				return ROUTING_BIC;
			}
			if (pOrdre == IBAN_NATIONAL_ID.ordre) {
				return IBAN_NATIONAL_ID;
			}
			if (pOrdre == SERVICE_CONTEXT.ordre) {
				return SERVICE_CONTEXT;
			}
			if (pOrdre == FIELD_A.ordre) {
				return FIELD_A;
			}
			if (pOrdre == FIELD_B.ordre) {
				return FIELD_B;
			}
			return null;
		}
	}

	private final IbanBicReporting reporting;

	public InputIbanBicReader(final String pSeparator, final String pFilePath, final String pEncoding, final IbanBicReporting pIbanBicReporting, final List<String> pRowCache) throws FileNotFoundException, UnsupportedEncodingException {
		super(pSeparator, pFilePath, pEncoding, pRowCache);
		this.reporting = pIbanBicReporting;
	}

	@Override
	protected void setColumnOrdre(final int pColOrdre, final String pColName) {
		IbanBicFileFieldName field = IbanBicFileFieldName.getEnum(pColName);
		if (field != null) {
			field.setOrdre(pColOrdre);
		}
	}

	@Override
	protected IbanBic populateObjectField(int pNumCol, String pColValue, IbanBic pObject) {
		if (pObject == null) {
			return null;
		}
		IbanBicFileFieldName lField = null;
		lField = IbanBicFileFieldName.getEnum(pNumCol);
		switch (lField) {
		case MODIFICATION_FLAG: break;
		case RECORD_KEY: break;
		case INSTITUTION_NAME: 
			pObject.setLiNomBanque(pColValue);
			break;
		case COUNTRY_NAME: 
			pObject.setLiNomPays(pColValue);
			break;
		case ISO_COUNTRY_CODE: 
			pObject.setCdPaysIso(pColValue);
			break;
		case IBAN_ISO_COUNTRY_CODE: 
			pObject.setCdIbanPaysIso(pColValue);
			break;
		case IBAN_BIC: 
			pObject.setCdIbanBic(pColValue);
			break;
		case ROUTING_BIC: 
			pObject.setCdRoutingBic(pColValue);
			break;
		case IBAN_NATIONAL_ID: 
			pObject.setCdIbanNationalId(pColValue);
			break;
		case SERVICE_CONTEXT: 
			pObject.setLiSerCtxt(pColValue);
			break;
		case FIELD_A: 
			if (pColValue.length() > 0) {
				this.reporting.nbIgnore++;
				logger.debug("Le champ '{}' n'est pas vide: '{}' la ligne sera ignorée.", lField.value, pColValue);
				return null;
			}
			break;
		case FIELD_B: break;
		default:break;
		}
		return pObject;
	}

	@Override
	protected IbanBic newObject() {
		return new IbanBic();
	}

}
