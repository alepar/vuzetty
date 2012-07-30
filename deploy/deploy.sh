#!/bin/sh

GIT_URL=git://github.com/alepar/vuzetty.git
DEPLOY_HOME=/home/torrent/deploy
JNLP_DIR=/home/torrent/vuzetty.alepar.ru/jnlp
AZUREUS_DIR=/home/torrent/azureus

KEYSTORE_PATH=$DEPLOY_HOME/key.store
M2_HOME=$DEPLOY_HOME/maven3
PATH=$M2_HOME/bin:$PATH
WORK_DIR=$DEPLOY_HOME/work
LOG_DIR=$DEPLOY_HOME/log

if [ -z $1 ]; then
  echo "Usage: ./deploy.sh <branch/tag label>"
  exit;
fi

echo "Keystore password: "
stty -echo
read keystore_password
stty echo

echo "Cleaning up logs $LOG_DIR"
rm -rf $LOG_DIR
mkdir -p $LOG_DIR

if [ ! -d $WORK_DIR ]; then
  echo "Checking out $GIT_URL"
  git clone $GIT_URL $WORK_DIR > $LOG_DIR/git_clone.log 2>&1

  if [ $? -ne 0 ]; then
    echo "Failed to git clone"
    exit;
  fi
else
  echo "Updating $GIT_URL"
  cd $WORK_DIR && git checkout master > $LOG_DIR/git_checkout.log 2>&1 && git pull > $LOG_DIR/git_pull.log 2>&1

  if [ $? -ne 0 ]; then
    echo "Failed to git pull"
    exit;
  fi
fi

echo "Switching to label $1"
git checkout $1 > $LOG_DIR/git_checkout.log 2>&1
if [ $? -ne 0 ]; then
  echo "Failed to checkout label $1"
  exit;
fi

echo "Packaging vuzetty"
export KEYSTORE_PATH
export KEYSTORE_PASSWORD=$keystore_password
cd $WORK_DIR && mvn clean > /dev/null && mvn package -pl vuzetty-client-desktop,vuzetty-server -am -P production > $LOG_DIR/mvn.log
exit_code=$?
export KEYSTORE_PASSWORD=""
if [ $exit_code -ne 0 ]; then
  echo "Failed to mvn package"
  exit;
fi

echo "Deploying jnlp"
rm -rf $JNLP_DIR && mkdir -p $JNLP_DIR && cp $WORK_DIR/vuzetty-client-desktop/target/jnlp/* $JNLP_DIR && chmod -R a+rX $JNLP_DIR

a_pid=`ps x | grep Azureus.jar | grep -v LC_ALL | grep -v grep | awk '{ print $1 }'`
if [ -z "$a_pid" ]; then
  echo "Azureus pid not found, skipping stop"
else
  echo "Stopping azureus at pid $a_pid"
  kill $a_pid
  while ps -p $a_pid > /dev/null ; do 
     sleep 1
  done
fi

echo "Deploying vuzetty-server"
rm -rf $AZUREUS_DIR/plugins/vuzetty && cp -R $WORK_DIR/vuzetty-server/target/workdir/plugins/vuzetty $AZUREUS_DIR/plugins

echo "Starting azureus"
sh -c $AZUREUS_DIR/azureus
