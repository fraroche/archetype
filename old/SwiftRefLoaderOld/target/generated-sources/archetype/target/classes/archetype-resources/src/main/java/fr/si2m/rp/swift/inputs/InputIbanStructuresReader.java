#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.inputs;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${groupId}.orm.IbanStructures;
import ${groupId}.report.IbanStructuresReporting;

public class InputIbanStructuresReader extends InputFileReader<IbanStructures> {
	private static final Logger logger = LoggerFactory.getLogger(InputIbanStructuresReader.class);


	enum IbanStructureFileFieldName {
		TAG("TAG"), 
		MODIFICATION_FLAG("MODIFICATION FLAG"), 
		IBAN_COUNTRY_CODE("IBAN COUNTRY CODE"), 
		IBAN_COUNTRY_CODE_POSITION("IBAN COUNTRY CODE POSITION"), 
		IBAN_COUNTRY_CODE_LENGTH("IBAN COUNTRY CODE LENGTH"), 
		IBAN_CHECK_DIGITS_POSITION("IBAN CHECK DIGITS POSITION"), 
		IBAN_CHECK_DIGITS_LENGTH("IBAN CHECK DIGITS LENGTH"), 
		BANK_IDENTIFIER_POSITION("BANK IDENTIFIER POSITION"), 
		BANK_IDENTIFIER_LENGTH("BANK IDENTIFIER LENGTH"), 
		BRANCH_IDENTIFIER_POSITION("BRANCH IDENTIFIER POSITION"), 
		BRANCH_IDENTIFIER_LENGTH("BRANCH IDENTIFIER LENGTH"), 
		IBAN_NATIONAL_ID_LENGTH("IBAN NATIONAL ID LENGTH"), 
		ACCOUNT_NUMBER_POSITION("ACCOUNT NUMBER POSITION"), 
		ACCOUNT_NUMBER_LENGTH("ACCOUNT NUMBER LENGTH"), 
		IBAN_TOTAL_LENGTH("IBAN TOTAL LENGTH"), 
		SEPA("SEPA"), 
		OPTIONAL_COMMENCE_DATE("OPTIONAL COMMENCE DATE"), 
		MANDATORY_COMMENCE_DATE("MANDATORY COMMENCE DATE");

		final String value;
		int ordre;

		IbanStructureFileFieldName(final String pValue) {
			this.value = pValue;
		}

		void setOrdre(final int pOrdre) {
			this.ordre = pOrdre;
		}

		public static IbanStructureFileFieldName getEnum(final String pValue) {
			if (pValue.equals(TAG                       .value)) {return TAG;}
			if (pValue.equals(MODIFICATION_FLAG         .value)) {return MODIFICATION_FLAG;}
			if (pValue.equals(IBAN_COUNTRY_CODE         .value)) {return IBAN_COUNTRY_CODE;}
			if (pValue.equals(IBAN_COUNTRY_CODE_POSITION.value)) {return IBAN_COUNTRY_CODE_POSITION;}
			if (pValue.equals(IBAN_COUNTRY_CODE_LENGTH  .value)) {return IBAN_COUNTRY_CODE_LENGTH;}
			if (pValue.equals(IBAN_CHECK_DIGITS_POSITION.value)) {return IBAN_CHECK_DIGITS_POSITION;}
			if (pValue.equals(IBAN_CHECK_DIGITS_LENGTH  .value)) {return IBAN_CHECK_DIGITS_LENGTH;}
			if (pValue.equals(BANK_IDENTIFIER_POSITION  .value)) {return BANK_IDENTIFIER_POSITION;}
			if (pValue.equals(BANK_IDENTIFIER_LENGTH    .value)) {return BANK_IDENTIFIER_LENGTH;}
			if (pValue.equals(BRANCH_IDENTIFIER_POSITION.value)) {return BRANCH_IDENTIFIER_POSITION;}
			if (pValue.equals(BRANCH_IDENTIFIER_LENGTH  .value)) {return BRANCH_IDENTIFIER_LENGTH;}
			if (pValue.equals(IBAN_NATIONAL_ID_LENGTH   .value)) {return IBAN_NATIONAL_ID_LENGTH;}
			if (pValue.equals(ACCOUNT_NUMBER_POSITION   .value)) {return ACCOUNT_NUMBER_POSITION;}
			if (pValue.equals(ACCOUNT_NUMBER_LENGTH     .value)) {return ACCOUNT_NUMBER_LENGTH;}
			if (pValue.equals(IBAN_TOTAL_LENGTH         .value)) {return IBAN_TOTAL_LENGTH;}
			if (pValue.equals(SEPA                      .value)) {return SEPA;}
			if (pValue.equals(OPTIONAL_COMMENCE_DATE    .value)) {return OPTIONAL_COMMENCE_DATE;}
			if (pValue.equals(MANDATORY_COMMENCE_DATE   .value)) {return MANDATORY_COMMENCE_DATE;}
			return null;
		}

