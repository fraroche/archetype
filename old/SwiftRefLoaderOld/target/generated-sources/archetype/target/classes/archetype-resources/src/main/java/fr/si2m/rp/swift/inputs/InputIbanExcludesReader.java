#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.inputs;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${groupId}.orm.IbanExcludes;
import ${groupId}.report.IbanExcludesReporting;

public class InputIbanExcludesReader extends InputFileReader<IbanExcludes> {
	private static final Logger logger = LoggerFactory.getLogger(InputIbanExcludesReader.class);

	enum IbanExcludesFileFieldName {
		MODIFICATION_FLAG("MODIFICATION FLAG"),
		RECORD_KEY("RECORD KEY"),
		COUNTRY_CODE("COUNTRY CODE"),
		IBAN_NATIONAL_ID("IBAN NATIONAL ID"),
		BIC("BIC"),
		VALID_FROM("VALID FROM"),
		FIELD_A("FIELD A"),
		FIELD_B("FIELD B");

		final String value;
		int ordre;

		IbanExcludesFileFieldName(final String pValue) {
			this.value = pValue;
		}

		void setOrdre(final int pOrdre) {
			this.ordre = pOrdre;
		}

		public static IbanExcludesFileFieldName getEnum(final String pValue) {
			if (pValue.equals(MODIFICATION_FLAG.value)) {return MODIFICATION_FLAG;}
			if (pValue.equals(RECORD_KEY       .value)) {return RECORD_KEY       ;}
			if (pValue.equals(COUNTRY_CODE     .value)) {return COUNTRY_CODE     ;}
			if (pValue.equals(IBAN_NATIONAL_ID .value)) {return IBAN_NATIONAL_ID ;}
			if (pValue.equals(BIC              .value)) {return BIC              ;}
			if (pValue.equals(VALID_FROM       .value)) {return VALID_FROM       ;}
			if (pValue.equals(FIELD_A          .value)) {return FIELD_A          ;}
			if (pValue.equals(FIELD_B          .value)) {return FIELD_B          ;}
			return null;
		}

		public static IbanExcludesFileFieldName getEnum(final int pOrdre) {
			if (pOrdre == MODIFICATION_FLAG.ordre) { return MODIFICATION_FLAG;}
			if (pOrdre == RECORD_KEY       .ordre) { return RECORD_KEY       ;}
			if (pOrdre == COUNTRY_CODE     .ordre) { return COUNTRY_CODE     ;}
			if (pOrdre == IBAN_NATIONAL_ID .ordre) { return IBAN_NATIONAL_ID ;}
			if (pOrdre == BIC              .ordre) { return BIC              ;}
			if (pOrdre == VALID_FROM       .ordre) { return VALID_FROM       ;}
			if (pOrdre == FIELD_A          .ordre) { return FIELD_A          ;}
			if (pOrdre == FIELD_B          .ordre) { return FIELD_B          ;}
			return null;
		}

	}

	private final IbanExcludesReporting reporting;

	public InputIbanExcludesReader(final String pSeparator, final String pFilePath, final String pEncoding, IbanExcludesReporting pIbanExcludesReporting, final List<String> pRowCache) throws FileNotFoundException, UnsupportedEncodingException {
		super(pSeparator, pFilePath, pEncoding, pRowCache);
		this.reporting = pIbanExcludesReporting;
	}

	@Override
	protected void setColumnOrdre(final int pColOrdre, final String pColName) {
		IbanExcludesFileFieldName field = IbanExcludesFileFieldName.getEnum(pColName);
		if (field != null) {
			field.setOrdre(pColOrdre);
		}
	}

	@Override
	protected IbanExcludes populateObjectField(int pNumCol, String pColValue, IbanExcludes pObject) {
		if (pObject == null) {
			return null;
		}
		IbanExcludesFileFieldName lField = null;
		lField = IbanExcludesFileFieldName.getEnum(pNumCol);

		// les seules valeurs optionnelles sont: 
		// - BIC
		// - VALID FROM
		// - FIELD A
		// - FIELD B
		switch (lField) {
		case MODIFICATION_FLAG: break;
		case RECORD_KEY: break;
		case COUNTRY_CODE: 
			pObject.setCdIbanPaysIso(pColValue);
			break;
		case IBAN_NATIONAL_ID: 
			pObject.setCdIbanNationalId(pColValue);
			break;
		case BIC: 
			pObject.setCdIbanBic(pColValue);
			break;
		case VALID_FROM: 
			if (pColValue.length() > 0) {
				this.reporting.nbIgnore++;
				logger.debug("Le champ '{}' n'est pas vide: '{}' la ligne sera ignorée.", lField.value, pColValue);
				return null;
			}
			break;
		case FIELD_A: break;
		case FIELD_B: break;
		default:break;
		}
		return pObject;
	}

	@Override
	protected IbanExcludes newObject() {
		return new IbanExcludes();
	}

}
