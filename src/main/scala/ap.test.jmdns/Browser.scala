package ap.test.jmdns

import javax.jmdns._
import java.util.{Dictionary, Hashtable => jHT}
import java.net.InetAddress

/**
 * launch this to monitor mDNS services on the network
 */
object Browser extends App with mDNS
{
  val myType = ""
  start
}
