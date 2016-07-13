import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Admin
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.client.Connection
import org.apache.hadoop.hbase.client.Table

object hbase {

    private val config = HBaseConfiguration.create()

    init {
        config.set("hbase.zookeeper.quorum", environment.HBASE_ZOOKEEPER_QUORUM)
    }

    fun withConnection(func: (Connection) -> Unit) = ConnectionFactory.createConnection(config).use(func)

    fun withAdmin(func: (Admin) -> Unit) = withConnection { it.admin.use(func) }

    fun withTable(tableName: TableName, func: (Table) -> Unit) = withConnection { it.getTable(tableName).use(func) }

}