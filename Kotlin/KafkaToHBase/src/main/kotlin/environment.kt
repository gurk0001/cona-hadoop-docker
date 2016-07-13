
object environment {
    val HBASE_ZOOKEEPER_QUORUM: String = System.getenv("ZOOKEEPER_QUORUM")
    val KAFKA_ZOOKEEPER_QUORUM: String = System.getenv("ZOOKEEPER_QUORUM")

    val HBASE_ZOOKEEPER_QUORUM_ADDRESS: String = HBASE_ZOOKEEPER_QUORUM.split(':')[0]
    val HBASE_ZOOKEEPER_QUORUM_PORT: String = HBASE_ZOOKEEPER_QUORUM.split(':')[1]
}