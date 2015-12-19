#!/bin/sh
## Date: 25/11/2013
## Auteur: IDEI080 (CSNT)
##
#######################################################################
## Description:
##    Lancement du batch SwiftRefLoader
##
## Frequence:
##    MENSUELLE
##
#######################################################################
## Modifications :
##      JJ.MM.AAAA par XXX
##      Nature de la modification
#######################################################################

#######################################################################
## Variables à positionner prealablement au lancement du script en
## fonction de l'environnement cible:
#######################################################################
#export DB_SCHEMA=EJRA01A
#export DB_URL=vipalp21.si2m.tec
#export DB_PORT=22446
#export DB_NAME=E2D3
#export DB_USER=SEJRRP00
#export DB_PWD=7CA2D3F9

export PROJECT_NAME=${project.name}
export CHAINE_EXP=${chaine.exp}
export LOGBACK_CONF=/appli/${USER}/batch/etc/${PROJECT_NAME}_logback.xml

REPFIL=/appli/${USER}/batch/${CHAINE_EXP}/file

FICHIER=${REPFIL}/${PROJECT_NAME}
if [ -f ${FICHIER}.properties ]
then
	echo "Le fichier ${FICHIER}.properties existe deja, on est donc dans un cas de reprise"
else
	if [ -f ${FICHIER}.ini ]
	then
		sed -r -e "s/\"/\\\\\"/g" -e "s/.*/echo \"\0\"/g" ${FICHIER}.ini | bash > ${FICHIER}.properties
	else
		echo "Le fichier ${FICHIER}.ini n'est pas present']"
	fi
fi

FICHIER=${REPFIL}/hibernate
if [ -f ${FICHIER}.properties ]
then
	echo "Le fichier ${FICHIER}.properties existe deja, on est donc dans un cas de reprise"
else
	if [ -f ${FICHIER}.ini ]
	then
		sed -r -e "s/\"/\\\\\"/g" -e "s/.*/echo \"\0\"/g" ${FICHIER}.ini | bash > ${FICHIER}.properties
	else
		echo "Le fichier ${FICHIER}.ini n'est pas present']"
	fi
fi

echo "Ligne de lancement: 'java -Xms128m -Xmx512m -Xbootclasspath/a:${REPFIL} -jar /appli/${USER}/batch/script/${PROJECT_NAME}.jar ${REPFIL}/${PROJECT_NAME}.properties'"
java -Xms128m -Xmx512m -Xbootclasspath/a:${REPFIL} -jar /appli/${USER}/batch/script/${PROJECT_NAME}.jar ${REPFIL}/${PROJECT_NAME}.properties
CR=$?
echo $CR

#-------------------------------------------------------------------
#. debut_trt $(basename $0) $*   # Utilitaire technique de debut
#----------------Debut du script applicatif-------------------------
#set -x

#FICHIER=$REPFIL/SwiftRefLoader
#if [ -f ${FICHIER}.properties ]
#then
#   echo "Le fichier ${FICHIER}.properties existe deja, on est donc dans un cas de reprise"
#else
#    if [ -f ${FICHIER}.ini ]
#    then
#        #-- On remplace les variables d'environnement dans le fichier par leur valeur
#        while read ligne
#            do echo -e $(eval echo $ligne)
#        done <${FICHIER}.ini > ${FICHIER}.properties
#     else
#        echo "Pas de fichier ${FICHIER}.ini a implementer avec les variables d'environnement"
#        CR=101
#    fi
#fi

#FICHIER=$REPFIL/hibernate
#if [ -f ${FICHIER}.properties ]
#then
#   echo "Le fichier ${FICHIER}.properties existe deja, on est donc dans un cas de reprise"
#else
#    if [ -f ${FICHIER}.ini ]
#    then
#        #-- On remplace les variables d'environnement dans le fichier par leur valeur
#        while read ligne
#            do echo -e $(eval echo $ligne)
#        done <${FICHIER}.ini > ${FICHIER}.properties
#     else
#        echo "Pas de fichier ${FICHIER}.ini a implementer avec les variables d'environnement"
#        CR=101
#    fi
#fi
#-- (
#-- cd $REPSCRIPT &&
#-- java -Xms128m -Xmx512m -jar ../../script/${PROJECT_NAME}.jar ${REPFIL}/${PROJECT_NAME}.properties
#-- )
#CR=$?
#echo $CR

#-----------------Fin du script applicatif--------------------------
#. fin_trt $CR                   # Utilitaire technique de fin
#-------------------------------------------------------------------