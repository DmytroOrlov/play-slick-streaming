package controllers

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import javax.inject.{Inject, Singleton}
import org.apache.http.HttpHost
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHeader
import org.elasticsearch.client.{Response, ResponseListener, RestClient}
import play.api.Configuration
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, Promise}

@slick.basic.StaticDatabaseConfig("file:conf/application.conf#slick.dbs.default")
@Singleton
class RdbToElasticController @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents, conf: Configuration)
(implicit ec: ExecutionContext, mat: Materializer) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  def stream: Action[AnyContent] = Action {
    val start = System.currentTimeMillis()
    val bulkSize = 10000
    val source = select(bulkSize).map(x =>
      s"""{"index":{"_index":"postgres","_type":"stream_data"}}
         |{"id":"$x"}
         |""".stripMargin)
      .grouped(bulkSize)
      .mapAsyncUnordered(1)(sendBulkUpdateRequest)
      .map(_ => s"${System.currentTimeMillis() - start}\n")
    Ok.chunked(source)
  }

  private def select(bulkSize: Int): Source[Int, NotUsed] =
    Source.fromPublisher(
      db.stream(tsql"select id from stream_data".transactionally.withStatementParameters(fetchSize = bulkSize))
    )

  private val client = RestClient.builder(new HttpHost(conf.underlying.getString("elasticsearch.host"), 9200)).build()
  private val params = Map[String, String]().asJava

  private def sendBulkUpdateRequest(messages: Seq[String]): Future[Done] = {
    val p = Promise[Done]()
    client.performRequestAsync(
      "POST",
      "/_bulk",
      params,
      new StringEntity(messages.mkString),
      new ResponseListener {
        def onFailure(exception: Exception): Unit = p.failure(exception)

        def onSuccess(response: Response): Unit =
          if (response.getStatusLine.getStatusCode == OK)
            p.success(Done)
          else p.failure(new Exception(response.getStatusLine.getStatusCode.toString))
      },
      new BasicHeader("Content-Type", "application/x-ndjson")
    )
    p.future
  }
}
