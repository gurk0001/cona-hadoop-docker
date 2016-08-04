#!/bin/bash
# /usr/lib/rstudio-server/bin/rserver --server-daemonize 0
$SPARK_HOME/sbin/start-master.sh
tail -f /dev/null