		public static IbanStructureFileFieldName getEnum(final int pOrdre) {
			if (pOrdre == TAG                        .ordre) {return TAG                          ;}
			if (pOrdre == MODIFICATION_FLAG          .ordre) {return MODIFICATION_FLAG            ;}
			if (pOrdre == IBAN_COUNTRY_CODE          .ordre) {return IBAN_COUNTRY_CODE            ;}
			if (pOrdre == IBAN_COUNTRY_CODE_POSITION .ordre) {return IBAN_COUNTRY_CODE_POSITION   ;}
			if (pOrdre == IBAN_COUNTRY_CODE_LENGTH   .ordre) {return IBAN_COUNTRY_CODE_LENGTH     ;}
			if (pOrdre == IBAN_CHECK_DIGITS_POSITION .ordre) {return IBAN_CHECK_DIGITS_POSITION   ;}
			if (pOrdre == IBAN_CHECK_DIGITS_LENGTH   .ordre) {return IBAN_CHECK_DIGITS_LENGTH     ;}
			if (pOrdre == BANK_IDENTIFIER_POSITION   .ordre) {return BANK_IDENTIFIER_POSITION     ;}
			if (pOrdre == BANK_IDENTIFIER_LENGTH     .ordre) {return BANK_IDENTIFIER_LENGTH       ;}
			if (pOrdre == BRANCH_IDENTIFIER_POSITION .ordre) {return BRANCH_IDENTIFIER_POSITION   ;}
			if (pOrdre == BRANCH_IDENTIFIER_LENGTH   .ordre) {return BRANCH_IDENTIFIER_LENGTH     ;}
			if (pOrdre == IBAN_NATIONAL_ID_LENGTH    .ordre) {return IBAN_NATIONAL_ID_LENGTH      ;}
			if (pOrdre == ACCOUNT_NUMBER_POSITION    .ordre) {return ACCOUNT_NUMBER_POSITION      ;}
			if (pOrdre == ACCOUNT_NUMBER_LENGTH      .ordre) {return ACCOUNT_NUMBER_LENGTH        ;}
			if (pOrdre == IBAN_TOTAL_LENGTH          .ordre) {return IBAN_TOTAL_LENGTH            ;}
			if (pOrdre == SEPA                       .ordre) {return SEPA                         ;}
			if (pOrdre == OPTIONAL_COMMENCE_DATE     .ordre) {return OPTIONAL_COMMENCE_DATE       ;}
			if (pOrdre == MANDATORY_COMMENCE_DATE    .ordre) {return MANDATORY_COMMENCE_DATE      ;}
			return null;
		}
	}

	private final IbanStructuresReporting reporting;

	public InputIbanStructuresReader(final String pSeparator, final String pFilePath, final String pEncoding, final IbanStructuresReporting pIbanStructuresReporting, final List<String> pRowCache) throws FileNotFoundException, UnsupportedEncodingException {
		super(pSeparator, pFilePath, pEncoding, pRowCache);
		this.reporting = pIbanStructuresReporting;
	}

