# The alpine-java image is a ultra-lightweight linux image set up to run java programs
FROM anapsix/alpine-java

# Spark needs procps and ncurses, and tar needs to be updated to use --strip-components a
RUN apk --update add wget tar procps ncurses && \
	mkdir /opt/spark && \
	# Stream from wget and untar into /opt/spark. Strip out the top directory so that it unpacks itself nicely.
	wget -qO- http://d3kbcqa49mib13.cloudfront.net/spark-1.6.2-bin-hadoop2.6.tgz | tar xvz --strip-components=1 -C /opt/spark && \
	# The spark-jars directory will contain jars that spark should run.
	mkdir /spark-jars

COPY runspark.sh /opt/spark

RUN ln -s /opt/spark/runspark.sh /usr/local/bin/runspark && \
	chmod +x /opt/spark/runspark.sh

ENV SPARK_HOME=/opt/spark

CMD ["runspark"]