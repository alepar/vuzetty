#!/bin/sh

GIT_URL=git://github.com/alepar/vuzetty.git
DEPLOY_HOME=/home/torrent/deploy
JNLP_DIR=/home/torrent/vuzetty.alepar.ru/jnlp

M2_HOME=$DEPLOY_HOME/maven3
PATH=$M2_HOME/bin:$PATH
WORK_DIR=$DEPLOY_HOME/work
LOG_DIR=$DEPLOY_HOME/log

echo Cleaning up workdir $WORK_DIR
rm -rf $WORK_DIR

echo Cleaning up logs $LOG_DIR
rm -rf $LOG_DIR
mkdir -p $LOG_DIR

echo Cloning $GIT_URL
git clone $GIT_URL $WORK_DIR > $LOG_DIR/git.log
if [ $? -ne 0 ]; then
  echo Failed to clone
  exit;
fi

echo Compiling vuzetty
cd $WORK_DIR; mvn package -pl vuzetty-client-desktop,vuzetty-server -am > $LOG_DIR/mvn.log
if [ $? -ne 0 ]; then
  echo Failed to compile
  exit;
fi

echo Deploying jnlp
rm -rf $JNLP_DIR
mkdir -p $JNLP_DIR
cp $WORK_DIR/vuzetty-client-desktop/target/jnlp/* $JNLP_DIR
chmod -R a+rX $JNLP_DIR
