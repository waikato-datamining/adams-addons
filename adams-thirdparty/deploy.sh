#!/bin/bash
#
# Imports knir 3rd party libraries into Nexus
#
# Author: FracPete (fracpete at waikato dot ac dot nz)
# Version: $Revision$

HOST=https://adams.cms.waikato.ac.nz
REPO=adams-thirdparty
REPO_URL=$HOST/nexus/content/repositories/$REPO

LIB_DIR=./

GROUP=???
mvn deploy:deploy-file -DgroupId=$GROUP \
  -DartifactId=??? \
  -Dversion=??? \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/???.jar \
  -Dsources=$LIB_DIR/???-sources.jar \
  -DgeneratePom.description="???" \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL

