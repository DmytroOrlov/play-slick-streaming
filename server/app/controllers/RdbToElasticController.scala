package controllers

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

@slick.basic.StaticDatabaseConfig("file:server/conf/application.conf#slick.dbs.default")
@Singleton
class RdbToElasticController @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)
  (implicit ec: ExecutionContext, mat: Materializer) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  def stream: Action[AnyContent] = Action {
    val bulkSize = 10000
    val source = select(bulkSize).map(StreamData.apply).map { personCity =>
      Json.toJson(personCity)
    }
    Ok.chunked(source)
  }

  private def select(bulkSize: Int)/*: Source[Int, NotUsed]*/ =
    Source.fromPublisher(
      db.stream(tsql"SELECT id FROM stream_data ORDER BY id" /*.as[Int]*/ .transactionally.withStatementParameters(fetchSize = bulkSize))
    )
}

case class StreamData(id: Int)

object StreamData {
  implicit val `writes[StreamData]`: Writes[StreamData] = writes[StreamData]
}