	@Override
	protected void setColumnOrdre(final int pColOrdre, final String pColName) {
		IbanStructureFileFieldName field = IbanStructureFileFieldName.getEnum(pColName);
		if (field != null) {
			field.setOrdre(pColOrdre);
		}
	}

	@Override
	protected IbanStructures populateObjectField(final int pNumCol, final String pColValue, final IbanStructures pObject) {
		if (pObject == null) {
			return null;
		}
		IbanStructureFileFieldName lField = null;
		lField = IbanStructureFileFieldName.getEnum(pNumCol);
		try {
			// les seules valeurs optionnelles sont: 
			// - IBAN COUNTRY CODE LENGTH
			// - BRANCH IDENTIFIER POSITION
			// - OPTIONAL COMMENCE DATE
			// - MANDATORY COMMENCE DATE
			switch (lField) {
			case TAG: break;
			case MODIFICATION_FLAG: 
				if ("D".equals(pColValue)) {
					logger.debug("'{}' = 'D' -> La ligne est ignorée.", lField.value);
					return null;
				}
			case IBAN_COUNTRY_CODE: 
				pObject.setCdIbanPaysIso(pColValue);
				break;
			case IBAN_COUNTRY_CODE_POSITION: 
				pObject.setNoPosCdIbanPays(Short.parseShort(pColValue));
				break;
			case IBAN_COUNTRY_CODE_LENGTH: 
				// Gestion de la chaine vide uniquement pour les valeur facultatives dans la spec technique ${batchName}
				if (pColValue.length() != 0) {
					pObject.setNoLgrCdIbanPays(Short.parseShort(pColValue));
				}
				break;
			case IBAN_CHECK_DIGITS_POSITION: 
				pObject.setNoPosCleIban(Short.parseShort(pColValue));
				break;
			case IBAN_CHECK_DIGITS_LENGTH: 
				pObject.setNoLgrCleIban(Short.parseShort(pColValue));
				break;
			case BANK_IDENTIFIER_POSITION: 
				pObject.setNoPosCdBanque(Short.parseShort(pColValue));
				break;
			case BANK_IDENTIFIER_LENGTH: 
				pObject.setNoLgrCdBanque(Short.parseShort(pColValue));
				break;
			case BRANCH_IDENTIFIER_POSITION: 
				// Gestion de la chaine vide uniquement pour les valeur facultatives dans la spec technique ${batchName}
				if (pColValue.length() != 0) {
					pObject.setNoPosCdGuichet(Short.parseShort(pColValue));
				}
				break;
			case BRANCH_IDENTIFIER_LENGTH: 
				pObject.setNoLgrCdGuichet(Short.parseShort(pColValue));
				break;
			case IBAN_NATIONAL_ID_LENGTH: 
				pObject.setNoLgrIbanNationalId(Short.parseShort(pColValue));
				break;
			case ACCOUNT_NUMBER_POSITION: 
				pObject.setNoPosNumCpt(Short.parseShort(pColValue));
				break;
			case ACCOUNT_NUMBER_LENGTH: 
				pObject.setNoLgrNumCpt(Short.parseShort(pColValue));
				break;
			case IBAN_TOTAL_LENGTH: 
				pObject.setNoLgrIbanTotal(Short.parseShort(pColValue));
				break;
			case SEPA: 
				pObject.setCdSepaStc("Y".equals(pColValue)?"O":pColValue);
				break;
			case OPTIONAL_COMMENCE_DATE: break;
			case MANDATORY_COMMENCE_DATE: break;

			default:break;
			}
		} catch (NumberFormatException e) {
			logger.error("La valeur '{}' pour la colonne '{}' indice '{}' ne peut pas être convertie en 'short'. La ligne l.{} sera ignorée.", pColValue, lField.value, lField.ordre, this.getFileIndex());
			this.reporting.nbErreur++;
			return null;
		}
		return pObject;
	}

	@Override
	protected IbanStructures newObject() {
		return new IbanStructures();
	}

}
