package input

import scala.io.BufferedSource
import vectors.{VenueVector, AbstractVector}
import features.IntFeature
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.module.scala.DefaultScalaModule
//import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._ 
import play.api.data.validation.ValidationError
import scala.util.parsing.json.JSON
import scala.collection.mutable.ListBuffer


case class VenueStats(checkinsCount: Option[Int], usersCount: Option[Int], tipCount: Option[Int])

case class VenueLocation(address: Option[String], crossStreet: Option[String],  city: Option[String], 
						 state: Option[String], postalCode: Option[String], lat: Option[Double], lng: Option[Double])

case class VenueCategory(id: Option[String], name: Option[String], pluralName: Option[String], shortName: Option[String],
                                  icon : Option[VenueIcon], primary: Option[Boolean])

case class VenueCategories(categories : List[VenueCategory])

case class VenueMayor(count: Option[Int], id: Option[String], firstName: Option[String], lastName: Option[String],
                      gender: Option[String], homeCity: Option[String], relationship: Option[String])

case class VenueTips(count: Int, groups: List[VenueTipGroup])

case class VenueTipGroup(`type`: String, name: String, count: Int, items: List[TipForList])

case class TipForList(id: String, createdAt: Long, text: Option[String], url: Option[String], status: Option[String])

case class VenuePhotoGroup(`type`: String, name: String, count: Int, items: List[PhotoForList])

case class VenuePhotos(count: Int, groups: List[VenuePhotoGroup])

case class PhotoForList(id: String, createdAt: Long, url: String, visibility: String)

case class CompactEvent(id: String, name: Option[String])

case class VenueListed(count: Int, items: Option[List[String]])

case class VenueIcon(prefix : Option[String], suffix: Option[String])


/**
 * @author Matteo Pagliardini
 */
class Venue(filePath : String) {

	val jsonString = scala.io.Source.fromFile(filePath).mkString
  	val jsonFile: JsValue = Json.parse(jsonString)

  	/*val mapper = new ObjectMapper() with ScalaObjectMapper
	mapper.registerModule(DefaultScalaModule)
	val obj = mapper.readValue[Map[String, Map[String, Object]]](jsonString)
	
	val map = mapper.readValue[Map[String, Any]](obj.get("venue").get("stats"))
	println(map)*/

	/*implicit val iconFormat: Format[VenueIcon] = (
			(__ \ "prefix").format[Option[String]] and
			(__ \ "suffix").format[Option[String]]
		)(VenueIcon.apply, unlift(VenueIcon.unapply))*/

	implicit val iconRead: Reads[VenueIcon] = (
			(JsPath \ "venue" \ "categories" \ "icon" \ "prefix").read[Option[String]] and
			(JsPath \ "venue" \ "categories" \ "icon" \ "suffix").read[Option[String]]
		)(VenueIcon.apply _)

	/*implicit val categoryFormat: Format[VenueCategory] = (
    		(__ \ "id").format[Option[String]] and 
  			(__ \ "name").format[Option[String]] and
  			(__ \ "pluralName").format[Option[String]] and
  			(__ \ "shortName").format[Option[String]] and
  			(__ \ "icon").format[Option[VenueIcon]] and
  			(__ \ "primary").format[Option[Boolean]] 
		)(VenueCategory.apply, unlift(VenueCategory.unapply))*/

	implicit val categoryRead: Reads[VenueCategory] = (
  			(JsPath \ "venue" \ "categories" \ "id").read[Option[String]] and
  			(JsPath \ "venue" \ "categories" \ "name").read[Option[String]] and
  			(JsPath \ "venue" \ "categories" \ "pluralName").read[Option[String]] and
  			(JsPath \ "venue" \ "categories" \ "shortName").read[Option[String]] and 
  			(JsPath \ "venue" \ "categories" \ "icon").read[Option[VenueIcon]] and
  			(JsPath \ "venue" \ "categories" \ "primary").read[Option[Boolean]]
		)(VenueCategory.apply _)

	implicit val categoriesRead: Reads[VenueCategories] = (JsPath \ "venue" \ "categories").read(list[VenueCategory](categoryRead)).map(VenueCategories.apply _)

	val id: String = filePath 								
    val name = (jsonFile \ "venue" \ "name" ).as[Option[String]]
    val likes = (jsonFile \ "venue" \ "likes" \ "count").as[Option[Int]]
    val rating = (jsonFile \ "venue" \ "rating").as[Option[Int]]
    val twitter	 = (jsonFile \ "venue" \ "contact" \ "twitter").as[Option[String]]
    val facebook = (jsonFile \ "venue" \ "contact" \ "facebook").as[Option[String]]
    val location = VenueLocation( (jsonFile \ "venue" \ "location" \ "address").as[Option[String]], 
    							(  jsonFile \ "venue" \ "location" \ "crossStreet").as[Option[String]],  
    						    (  jsonFile \ "venue" \ "location" \ "city").as[Option[String]], 
			    				(  jsonFile \ "venue" \ "location" \ "state").as[Option[String]], 
						 		(  jsonFile \ "venue" \ "location" \ "postalCode").as[Option[String]], 
                         	    (  jsonFile \ "venue" \ "location" \ "lat").as[Option[Double]], 
                         		(  jsonFile \ "venue" \ "location" \ "lng").as[Option[Double]])
    val foursquareURL = (jsonFile \ "venue" \ "canonicalUrl" ).as[Option[String]]
    //val categories: List[VenueCategoryCompact] 			= ???
    //var categories: JsResult[VenueCategories] = jsonFile.validate[VenueCategories](categoriesRead)


