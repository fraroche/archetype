#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.orm;
// Generated 4 nov. 2013 15:49:58 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * IbanStructures generated by hbm2java
 */
public class IbanStructures  implements java.io.Serializable {


     private int noInterne;
     private String cdIbanPaysIso;
     private short noPosCdIbanPays;
     private Short noLgrCdIbanPays;
     private short noPosCleIban;
     private short noLgrCleIban;
     private short noPosCdBanque;
     private short noLgrCdBanque;
     private Short noPosCdGuichet;
     private short noLgrCdGuichet;
     private short noLgrIbanNationalId;
     private short noPosNumCpt;
     private short noLgrNumCpt;
     private short noLgrIbanTotal;
     private String cdSepaStc;
     private short noIdBatch;
     private String cdActivite;
     private Date dtCreation;
     private Date hrCreation;
     private String niCreation;
     private Date dtModif;
     private Date hrModif;
     private String niModif;
     private Date dtAnnulation;
     private Date hrAnnulation;
     private String niAnnulation;

    public IbanStructures() {
    }

	
    public IbanStructures(int noInterne, String cdIbanPaysIso, short noPosCdIbanPays, short noPosCleIban, short noLgrCleIban, short noPosCdBanque, short noLgrCdBanque, short noLgrCdGuichet, short noLgrIbanNationalId, short noPosNumCpt, short noLgrNumCpt, short noLgrIbanTotal, String cdSepaStc, short noIdBatch, Date dtCreation, Date hrCreation, String niCreation) {
        this.noInterne = noInterne;
        this.cdIbanPaysIso = cdIbanPaysIso;
        this.noPosCdIbanPays = noPosCdIbanPays;
        this.noPosCleIban = noPosCleIban;
        this.noLgrCleIban = noLgrCleIban;
        this.noPosCdBanque = noPosCdBanque;
        this.noLgrCdBanque = noLgrCdBanque;
        this.noLgrCdGuichet = noLgrCdGuichet;
        this.noLgrIbanNationalId = noLgrIbanNationalId;
        this.noPosNumCpt = noPosNumCpt;
        this.noLgrNumCpt = noLgrNumCpt;
        this.noLgrIbanTotal = noLgrIbanTotal;
        this.cdSepaStc = cdSepaStc;
        this.noIdBatch = noIdBatch;
        this.dtCreation = dtCreation;
        this.hrCreation = hrCreation;
        this.niCreation = niCreation;
    }
    public IbanStructures(int noInterne, String cdIbanPaysIso, short noPosCdIbanPays, Short noLgrCdIbanPays, short noPosCleIban, short noLgrCleIban, short noPosCdBanque, short noLgrCdBanque, Short noPosCdGuichet, short noLgrCdGuichet, short noLgrIbanNationalId, short noPosNumCpt, short noLgrNumCpt, short noLgrIbanTotal, String cdSepaStc, short noIdBatch, String cdActivite, Date dtCreation, Date hrCreation, String niCreation, Date dtModif, Date hrModif, String niModif, Date dtAnnulation, Date hrAnnulation, String niAnnulation) {
       this.noInterne = noInterne;
       this.cdIbanPaysIso = cdIbanPaysIso;
       this.noPosCdIbanPays = noPosCdIbanPays;
       this.noLgrCdIbanPays = noLgrCdIbanPays;
       this.noPosCleIban = noPosCleIban;
       this.noLgrCleIban = noLgrCleIban;
       this.noPosCdBanque = noPosCdBanque;
       this.noLgrCdBanque = noLgrCdBanque;
       this.noPosCdGuichet = noPosCdGuichet;
       this.noLgrCdGuichet = noLgrCdGuichet;
       this.noLgrIbanNationalId = noLgrIbanNationalId;
       this.noPosNumCpt = noPosNumCpt;
       this.noLgrNumCpt = noLgrNumCpt;
       this.noLgrIbanTotal = noLgrIbanTotal;
       this.cdSepaStc = cdSepaStc;
       this.noIdBatch = noIdBatch;
       this.cdActivite = cdActivite;
       this.dtCreation = dtCreation;
       this.hrCreation = hrCreation;
       this.niCreation = niCreation;
       this.dtModif = dtModif;
       this.hrModif = hrModif;
       this.niModif = niModif;
       this.dtAnnulation = dtAnnulation;
       this.hrAnnulation = hrAnnulation;
       this.niAnnulation = niAnnulation;
    }
   
    public int getNoInterne() {
        return this.noInterne;
    }
    
    public void setNoInterne(int noInterne) {
        this.noInterne = noInterne;
    }
    public String getCdIbanPaysIso() {
        return this.cdIbanPaysIso;
    }
    
    public void setCdIbanPaysIso(String cdIbanPaysIso) {
        this.cdIbanPaysIso = cdIbanPaysIso;
    }
    public short getNoPosCdIbanPays() {
        return this.noPosCdIbanPays;
    }
    
