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

import fr.si2m.rp.swift.orm.IbanBic;
import fr.si2m.rp.swift.report.IbanBicReporting;
import fr.si2m.rp.swift.util.DateUtils;

public class OutputIbanBicWriter {
	private static final Logger		logger					= LoggerFactory.getLogger(OutputIbanBicWriter.class);
	private static final Logger		loggerF				= LoggerFactory.getLogger("AnomaliesFonctionelles");

	private static final String		sMaxNoIdBatchQry		= "SELECT MAX(i.noIdBatch) FROM IbanBic i";
	private static final String		sIbanBicFromNatIdQry	= "SELECT i FROM IbanBic i WHERE i.cdIbanPaysIso = :cdIbanPaysIso and i.cdIbanNationalId = :cdIbanNationalId and i.cdEtat = :cdEtat and i.dtDebutAppli <= :date and (i.dtFinAppli is null or i.dtFinAppli >:date)";

	//	private static final String		sIbanBicClosed		= "SELECT i.noInterne FROM IbanBic i WHERE i.dtFinAppli IS NULL and i.noIdBatch < :noIdBatchEncours";
	//	private static final String		sIbanBicUpdateClose	= "UPDATE IbanBic i SET i.dtFinAppli = :dtFinAppli, i.noIdBatch = :idBatchEncours, i.niModif = :batchName, i.dtModif = :dtModif, i.hrModif = :hrModif WHERE i.noInterne = :noInterne";

	private static final String 		sIbanBicUpdateClosed = "UPDATE IbanBic i SET i.dtFinAppli = :dtFinAppli, i.noIdBatch = :idBatchEncours, i.niModif = :batchName, i.dtModif = :dtModif, i.hrModif = :hrModif WHERE i.dtFinAppli IS NULL and i.noIdBatch < :noIdBatchEncours";

	private EntityManagerFactory		emf						= null;
	private EntityManager				em						= null;
	private EntityTransaction			tx						= null;
	private Query						ibanBicFromNatIdQry		= null;
	//	private Query						ibanBicUpdateClose 		= null;
	private Query						ibanBicUpdateClosed 	= null;
	private int						commitStep				= 1;
	private int						step					= 0;
	private final short				idBatchEncours;
	private final Date					dateTraitement;
	private final Date					dateTraitementPlusUn;
	private final String				batchName;
	private final IbanBicReporting		reporting;

