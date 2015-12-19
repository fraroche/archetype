import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import fr.si2m.rp.swift.orm.IbanBic;




public class TestJqlQuery {
	private static final String sMaxNoIdBatchQry = "select max(i.noIdBatch) from IbanBic i";
	public static void main(String[] args) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("SwifRefLoader");
		EntityManager em = emf.createEntityManager();
		Query ibanBicFromNatIdQry = em.createQuery("SELECT i FROM IbanBic i WHERE i.cdIbanNationalId = :in");
		//		Query ibanBicFromNatId_ACTIF_Qry = em.createQuery("SELECT i FROM IbanBic i WHERE i.cdIbanPaysIso = :cdIbanPaysIso and i.cdIbanNationalId = :cdIbanNationalId");
		//		Query ibanBicFromNatId_ACTIF_Qry = em.createQuery("SELECT i FROM IbanBic i WHERE i.cdIbanPaysIso = :cdIbanPaysIso and i.cdIbanNationalId = :cdIbanNationalId and i.cdEtat = :cdEtat and (i.dtDebutAppli <= :date and i.dtFinAppli > :date)");
		Query ibanBicFromNatId_ACTIF_Qry = em.createQuery("SELECT i FROM IbanBic i WHERE i.cdIbanPaysIso = :cdIbanPaysIso and i.cdIbanNationalId = :cdIbanNationalId and i.cdEtat = :cdEtat and i.dtDebutAppli <= :date and (i.dtFinAppli is null or i.dtFinAppli >:date)");
		Query ibanFerme = em.createQuery("SELECT i.noInterne FROM IbanBic i WHERE i.dtFinAppli IS NULL and i.noIdBatch = :noIdBatch");
		Query maxNoIdBatchQry = em.createQuery(sMaxNoIdBatchQry);

		//		EntityTransaction tx = em.getTransaction();
		//		tx.begin();

		ibanBicFromNatIdQry.setParameter("in", "20151");
		List<IbanBic> resultList = ibanBicFromNatIdQry.getResultList();
		System.out.println(resultList);

		resultList = maxNoIdBatchQry.getResultList();
		System.out.println(resultList);

		ibanBicFromNatId_ACTIF_Qry.setParameter("cdIbanPaysIso", "FR");
		ibanBicFromNatId_ACTIF_Qry.setParameter("cdIbanNationalId", "1010700177");
		ibanBicFromNatId_ACTIF_Qry.setParameter("cdEtat", " ");
		ibanBicFromNatId_ACTIF_Qry.setParameter("date", new GregorianCalendar().getTime());
		resultList = ibanBicFromNatId_ACTIF_Qry.getResultList();
		System.out.println(resultList);

		ibanFerme.setParameter("noIdBatch", (short)1);
		resultList = ibanFerme.getResultList();
		System.out.println("");

		//		tx.commit();
		em.close();
		emf.close();
		System.out.println(resultList.get(0).getCdIbanBic());
		System.exit(0);
	}
}
