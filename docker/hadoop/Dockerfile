FROM anapsix/alpine-java

RUN apk --update add wget tar procps ncurses openssh && \0
	mkdir /opt/hadoop  && \
	wget -qO- http://apache.claz.org/hadoop/common/hadoop-2.6.4/hadoop-2.6.4.tar.gz | tar -xvz --strip-components=1 -C /opt/hadoop && \
	# Setup passwordless SSH between hadoop instances
	ssh-keygen -q -N "" -t dsa -f /etc/ssh/ssh_host_dsa_key && \
	ssh-keygen -q -N "" -t rsa -f /etc/ssh/ssh_host_rsa_key && \
	ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa && \
	cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

ENV HADOOP_HOME=/opt/hadoop
ENV HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop

ENV HADOOP_TMP_DIR=$HADOOP_HOME/tmp

COPY bootstrap-hadoop.sh /opt/hadoop
