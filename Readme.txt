The Arduino code for the Pi Producer assumes connection to the interrupt pin of an arduino (pin 3) of an Arduino Uno.

It also requires the NewPing library, which has been included as a zip file.

It interfaces with the raspberry pi through a serial connection. Modify the resources file in order to select the right
USB port. The RaspberryPi is configured to connect to /dev/ttyACM0, which is the USB slot at the top left corner of the RaspberryPi (with the ethernet port facing you).

Using IntelliJ, you should be able to download and install the maven dependencies. The pi project also integrates with maven through "install" in which it automatically deploys the code to the RaspberryPi if its IP is configured properly.

The current code base is reliant on Docker-Compose the launch and create the Hadoop cluster. If you are running windows, Docker runs through a VirtualBox instance, which must be configured to allow for outside access. Usually this means portforwarding the KAFKA_ADVERTISED_HOSTNAME to 9092 through virtualbox.


Running:

Make sure you have docker installed. Then, clone the git repository using git.

Then, change to the docker directory, and run docker-compose build. This will build the images for HBase, Spark, Node, Kafka, and Hadoop.

To run, run docker-compose up -d.

To access each of the containers, run docker exec -it container_name /bin/bash. For example, to access spark, I would run docker exec -t docker_spark_1 /bin/bash.

