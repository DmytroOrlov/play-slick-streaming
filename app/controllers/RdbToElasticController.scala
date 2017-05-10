package controllers

import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
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

@Singleton
class RdbToElasticController @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents, c: Configuration)
(implicit ec: ExecutionContext, mat: Materializer) extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.profile.api._

  def stream: Action[AnyContent] = Action {
    val start = System.currentTimeMillis()
    val s: Source[String, NotUsed] = Source.fromPublisher(
      db.stream(sql"select id from stream_data".as[Int].transactionally.withStatementParameters(fetchSize = 10000))
    )
      .map(x =>
        s"""{index:{_index:"postgres",_type:"stream_data"}}
           |{id:"$x"}
           |""".stripMargin)
      .grouped(10000)
      .mapAsyncUnordered(1)(sendBulkUpdateRequest)
      .map(_ => s"${System.currentTimeMillis() - start}\n")
    Ok.chunked(s)
  }

  private val client = RestClient.builder(new HttpHost(c.underlying.getString("elasticsearch.host"), 9200)).build()
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
          else p.failure(new RuntimeException(response.getStatusLine.getStatusCode.toString))
      },
      new BasicHeader("Content-Type", "application/x-ndjson")
    )
    p.future
  }
}
