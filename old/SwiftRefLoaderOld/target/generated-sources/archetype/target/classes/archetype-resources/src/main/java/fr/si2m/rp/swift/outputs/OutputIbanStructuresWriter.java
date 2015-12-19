#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.outputs;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${groupId}.orm.IbanStructures;
import ${groupId}.report.IbanStructuresReporting;
import ${groupId}.util.DateUtils;

public class OutputIbanStructuresWriter {
	private static final Logger logger = LoggerFactory.getLogger(OutputIbanStructuresWriter.class);

	private static final String			sMaxNoIdBatchQry			= "SELECT MAX(i.noIdBatch) FROM IbanStructures i";
	private static final String			sIbanStructUpdateClosed	= "UPDATE IbanStructures i SET i.cdActivite = 'I', i.noIdBatch = :idBatchEncours, i.niModif = :batchName, i.dtModif = :dtModif, i.hrModif = :hrModif WHERE i.cdActivite = 'A' and i.noIdBatch < :idBatchEncours";
	private EntityManagerFactory			emf							= null;
	private EntityManager					em							= null;
	private EntityTransaction				tx							= null;
	private Query							ibanStructUpdateClosed 		= null;
	private int							commitStep					= 1;
	private int							step						= 0;
	private final short					idBatchEncours;
	private final Date						dateTraitement;
	private final String					batchName;
	private final IbanStructuresReporting 	reporting;

	public OutputIbanStructuresWriter(final int pCommitStep, final EntityManagerFactory pEmf, final IbanStructuresReporting pIbanStructuresReporting, final String pBatchName) {
		this.dateTraitement = DateUtils.getInstance().getCurrentDate();
		this.batchName = pBatchName;
		this.reporting = pIbanStructuresReporting;
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

	public void processRow(final IbanStructures pIbanStructuresFromFile) {
		this.ajout(pIbanStructuresFromFile);
		this.reporting.nbAjout++;
	}

	public void inactiveTouteLigneNonLuesDansFichier() {

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.ibanStructUpdateClosed.setParameter("idBatchEncours", this.idBatchEncours);
		this.ibanStructUpdateClosed.setParameter("batchName", this.batchName);
		this.ibanStructUpdateClosed.setParameter("dtModif", this.dateTraitement);
		this.ibanStructUpdateClosed.setParameter("hrModif", new GregorianCalendar().getTime());
		this.ibanStructUpdateClosed.setParameter("idBatchEncours", this.idBatchEncours);
		this.reporting.nbInactif += this.ibanStructUpdateClosed.executeUpdate();

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}
	}

	private void ajout(final IbanStructures pIbanStructuresFromFile) {
		pIbanStructuresFromFile.setNoIdBatch(this.idBatchEncours);
		pIbanStructuresFromFile.setCdActivite("A");
		pIbanStructuresFromFile.setDtCreation(this.dateTraitement);
		pIbanStructuresFromFile.setHrCreation(new GregorianCalendar().getTime());
		pIbanStructuresFromFile.setNiCreation(this.batchName);

		if ((this.step % this.commitStep) == 0) {
			this.tx = this.em.getTransaction();
			this.tx.begin();
		}

		this.em.persist(pIbanStructuresFromFile);

		if (((++this.step) % this.commitStep) == 0) {
			this.tx.commit();
		}
	}

	private EntityManager getEntityManager() {
		if ((this.em != null) && this.em.isOpen()) {
			return this.em;
		}
		this.em = this.emf.createEntityManager();
		this.ibanStructUpdateClosed = this.em.createQuery(sIbanStructUpdateClosed);
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