  	private val json:Option[Any] = JSON.parseFull(jsonString)
  	private val map:Map[String,Any] = json.get.asInstanceOf[Map[String, Any]]
  	private val venue:Map[String,Any] = map.get("venue").get.asInstanceOf[Map[String,Any]]
  	private val categoriesList:List[Any] = venue.get("categories").get.asInstanceOf[List[Any]]
	var categories  = new ListBuffer[(String,String,Boolean)]//(String, String, Boolean)]
	categoriesList.foreach( c => {
		val cat: Map[String,Any] = c.asInstanceOf[Map[String,Any]]
		var id : String =
	    if(cat.contains("id")) {
			cat.get("id").get.asInstanceOf[String]
		} else {
			""
		}
		val name: String = cat.get("name").get.asInstanceOf[String]
		val primary : Boolean = 
		if(cat.contains("primary")) {
			cat.get("primary").get.asInstanceOf[Boolean]
		} else {
			false
		}

		val t : (String,String,Boolean) = (id, name, primary)
		categories += t
	})
    //var categories = (Json.parse(jsonString) \ "venue" \ "categories").as[List[VenueCategory]]
    val verified = (jsonFile \ "venue" \ "verified").as[Option[Boolean]]
    val stats = VenueStats((jsonFile \ "venue" \ "stats" \ "checkinsCount").as[Option[Int]], 
    	   				   (jsonFile \ "venue" \ "stats" \ "usersCount").as[Option[Int]],
    	   				   (jsonFile \ "venue" \ "stats" \ "tipCount").as[Option[Int]])
    val url = (jsonFile \ "venue" \ "url").as[Option[String]]		
    val mayor = VenueMayor ((jsonFile \ "venue" \ "mayor" \ "count").as[Option[Int]],
    			 		   (jsonFile \ "venue" \ "mayor" \ "user" \ "id").as[Option[String]],
    			 		   (jsonFile \ "venue" \ "mayor" \ "user" \ "firstName").as[Option[String]],
    			 		   (jsonFile \ "venue" \ "mayor" \ "user" \ "lastName").as[Option[String]],
    			 		   (jsonFile \ "venue" \ "mayor" \ "user" \ "gender").as[Option[String]],
    			   		   (jsonFile \ "venue" \ "mayor" \ "user" \ "homeCity").as[Option[String]],
    			 		   (jsonFile \ "venue" \ "mayor" \ "user" \ "relationship").as[Option[String]])
    //val tips: VenueTips 								= ???
    val tags = (jsonFile \ "venue" \ "tags" ).as[Option[List[String]]] 

    val timeZone = (jsonFile \ "venue" \ "timeZone" ).as[Option[String]]
    //val photos: Option[VenuePhotos]	 					= ???
    val description = (jsonFile \ "venue" \ "description" ).as[Option[String]]
    //val events: Option[List[CompactEvent]]	 			= ???
    //val listed: Option[VenueListed] 					= ???

 
 /**
  * Getters
  */
  def address(): Option[String] = {
  	location.address
  }
  def crossStreet(): Option[String] = {
  	location.crossStreet
  }
  def city(): Option[String] = {
  	location.city
  }
  def state(): Option[String] = {
  	location.state
  }
  def postalCode(): Option[String] = {
  	location.postalCode
  }
  def latitude(): Option[Double] = {
  	location.lat
  }
  def longitude(): Option[Double] = {
  	location.lng
  }
  def checkinsCount(): Option[Int] = {
  	stats.checkinsCount
  }
  def usersCount(): Option[Int] = {
  	stats.usersCount
  }
  def tipCount(): Option[Int] = {
  	stats.tipCount
  }
  def mayorCount() : Option[Int] = {
  	mayor.count
  }
  def mayorId():Option[String] = {
  	mayor.id
  }
  def mayorFirstName():Option[String] = {
  	mayor.firstName
  }
  def mayorLastName():Option[String] = {
  	mayor.lastName
  }
  def mayorGender():Option[String] = {
  	mayor.gender
  }
  def mayorHomeCity():Option[String] = {
  	mayor.homeCity
  }
  def mayorRelationship():Option[String] = {
  	mayor.relationship
  }

 /**
  * Display the features in a nice form
  */
  def displayFeatures() {
  	println("\n****************************************************")
  	println(  "VENUE : "+id)
  	println(  "****************************************************\n")
	println(  "name   := "+name)
	println(  "likes  := "+likes)
	println(  "rating := "+rating)
	println(  "twitter := "+twitter)
	println(  "facebook := "+facebook)
	println(  "address := "+location.address)
	println(  "crossStreet := "+location.crossStreet)
	println(  "city := "+location.city)
	println(  "state := "+location.state)
	println(  "postalCode := "+location.postalCode)
	println(  "latitude := "+location.lat)
	println(  "longitude := "+location.lng)
  	println(  "foursquareURL := "+foursquareURL)
	println(  "verified := "+verified)
	println(  "checkinsCount := "+stats.checkinsCount)
	println(  "usersCount := "+stats.usersCount)
	println(  "tipCount := "+stats.tipCount)
	println(  "url := "+url)
	println(  "mayorCount := "+mayor.count)
	println(  "mayorId := "+mayor.id)
	println(  "mayorFirstName := "+mayor.firstName)
	println(  "mayorLastName := "+mayor.lastName)
	println(  "mayorGender := "+mayor.gender)
	println(  "mayorHomeCity := "+mayor.homeCity)
	println(  "mayorRelationship := "+mayor.relationship)
	println(  "tags := "+tags)
	println(  "timeZone := "+timeZone)
	println(  "description := "+description)
	println("\n****************************************************")
  	println(  "****************************************************\n")
  }
}

