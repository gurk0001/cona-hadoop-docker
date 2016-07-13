#!/bin/bash

echo 'Building hbase-site.xml'
# Need to use '|' as the sed delimiter because $HBASE_HOME and $HBASE_ZOOKEEPER_DATA_DIR have '/' in them.
cat /tmp/hbase-site.xml.template | sed 's|HBASE_ROOT_DIR|'$HBASE_HOME'|' | sed 's|ZOOKEEPER_QUORUM|'$ZOOKEEPER_QUORUM'|' | sed 's|HBASE_ROOT_DIR|'$HBASE_ROOT_DIR'|' > $HBASE_HOME/conf/hbase-site.xml

echo 'Starting HBase'
$HBASE_HOME/bin/start-hbase.sh
echo 'HBase started'
# This will wait forever (and it needs to because docker exits when there are no more foreground processes)
tail -f /dev/null