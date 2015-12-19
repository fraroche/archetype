/**
 * ErrorCodeServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package fr.si2m.fwk;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import fr.si2m.fwk.ack.schema.v1_0_2.AckType;
import fr.si2m.fwk.ack.schema.v1_0_2.CodeRetourType;
import fr.si2m.fwk.error.code.v1_0_0.Error;
import fr.si2m.fwk.error.code.v1_0_0.ErrorCode;
import fr.si2m.fwk.error.code.v1_0_0.ErrorList;
import fr.si2m.fwk.error.code.v1_0_0.ErrorListResponseType;
import fr.si2m.fwk.error.code.v1_0_0.ErrorListType;
import fr.si2m.fwk.error.code.v1_0_0.ErrorListTypeSequence;
import fr.si2m.fwk.error.code.v1_0_0.ErrorResponseType;
import fr.si2m.fwk.error.code.v1_0_0.ErrorType;
import fr.si2m.zone.erreurs.ErreurCode;
import fr.si2m.zone.erreurs.GestionErreurs;

/**
 * ErrorCodeServiceSkeleton java skeleton for the axisService
 */
public class ErrorCodeServiceSkeleton {

	/**
	 * Auto generated method signature
	 * 
	 * @param pErrorCode
	 */

	public Error getError(ErrorCode pErrorCode) {
		ResourceBundle lErrorBundle = GestionErreurs.getInstance().getBundle();
		String lLibelleErreur = lErrorBundle.getString(pErrorCode.getErrorCode());
		Error lError = null;
		ErrorResponseType lErrorResponseType = null;
		ErrorType lErrorType = null;

		AckType lAckType = null;
		CodeRetourType lCodeRetourType = null;

		(lErrorType = new ErrorType()).setCode(pErrorCode.getErrorCode());
		lErrorType.setMessage(lLibelleErreur);
		(lErrorResponseType = new ErrorResponseType()).setError(lErrorType);
		(lError = new Error()).setError(lErrorResponseType);

		(lCodeRetourType = new CodeRetourType()).setCodeRetourType(ErreurCode.CODE_RETOUR_OK.toString());
		(lAckType = new AckType()).setCodeRetour(lCodeRetourType);
		lErrorResponseType.setAcknowledgement(lAckType);

		return lError;
	}

	/**
	 * Auto generated method signature
	 * 
	 */

	public ErrorList getErrorList() {

		ErrorList lErrorList = null;
		ErrorListResponseType lErrorListResponseType = null;
		ErrorListType lErrorListType = null;
		ErrorListTypeSequence lErrorListTypeSequence[] = null;
		//		Error lError = null;
		//		ErrorResponseType lErrorResponseType = null;
		ErrorType lErrorType = null;

		AckType lAckType = null;
		CodeRetourType lCodeRetourType = null;

		ResourceBundle lBundle = GestionErreurs.getInstance().getBundle();
		Enumeration<String> lKeys = lBundle.getKeys();
		List<ErrorListTypeSequence> lErrorListTypeSeqList = new ArrayList<ErrorListTypeSequence>();
		int i = 0;
		while (lKeys.hasMoreElements()) {
			String lKey = lKeys.nextElement();
			String lMessageErreur = lBundle.getString(lKey);
			(lErrorType = new ErrorType()).setCode(lKey);
			lErrorType.setMessage(lMessageErreur);
			ErrorListTypeSequence lErroListTypeSeq = new ErrorListTypeSequence();
			lErroListTypeSeq.setError(lErrorType);
			lErrorListTypeSeqList.add(lErroListTypeSeq);
		}
		lErrorListTypeSequence = (ErrorListTypeSequence[]) lErrorListTypeSeqList.toArray();

		//		Set<String> lKeySet = lBundle.keySet();
		//		lErrorListTypeSequence = new ErrorListTypeSequence[lKeySet.size()];
		//		int i = 0;
		//		for (String lKey : lKeySet) {
		//			String lLibelleErreur = GestionErreurs.getLibelleErreur(lKey);
		//
		//			(lErrorType = new ErrorType()).setCode(lKey);
		//			lErrorType.setMessage(lLibelleErreur);
		//			//			(lErrorResponseType = new ErrorResponseType()).setError(lErrorType);
		//			//			(lError = new Error()).setError(lErrorResponseType);
		//			(lErrorListTypeSequence[i++] = new ErrorListTypeSequence()).setError(lErrorType);
		//		}

		(lErrorListType = new ErrorListType()).setErrorListTypeSequence(lErrorListTypeSequence);
		(lErrorListResponseType = new ErrorListResponseType()).setErrorList(lErrorListType);
		(lCodeRetourType = new CodeRetourType()).setCodeRetourType(ErreurCode.CODE_RETOUR_OK.toString());
		(lAckType = new AckType()).setCodeRetour(lCodeRetourType);
		lErrorListResponseType.setAcknowledgement(lAckType);
		(lErrorList = new ErrorList()).setErrorList(lErrorListResponseType);
		return lErrorList;
	}

}
