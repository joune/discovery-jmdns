package ap.test.jmdns

import javax.jmdns._
import java.util.{Hashtable => jHT}
import java.net.InetAddress

/**
 * basic test.
 * launch a bunch of these and let them discover each other
 */
object Main extends App with mDNS
{
  val myType = "_123._tcp.aptest"
  val myService = "myService"+System.currentTimeMillis

  start

  register(myService,
           (math.random * 10000).toInt, //fake random port number
           new jHT[String,String] {
             put("sample-key", "sample-value")
           })

  // unregister on shutdown
  Runtime.getRuntime.addShutdownHook(new Thread {
   override def run = stop
  })

}

trait mDNS
{
  val myType: String
  private var jmdns: JmDNS = _

  def start() 
  {
    val addr = InetAddress.getLocalHost
    jmdns = JmDNS.create(addr)
    println(s"jmdns started on $addr")

    // add listeners for jmDNS events
    jmdns.addServiceTypeListener(new ServiceTypeListener 
    {
      override def serviceTypeAdded(ev:ServiceEvent) = Option(ev.getType) match {
        case Some(typ) => 
          if (typ.contains(myType)) 
          {
            jmdns.addServiceListener(typ, new ServiceListener 
            {
              override def serviceAdded(ev:ServiceEvent)
              {
                println(s"request reslution for serviceAdded ${ev.getInfo}")
                jmdns.requestServiceInfo(typ, ev.getName) //force resolution
              }

              override def serviceResolved(ev:ServiceEvent) = discovered(ev.getInfo)

              override def serviceRemoved(ev:ServiceEvent) = removed(ev.getInfo)
              println(s"serviceRemoved ${ev}")
            })
          } 
          else println("ignore foreign type "+typ)
        case _ => //ignore
      }
      override def subTypeForServiceTypeAdded(ev:ServiceEvent) = Unit //not used
    })
    println("type listener added")
  }

  def discovered(info: ServiceInfo) = println(s"service discovered $info")

  def removed(info: ServiceInfo) = println(s"service removed $info")

  def register(service: String, port: Int, props: jHT[String,String])
  {
    // register ourself
    val myInfo = ServiceInfo.create(myType, 
                  service,
                  port,
                  10, //weight 
                  1,  //priority 
                  props)
    jmdns.registerService(myInfo) //FIXME, blocking Felix main thread for 6 seconds!
    println("registered "+service)
  }

  def stop() = jmdns.close
}

