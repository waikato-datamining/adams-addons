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

GROUP=nz.ac.waikato.cms.moa
mvn deploy:deploy-file -DgroupId=$GROUP \
  -DartifactId=moa \
  -Dversion=2020.03.22 \
  -Dpackaging=jar \
  -Dfile=$LIB_DIR/moa-2020.03.22.jar \
  -Dsources=$LIB_DIR/moa-2020.03.22-sources.jar \
  -DgeneratePom.description="MOA: Massive Online Analysis" \
  -DrepositoryId=$REPO \
  -Durl=$REPO_URL