	public OutputIbanBicWriter(final int pCommitStep, final EntityManagerFactory pEmf, final IbanBicReporting pIbanBicReporting, final String pBatchName) {
		this.dateTraitement = DateUtils.getInstance().getCurrentDate();
		this.dateTraitementPlusUn = DateUtils.addDayMonthYear(this.dateTraitement, 1, 0, 0);
		this.batchName = pBatchName;
		this.reporting = pIbanBicReporting;
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

	private EntityManager getEntityManager() {
		if ((this.em != null) && this.em.isOpen()) {
			return this.em;
		}
		this.em = this.emf.createEntityManager();
		this.ibanBicFromNatIdQry = this.em.createQuery(sIbanBicFromNatIdQry);
		//		this.ibanBicUpdateClose = this.em.createQuery(sIbanBicUpdateClose);
		this.ibanBicUpdateClosed = this.em.createQuery(sIbanBicUpdateClosed);
		return this.em;
	}

	public void processRow(final IbanBic pIbanBicFromFile) {

		IbanBic lIbanBicInDB = this.getIbanBicActif(pIbanBicFromFile.getCdIbanPaysIso(), pIbanBicFromFile.getCdIbanNationalId(), this.dateTraitement);

		if (lIbanBicInDB != null) {
			if (!pIbanBicFromFile.getCdIbanBic().equals(lIbanBicInDB.getCdIbanBic())) {
				// evolution de la ligne
				this.evolution(pIbanBicFromFile, lIbanBicInDB);
				this.reporting.nbMajEvol++;
			} else {
				boolean correction = false;
				if (!pIbanBicFromFile.getLiNomBanque().equals(lIbanBicInDB.getLiNomBanque()) || 
						!pIbanBicFromFile.getLiNomPays().equals(lIbanBicInDB.getLiNomPays()) || 
						!pIbanBicFromFile.getCdRoutingBic().equals(lIbanBicInDB.getCdRoutingBic()) || 
						!(((pIbanBicFromFile.getLiSerCtxt() == null) && (lIbanBicInDB.getLiSerCtxt() == null)) 
								|| ((pIbanBicFromFile.getLiSerCtxt() !=null) && (lIbanBicInDB.getLiSerCtxt() != null) && pIbanBicFromFile.getLiSerCtxt().equals(lIbanBicInDB.getLiSerCtxt())))) {
					correction = true;
				}
				if (correction) {
					this.correction(pIbanBicFromFile, lIbanBicInDB);
					this.reporting.nbMajCorr++;
				} else {
					lIbanBicInDB.setNoIdBatch(this.idBatchEncours);
					this.em.merge(lIbanBicInDB);
					this.reporting.nbVus++;
				}
			}
		} else {
			this.ajout(pIbanBicFromFile);
			this.reporting.nbAjout++;
		}
	}

	/**
	 * Description:
	 */
	public void fermeTouteLigneHorsFichier() {

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.ibanBicUpdateClosed.setParameter("dtFinAppli", this.dateTraitement);
		this.ibanBicUpdateClosed.setParameter("idBatchEncours", this.idBatchEncours);
		this.ibanBicUpdateClosed.setParameter("batchName", this.batchName);
		this.ibanBicUpdateClosed.setParameter("dtModif", this.dateTraitement);
		this.ibanBicUpdateClosed.setParameter("hrModif", new GregorianCalendar().getTime());
		this.ibanBicUpdateClosed.setParameter("noIdBatchEncours", this.getNoIdBatch());
		this.reporting.nbFermeture += this.ibanBicUpdateClosed.executeUpdate();

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}
	}

	/**
	 * Description:
	 * 
	 * @param pCdIbanPaysIso
	 * @param pIbanNationalId
	 * @param pDateRecherche
	 * @return
	 */
	private IbanBic getIbanBicActif(final String pCdIbanPaysIso, final String pIbanNationalId, final Date pDateRecherche) {
		IbanBic ibanBicInDB = null;

		this.ibanBicFromNatIdQry.setParameter("cdIbanPaysIso", pCdIbanPaysIso);
		this.ibanBicFromNatIdQry.setParameter("cdIbanNationalId", pIbanNationalId);
		this.ibanBicFromNatIdQry.setParameter("cdEtat", " ");
		this.ibanBicFromNatIdQry.setParameter("date", pDateRecherche);
		List<IbanBic> resultList = this.ibanBicFromNatIdQry.getResultList();
		if ((resultList != null) && !resultList.isEmpty()) {
			ibanBicInDB = resultList.get(0);
		}
		return ibanBicInDB;
	}

