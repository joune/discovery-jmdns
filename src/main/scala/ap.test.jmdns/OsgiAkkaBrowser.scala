package ap.test.jmdns

import javax.jmdns._

import aQute.bnd.annotation.component._
import org.osgi.service.component.ComponentFactory
import org.osgi.framework.BundleContext

import akka.actor.{Actor, ActorSystem, Props}
import akka.osgi.ActorSystemActivator

@Component(factory="com.kyriba.jmdns", provide=Array()) 
class OsgiAkkaBrowser extends Actor with mDNS
{
  val myType = ""

  case class ServiceResolved(info:ServiceInfo)
  case class ServiceRemoved(info:ServiceInfo)

  @Activate def activate() = start()

  override def discovered(info:ServiceInfo) = self ! ServiceResolved(info)

  override def removed(info:ServiceInfo) = self ! ServiceResolved(info)

  def receive = {
    case m => println(m.toString)
  }
}

// Activator for OSGi dependency injection in akka actor
@Component(immediate=true) 
class Activator 
{
  private var system: ActorSystem = _
  private var factory: ComponentFactory = _

  @Reference def bindSystem(sys: ActorSystem): Unit = system = sys

  @Reference(target="(component.factory=com.kyriba.jmdns)") def bindCF(cf: ComponentFactory): Unit = factory = cf
  
  def create: Actor = factory.newInstance(null).getInstance.asInstanceOf[Actor]
  
  @Activate def start(bc: BundleContext): Unit = system.actorOf(Props(create), "mDNS")
}

class SharedAkka extends ActorSystemActivator
{
  def configure(bundleContext: BundleContext, system: ActorSystem): Unit = 
    registerService(bundleContext, system)
}


