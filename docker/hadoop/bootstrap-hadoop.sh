#!/bin/bash

# Format the core-site.xml template using environment variables only available at runtime.
cat $HADOOP_HOME/core-site.xml.template | sed s/HADOOP_MASTER_HOSTNAME/$HADOOP_MASTER_HOSTNAME/ > $HADOOP_CONF_DIR/core-site.xml

service sshd start

$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh

# This will wait forever (and it needs to because docker exits when there are no more foreground processes)
tail -f /dev/null