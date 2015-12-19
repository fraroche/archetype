package fr.si2m.rp.swift.outputs;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.si2m.rp.swift.orm.IbanExcludes;
import fr.si2m.rp.swift.report.IbanExcludesReporting;
import fr.si2m.rp.swift.util.DateUtils;

public class OutputIbanExcludesWriter {
	private static final Logger		logger					= LoggerFactory.getLogger(OutputIbanExcludesWriter.class);

	private static final String			sMaxNoIdBatchQry		= "SELECT MAX(i.noIdBatch) FROM IbanExcludes i";
	private static final String			sIbanExcUpdateClosed	= "UPDATE IbanExcludes i SET i.cdActivite = 'I', i.noIdBatch = :idBatchEncours, i.niModif = :batchName, i.dtModif = :dtModif, i.hrModif = :hrModif WHERE i.cdActivite = 'A' and i.noIdBatch < :idBatchEncours";
	private static final String			sIbanExcFromNatIdQry	= "SELECT i FROM IbanExcludes i WHERE i.cdIbanPaysIso = :cdIbanPaysIso and i.cdIbanNationalId = :cdIbanNationalId and i.cdActivite = 'A'";

	private EntityManagerFactory			emf						= null;
	private EntityManager					em						= null;
	private EntityTransaction				tx						= null;
	private Query							ibanExcFromNatIdQry		= null;
	private Query							ibanExcUpdateClosed 	= null;
	private int							commitStep				= 1;
	private int							step					= 0;
	private final short					idBatchEncours;
	private final Date						dateTraitement;
	private final String					batchName;
	private final IbanExcludesReporting	reporting;

	public OutputIbanExcludesWriter(final int pCommitStep, final EntityManagerFactory pEmf, final IbanExcludesReporting pIbanExcludesReporting, final String pBatchName) {
		this.dateTraitement = DateUtils.getInstance().getCurrentDate();
		this.batchName = pBatchName;
		this.reporting = pIbanExcludesReporting;
		this.emf = pEmf;
		this.commitStep = pCommitStep;
		this.em = this.getEntityManager();
		EntityManager lEm = this.emf.createEntityManager();
		Query lMaxNoIdBatchQry = lEm.createQuery(sMaxNoIdBatchQry);
		List lRes = lMaxNoIdBatchQry.getResultList();
		if ((lRes == null) || lRes.isEmpty() || (lRes.get(0) == null)) {
			this.idBatchEncours = 1;
		} else {
			this.idBatchEncours = (short) (((Short) lRes.get(0)).shortValue() + 1);
		}
		if ((lEm != null) && lEm.isOpen()) {
			lEm.close();
		}
	}

	public void processRow(final IbanExcludes pIbanExcludesFromFile) {
		IbanExcludes lIbanExcInDB = this.getIbanExculdesActif(pIbanExcludesFromFile.getCdIbanPaysIso(), pIbanExcludesFromFile.getCdIbanNationalId());
		// Si clef trouvee:
		if (lIbanExcInDB != null) {
			boolean lBicUnchanged = pIbanExcludesFromFile.getCdIbanBic().equals(lIbanExcInDB.getCdIbanBic());
			// Si pas de difference BIC entre table et fichier
			if (lBicUnchanged) {
				this.miseAjour(lIbanExcInDB);
			} else {
				this.correction(pIbanExcludesFromFile, lIbanExcInDB);
			}
		} else {
			this.ajout(pIbanExcludesFromFile);
		}
	}

	public void inactiveTouteLigneNonLuesDansFichier() {

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.ibanExcUpdateClosed.setParameter("idBatchEncours", this.idBatchEncours);
		this.ibanExcUpdateClosed.setParameter("batchName", this.batchName);
		this.ibanExcUpdateClosed.setParameter("dtModif", this.dateTraitement);
		this.ibanExcUpdateClosed.setParameter("hrModif", new GregorianCalendar().getTime());
		this.ibanExcUpdateClosed.setParameter("idBatchEncours", this.idBatchEncours);
		this.reporting.nbInactif += this.ibanExcUpdateClosed.executeUpdate();

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}
	}

	private void miseAjour(final IbanExcludes pIbanExcInDB) {

		pIbanExcInDB.setNoIdBatch(this.idBatchEncours);
		pIbanExcInDB.setDtModif(this.dateTraitement);
		pIbanExcInDB.setHrModif(new GregorianCalendar().getTime());
		pIbanExcInDB.setNiModif(this.batchName);

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.em.merge(pIbanExcInDB);

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}

		this.reporting.nbVus++;
	}

	private void correction(final IbanExcludes pIbanExcFromFile, final IbanExcludes pIbanExcInDB) {

		pIbanExcInDB.setNoIdBatch(this.idBatchEncours);
		pIbanExcInDB.setDtModif(this.dateTraitement);
		pIbanExcInDB.setHrModif(new GregorianCalendar().getTime());
		pIbanExcInDB.setNiModif(this.batchName);
		pIbanExcInDB.setCdIbanBic(pIbanExcFromFile.getCdIbanBic());

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.em.merge(pIbanExcInDB);

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}

		this.reporting.nbMajCorr++;
	}

	private void ajout(final IbanExcludes pIbanExcludesFromFile) {
		pIbanExcludesFromFile.setNoIdBatch(this.idBatchEncours);
		pIbanExcludesFromFile.setCdActivite("A");
		pIbanExcludesFromFile.setDtCreation(this.dateTraitement);
		pIbanExcludesFromFile.setHrCreation(new GregorianCalendar().getTime());
		pIbanExcludesFromFile.setNiCreation(this.batchName);

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.em.persist(pIbanExcludesFromFile);

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}

		this.reporting.nbAjout++;
	}

	private IbanExcludes getIbanExculdesActif(String pIsoCoutryCode, String pIbanNationalId) {
		IbanExcludes ibanExcInDB = null;

		this.ibanExcFromNatIdQry.setParameter("cdIbanPaysIso", pIsoCoutryCode);
		this.ibanExcFromNatIdQry.setParameter("cdIbanNationalId", pIbanNationalId);
		List<IbanExcludes> resultList = this.ibanExcFromNatIdQry.getResultList();
		if ((resultList != null) && !resultList.isEmpty()) {
			ibanExcInDB = resultList.get(0);
		}
		return ibanExcInDB;
	}

	private EntityManager getEntityManager() {
		if ((this.em != null) && this.em.isOpen()) {
			return this.em;
		}
		this.em = this.emf.createEntityManager();
		this.ibanExcFromNatIdQry = this.em.createQuery(sIbanExcFromNatIdQry);
		this.ibanExcUpdateClosed = this.em.createQuery(sIbanExcUpdateClosed);
		return this.em;
	}

	public void destroy() {
		try {
			if (this.tx.isActive()) {
				this.tx.commit();
			}
			if (this.em.isOpen()) {
				this.em.close();
			}
		} catch (IllegalStateException lE) {
			lE.printStackTrace();
		}
	}

	public boolean isOnCommitStep() {
		return this.step == 0;
	}

	public short getNoIdBatch() {
		return this.idBatchEncours;
	}

}