	private void ajout(final IbanBic pIbanBicFromFile) {
		pIbanBicFromFile.setDtDebutAppli(this.dateTraitementPlusUn);
		pIbanBicFromFile.setNoIdBatch(this.idBatchEncours);
		pIbanBicFromFile.setCdEtat(" ");
		pIbanBicFromFile.setDtCreation(this.dateTraitement);
		pIbanBicFromFile.setHrCreation(new GregorianCalendar().getTime());
		pIbanBicFromFile.setNiCreation(this.batchName);

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.em.persist(pIbanBicFromFile);

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}
	}

	/**
	 * Description:
	 * 
	 * @param pIbanBicFromFile
	 * @param pIbanBicInDB
	 */
	private void evolution(final IbanBic pIbanBicFromFile, final IbanBic pIbanBicInDB) {
		pIbanBicInDB.setDtFinAppli(this.dateTraitement);
		pIbanBicInDB.setNoIdBatch(this.idBatchEncours);
		pIbanBicInDB.setNiModif(this.batchName);
		pIbanBicInDB.setDtModif(this.dateTraitement);
		pIbanBicInDB.setHrModif(new GregorianCalendar().getTime());

		pIbanBicFromFile.setDtDebutAppli(this.dateTraitementPlusUn);
		pIbanBicFromFile.setNoIdBatch(this.idBatchEncours);
		pIbanBicFromFile.setCdEtat(" ");
		pIbanBicFromFile.setDtCreation(this.dateTraitement);
		pIbanBicFromFile.setHrCreation(new GregorianCalendar().getTime());
		pIbanBicFromFile.setNiCreation(this.batchName);

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.em.merge(pIbanBicInDB);
		this.em.persist(pIbanBicFromFile);

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}
	}

	/**
	 * Description:
	 * 
	 * @param pIbanBicFromFile
	 * @param pIbanBicInDB
	 */
	private void correction(final IbanBic pIbanBicFromFile, IbanBic pIbanBicInDB) {
		pIbanBicInDB.setCdEtat("M");
		pIbanBicInDB.setNoIdBatch(this.idBatchEncours);
		pIbanBicInDB.setNiModif(this.batchName);
		pIbanBicInDB.setDtModif(this.dateTraitement);
		pIbanBicInDB.setHrModif(new GregorianCalendar().getTime());

		pIbanBicFromFile.setDtDebutAppli(pIbanBicInDB.getDtDebutAppli());
		pIbanBicFromFile.setCdIbanBic(pIbanBicInDB.getCdIbanBic());
		pIbanBicFromFile.setCdIbanNationalId(pIbanBicInDB.getCdIbanNationalId());
		pIbanBicFromFile.setCdPaysIso(pIbanBicInDB.getCdPaysIso());
		pIbanBicFromFile.setCdIbanPaysIso(pIbanBicInDB.getCdIbanPaysIso());

		pIbanBicFromFile.setNoIdBatch(this.idBatchEncours);
		pIbanBicFromFile.setCdEtat(" ");
		pIbanBicFromFile.setDtCreation(this.dateTraitement);
		pIbanBicFromFile.setHrCreation(new GregorianCalendar().getTime());
		pIbanBicFromFile.setNiCreation(this.batchName);

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.em.merge(pIbanBicInDB);
		this.em.persist(pIbanBicFromFile);

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}
	}

	//	public void fermeTouteLigneHorsFichier() {
	//		List<Integer> lFermetures = this.getFermetures();
	//		for (Integer lNoInterne : lFermetures) {
	//			this.fermeture(lNoInterne);
	//		}
	//	}
	//	/**
	//	 * Description:
	//	 * 
	//	 * @param pNoInterne
	//	 */
	//	private void fermeture(Integer pNoInterne) {
	//
	//		if ((this.step % this.commitStep) == 0) {
	//			this.tx = this.em.getTransaction();
	//			this.tx.begin();
	//		}
	//
	//		this.ibanBicUpdateClose.setParameter("dtFinAppli", this.dateTraitement);
	//		this.ibanBicUpdateClose.setParameter("idBatchEncours", this.idBatchEncours);
	//		this.ibanBicUpdateClose.setParameter("batchName", this.batchName);
	//		this.ibanBicUpdateClose.setParameter("dtModif", this.dateTraitement);
	//		this.ibanBicUpdateClose.setParameter("hrModif", new GregorianCalendar().getTime());
	//		this.ibanBicUpdateClose.setParameter("noInterne", pNoInterne);
	//		this.ibanBicUpdateClose.executeUpdate();
	//
	//		if (((++this.step) % this.commitStep) == 0) {
	//			this.tx.commit();
	//		}
	//	}
	//
	//	private List<Integer> getFermetures() {
	//		Query fermetures = this.em.createQuery(sIbanBicClosed);
	//		fermetures.setParameter("noIdBatchEncours", this.getNoIdBatch());
	//		List<Integer> res = fermetures.getResultList();
	//		return res;
	//	}

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