    public void setNoPosCdIbanPays(short noPosCdIbanPays) {
        this.noPosCdIbanPays = noPosCdIbanPays;
    }
    public Short getNoLgrCdIbanPays() {
        return this.noLgrCdIbanPays;
    }
    
    public void setNoLgrCdIbanPays(Short noLgrCdIbanPays) {
        this.noLgrCdIbanPays = noLgrCdIbanPays;
    }
    public short getNoPosCleIban() {
        return this.noPosCleIban;
    }
    
    public void setNoPosCleIban(short noPosCleIban) {
        this.noPosCleIban = noPosCleIban;
    }
    public short getNoLgrCleIban() {
        return this.noLgrCleIban;
    }
    
    public void setNoLgrCleIban(short noLgrCleIban) {
        this.noLgrCleIban = noLgrCleIban;
    }
    public short getNoPosCdBanque() {
        return this.noPosCdBanque;
    }
    
    public void setNoPosCdBanque(short noPosCdBanque) {
        this.noPosCdBanque = noPosCdBanque;
    }
    public short getNoLgrCdBanque() {
        return this.noLgrCdBanque;
    }
    
    public void setNoLgrCdBanque(short noLgrCdBanque) {
        this.noLgrCdBanque = noLgrCdBanque;
    }
    public Short getNoPosCdGuichet() {
        return this.noPosCdGuichet;
    }
    
    public void setNoPosCdGuichet(Short noPosCdGuichet) {
        this.noPosCdGuichet = noPosCdGuichet;
    }
    public short getNoLgrCdGuichet() {
        return this.noLgrCdGuichet;
    }
    
    public void setNoLgrCdGuichet(short noLgrCdGuichet) {
        this.noLgrCdGuichet = noLgrCdGuichet;
    }
    public short getNoLgrIbanNationalId() {
        return this.noLgrIbanNationalId;
    }
    
    public void setNoLgrIbanNationalId(short noLgrIbanNationalId) {
        this.noLgrIbanNationalId = noLgrIbanNationalId;
    }
    public short getNoPosNumCpt() {
        return this.noPosNumCpt;
    }
    
    public void setNoPosNumCpt(short noPosNumCpt) {
        this.noPosNumCpt = noPosNumCpt;
    }
    public short getNoLgrNumCpt() {
        return this.noLgrNumCpt;
    }
    
    public void setNoLgrNumCpt(short noLgrNumCpt) {
        this.noLgrNumCpt = noLgrNumCpt;
    }
    public short getNoLgrIbanTotal() {
        return this.noLgrIbanTotal;
    }
    
    public void setNoLgrIbanTotal(short noLgrIbanTotal) {
        this.noLgrIbanTotal = noLgrIbanTotal;
    }
    public String getCdSepaStc() {
        return this.cdSepaStc;
    }
    
    public void setCdSepaStc(String cdSepaStc) {
        this.cdSepaStc = cdSepaStc;
    }
    public short getNoIdBatch() {
        return this.noIdBatch;
    }
    
    public void setNoIdBatch(short noIdBatch) {
        this.noIdBatch = noIdBatch;
    }
    public String getCdActivite() {
        return this.cdActivite;
    }
    
    public void setCdActivite(String cdActivite) {
        this.cdActivite = cdActivite;
    }
    public Date getDtCreation() {
        return this.dtCreation;
    }
    
    public void setDtCreation(Date dtCreation) {
        this.dtCreation = dtCreation;
    }
    public Date getHrCreation() {
        return this.hrCreation;
    }
    
    public void setHrCreation(Date hrCreation) {
        this.hrCreation = hrCreation;
    }
    public String getNiCreation() {
        return this.niCreation;
    }
    
    public void setNiCreation(String niCreation) {
        this.niCreation = niCreation;
    }
    public Date getDtModif() {
        return this.dtModif;
    }
    
    public void setDtModif(Date dtModif) {
        this.dtModif = dtModif;
    }
    public Date getHrModif() {
        return this.hrModif;
    }
    
    public void setHrModif(Date hrModif) {
        this.hrModif = hrModif;
    }
    public String getNiModif() {
        return this.niModif;
    }
    
    public void setNiModif(String niModif) {
        this.niModif = niModif;
    }
    public Date getDtAnnulation() {
        return this.dtAnnulation;
    }
    
    public void setDtAnnulation(Date dtAnnulation) {
        this.dtAnnulation = dtAnnulation;
    }
    public Date getHrAnnulation() {
        return this.hrAnnulation;
    }
    
    public void setHrAnnulation(Date hrAnnulation) {
        this.hrAnnulation = hrAnnulation;
    }
    public String getNiAnnulation() {
        return this.niAnnulation;
    }
    
    public void setNiAnnulation(String niAnnulation) {
        this.niAnnulation = niAnnulation;
    }




}


