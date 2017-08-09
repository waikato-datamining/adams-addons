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

GROUP=com.github.microsoft
mvn deploy:deploy-file -DgroupId=$GROUP \
  -DartifactId=cntk \
  -Dversion=2.1 \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/cntk.jar \
  -Dsources=$LIB_DIR/cntk-sources.jar \
  -DgeneratePom.description="CNTK Java bindings for prediction time" \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL

