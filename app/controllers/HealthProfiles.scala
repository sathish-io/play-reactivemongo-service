package controllers

import javax.inject.Inject

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, BodyParsers, Call, Controller, Result}

import reactivemongo.bson.{BSONObjectID, BSONDocument}
import reactivemongo.core.actors.Exceptions.PrimaryUnavailableException
import reactivemongo.api.commands.WriteResult

import play.modules.reactivemongo.{
MongoController, ReactiveMongoApi, ReactiveMongoComponents
}

class HealthProfiles @Inject()(val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  import controllers.HealthProfiles._

  def profilesRepo = new backend.HealthProfilesMongoRepo(reactiveMongoApi)

  def list = Action.async { implicit request =>
    profilesRepo.find()
      .map(profiles => Ok(Json.toJson(profiles.reverse)))
      .recover { case PrimaryUnavailableException => InternalServerError("Please install MongoDB") }
  }

  def findOne(name: String) = Action.async { implicit request =>
    profilesRepo.findOne(BSONDocument(Name -> name))
      .map(profile => Ok(Json.toJson(profile)))
  }

}

object HealthProfiles {
  val Id = "_id"
  val Name = "name"
}
