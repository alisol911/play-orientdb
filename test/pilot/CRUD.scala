package pilot

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import scala.util.Random
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import org.apache.http.client.utils.URIUtils
import java.net.URLEncoder

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith( classOf[JUnitRunner] )
class CRUD extends Specification {

  //  val uri = Map( "orientdb.uri" -> "local:/tmp/testdb" )
  val uri = Map( "orientdb.uri" -> "memory:testdb" )
  //  val uri = Map( "orientdb.uri" -> "remote:localhost/testdb" )

  "create simple note" in {
    running( FakeApplication( additionalConfiguration = uri ) ) {
      val nameForInsert = "name" + Random.nextInt
      val nameForUpdate = "name" + Random.nextInt
      val familyForUpdate1 = "fmily" + Random.nextInt
      val familyForUpdate2 = "fmily" + Random.nextInt
      val jsonForInsert = Json.obj( "name" -> nameForInsert )
      val jsonForUpdate1 = Json.obj( "name" -> nameForUpdate, "family" -> familyForUpdate1 )
      val jsonForUpdate2 = Json.obj( "family" -> familyForUpdate2 )

      val createResult = route( FakeRequest( POST, "/service/entity/note",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForInsert ) ).get
      status( createResult ) must equalTo( OK )
      val id = URLEncoder.encode( ( Json.parse( contentAsString( createResult ) ) \ "oid" ).as[String] )

      val findForInsertResult = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForInsertResult ) must equalTo( OK )
      val jsonFindForInsertResult = Json.parse( contentAsString( findForInsertResult ) )
      contentType( findForInsertResult ).get must equalTo( "application/json" )
      ( jsonFindForInsertResult \ "name" ).as[String] must equalTo( nameForInsert )

      val findAllForInsertResult = route( FakeRequest( GET,
        "/service/entity/note?criteria=name%3D%22" + nameForInsert + "%22" ) ).get
      status( findAllForInsertResult ) must equalTo( OK )
      contentType( findForInsertResult ).get must equalTo( "application/json" )
      val jsonFindAllForInsertResult = Json.parse( contentAsString( findAllForInsertResult ) ).as[JsArray]
      ( jsonFindAllForInsertResult( 0 ) \ "name" ).as[String] must equalTo( nameForInsert )
     
      val updateResult1 = route( FakeRequest( PUT, "/service/entity/note/" + id,
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForUpdate1 ) ).get
      status( updateResult1 ) must equalTo( OK )

      val findForUpdateResult1 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForUpdateResult1 ) must equalTo( OK )
      val jsonFindForUpdateResult1 = Json.parse( contentAsString( findForUpdateResult1 ) )

      ( jsonFindForUpdateResult1 \ "name" ).as[String] must equalTo( nameForUpdate )
      ( jsonFindForUpdateResult1 \ "family" ).as[String] must equalTo( familyForUpdate1 )

      val updateResult2 = route( FakeRequest( PUT, "/service/entity/note/" + id,
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForUpdate2 ) ).get
      status( updateResult2 ) must equalTo( OK )

