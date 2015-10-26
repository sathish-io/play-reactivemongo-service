package backend

import scala.concurrent.{ ExecutionContext, Future }

import play.api.Play.current
import play.api.libs.json.{ JsObject, Json }

import reactivemongo.bson.BSONDocument
import reactivemongo.api.commands.WriteResult

import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json.collection.JSONCollection

trait HealthProfilesRepo {
  def find()(implicit ec: ExecutionContext): Future[List[JsObject]]

  def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult]

  def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult]

  def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult]
}

class HealthProfilesMongoRepo(reactiveMongoApi: ReactiveMongoApi) extends HealthProfilesRepo {
  // BSON-JSON conversions
  import play.modules.reactivemongo.json._, ImplicitBSONHandlers._

  protected def collection =
    reactiveMongoApi.db.collection[JSONCollection]("health_profiles")

  def find()(implicit ec: ExecutionContext): Future[List[JsObject]] =
    collection.find(Json.obj()).cursor[JsObject].collect[List]()
    
  def findOne(selector: BSONDocument)(implicit ec: ExecutionContext): Future[Option[JsObject]]  = {
    println("query: " + BSONDocument.pretty(selector))
    collection.find(selector).one[JsObject]
  }

  def update(selector: BSONDocument, update: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = collection.update(selector, update)

  def remove(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = collection.remove(document)

  def save(document: BSONDocument)(implicit ec: ExecutionContext): Future[WriteResult] = collection.save(document)
}
