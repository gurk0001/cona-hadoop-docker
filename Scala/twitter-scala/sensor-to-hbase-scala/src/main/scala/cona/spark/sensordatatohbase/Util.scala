package cona.spark.sensordatatohbase

import java.util.Properties

object Util {
  def loadProperties(propsLocation: String): Properties = {
    val props = new Properties()
    props.load(Util.getClass.getClassLoader.getResourceAsStream(propsLocation))
    props
  }

  def envVariable(varName: String): String = {
    val envVar = System.getenv(varName)
    if (envVar == null) {
      // Catch this error fast
      throw new NullPointerException(s"$varName is not a defined environment variable")
    }
    envVar
  }
}