      val findForUpdateResult2 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForUpdateResult2 ) must equalTo( OK )
      val jsonFindForUpdateResult2 = Json.parse( contentAsString( findForUpdateResult2 ) )
      ( jsonFindForUpdateResult2 \ "name" ).as[ String ] must equalTo( nameForUpdate )
      ( jsonFindForUpdateResult2 \ "family" ).as[ String ] must equalTo( familyForUpdate2 )

      /*      
      val jsonForAddToSet1 = Json.obj( "value" -> "myValue" )
      val addToSetResult1 = route( FakeRequest( POST, "/service/entity/note/" + id + "/oneSet",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForAddToSet1 ) ).get
      status( addToSetResult1 ) must equalTo( NO_CONTENT )

      val findForAddToSetResult1 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForAddToSetResult1 ) must equalTo( OK )
      val jsonfindForAddToSetResult1 = Json.parse( contentAsString( findForAddToSetResult1 ) )
      val oneSetResult1 = ( jsonfindForAddToSetResult1 \ "oneSet" ).as[ JsArray ]
      oneSetResult1( 0 ).as[ String ] must equalTo( "myValue" )

      val jsonForAddToSet2 = Json.obj( "value" -> Json.obj( "val" -> 1 ) )
      val addToSetResult2 = route( FakeRequest( POST, "/service/entity/note/" + id + "/oneSet",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForAddToSet2 ) ).get
      status( addToSetResult2 ) must equalTo( NO_CONTENT )

      val findForAddToSetResult2 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForAddToSetResult2 ) must equalTo( OK )
      val jsonfindForAddToSetResult2 = Json.parse( contentAsString( findForAddToSetResult2 ) )
      val oneSetResult2 = ( jsonfindForAddToSetResult2 \ "oneSet" ).as[ JsArray ]
      ( oneSetResult2( 1 ).as[ JsObject ] \ "val" ).as[ Double ] must equalTo( 1.0 )

      val pullFromSetResult1 = route( FakeRequest( DELETE, "/service/entity/note/" + id +
        "/oneSet?value=%7B%22value%22%3A%22myValue%22%7D" ) ).get
      status( pullFromSetResult1 ) must equalTo( NO_CONTENT )
      val pullFromSetResult2 = route( FakeRequest( DELETE, "/service/entity/note/" + id +
        "/oneSet?value=%7B%22value%22%3A%7B%22val%22%3A1.0%7D%7D" ) ).get
      status( pullFromSetResult2 ) must equalTo( NO_CONTENT )

      val findForPushFromSetResult1 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForPushFromSetResult1 ) must equalTo( OK )
      val oneSetResult3 = ( Json.parse( contentAsString( findForPushFromSetResult1 ) ) \ "oneSet" ).as[ JsArray ]
      oneSetResult3.toString must equalTo( "[]" )

      val addToSetResult3 = route( FakeRequest( POST, "/service/entity/note/" + id + "/oneSet",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForAddToSet1 ) ).get
      status( addToSetResult3 ) must equalTo( NO_CONTENT )

      val editSetResult1 = route( FakeRequest( PUT, "/service/entity/note/" + id + "/oneSet",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ),
        Json.obj( "oldValue" -> "myValue", "value" -> "myValue2" ) ) ).get
      status( editSetResult1 ) must equalTo( NO_CONTENT )

      val findForEditSetResult1 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForEditSetResult1 ) must equalTo( OK )
      val oneSetResult4 = ( Json.parse( contentAsString( findForEditSetResult1 ) ) \ "oneSet" ).as[ JsArray ]
      oneSetResult4( 0 ).as[ String ] must equalTo( "myValue2" )

      val addToSetResult4 = route( FakeRequest( POST, "/service/entity/note/" + id + "/oneSet",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ), jsonForAddToSet2 ) ).get
      status( addToSetResult4 ) must equalTo( NO_CONTENT )

      val editSetResult2 = route( FakeRequest( PUT, "/service/entity/note/" + id + "/oneSet",
        FakeHeaders( Seq( "Content-type" -> Seq( "application/json" ) ) ),
        Json.obj( "oldValue" -> Json.obj( "val" -> 1 ), "value" -> Json.obj( "val" -> 2 ) ) ) ).get
      status( editSetResult2 ) must equalTo( NO_CONTENT )

      val findForEditSetResult2 = route( FakeRequest( GET, "/service/entity/note/" + id ) ).get
      status( findForEditSetResult2 ) must equalTo( OK )
      val oneSetResult5 = ( Json.parse( contentAsString( findForEditSetResult2 ) ) \ "oneSet" ).as[ JsArray ]
      ( oneSetResult5( 1 ).as[ JsObject ] \ "val" ).as[ Double ] must equalTo( 2.0 )
*/

      val deleteResult = route( FakeRequest( DELETE, "/service/entity/note/" + id ) ).get
      status( deleteResult ) must equalTo( NO_CONTENT )
    }
  }
}	