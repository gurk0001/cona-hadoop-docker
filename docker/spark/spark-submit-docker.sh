#/bin/bash

docker cp $1 kafkaproject_spark_1:/spark-jars/$1

docker exec kafkaproject_spark_1 /opt/spark/bin/spark-submit --master local[*] --deploy-mode client /spark-jars/$1
