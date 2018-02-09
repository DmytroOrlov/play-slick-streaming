package play.api.db.slick.evolutions

import java.io.File

import com.typesafe.config.ConfigFactory.load
import play.api.Configuration
import play.api.Environment.simple
import play.api.Mode.Prod
import play.api.db.evolutions.OfflineEvolutions.applyScript
import play.api.db.slick.DefaultSlickApi
import play.api.db.slick.evolutions.internal.DBApiAdapter
import play.api.inject.DefaultApplicationLifecycle

import scala.concurrent.ExecutionContext.Implicits.global

object ApplyEvolutionsHelper {
  def apply(): Unit = {
    val slickApi = new DefaultSlickApi(simple(mode = Prod), Configuration(load("evolutions.conf")), new DefaultApplicationLifecycle)
    applyScript(new File("server"), this.getClass.getClassLoader, new DBApiAdapter(slickApi), "default")
  }
}
