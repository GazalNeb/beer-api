package controllers

import models.{Beer}
import play.api.libs.json.Format.GenericFormat
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class BeerAPIController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  private val beerList = new mutable.ListBuffer[Beer]()
  beerList += Beer(1, "Corona")
  beerList += Beer(2, "Budweiser")

  implicit val beerListJson = Json.format[Beer]

  def getAll(): Action[AnyContent] = Action {
    if (beerList.isEmpty) {
      NoContent
    } else {
      Ok(Json.toJson(beerList))
    }
  }
  def getById(itemId: Long) = Action {
    val foundItem = beerList.find(_.id == itemId)
    foundItem match {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound
    }
  }
  def deleteBeerById(itemId: Long) = Action {
    beerList.filterInPlace(_.id != itemId)
    Accepted(Json.toJson(beerList)) //this will give back the filtered list in
  }

  def addNewItem() = Action { implicit request =>
    val content = request.body
    val jsonObject = content.asJson
    val beerItem: Option[Beer] =
      jsonObject.flatMap(
        Json.fromJson[Beer](_).asOpt
      )

    beerItem match {
      case Some(newItem) =>
        val nextId = beerList.map(_.id).max + 1
        val toBeAdded = Beer(nextId, newItem.name)
        beerList += toBeAdded
        Created(Json.toJson(toBeAdded))
      case None =>
        BadRequest
    }
  }


}
