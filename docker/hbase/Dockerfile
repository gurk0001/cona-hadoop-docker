FROM anapsix/alpine-java

RUN apk --update add wget tar procps ncurses && \
	mkdir /opt/hbase  && \
	wget -qO- http://apache.mirrors.ionfish.org/hbase/stable/hbase-1.2.2-bin.tar.gz | tar -xvz --strip-components=1 -C /opt/hbase

ENV HBASE_HOME=/opt/hbase \
	HBASE_ZOOKEEPER_DATA_DIR=/home/zookeeper-data \
	HBASE_ROOT_DIR=/home/hbase

ENV PATH $PATH:$HBASE_HOME/bin

COPY hbase-site.xml.template /tmp/hbase-site.xml.template

COPY bootstrap-hbase.sh $HBASE_HOME/bootstrap-hbase.sh

COPY hbase-env.sh $HBASE_HOME/conf/hbase-env.sh


RUN chmod +x $HBASE_HOME/bootstrap-hbase.sh && \
	ln -s $HBASE_HOME/bootstrap-hbase.sh /usr/local/bin/bootstrap-hbase

ENTRYPOINT ["/bin/bash", "bootstrap-hbase"]
