package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.http.Writeable
import play.api.Play.current
import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import com.orientechnologies.orient.core.db.`object`.ODatabaseObject
import com.orientechnologies.orient.`object`.db.ODatabaseObjectPool
import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.tx.OTransaction.TXTYPE
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.metadata.schema.OType._
object Application extends Controller {

  lazy val uri = current.configuration.getString( "orientdb.uri" ).get

  def index = Action {
    Ok( views.html.index( "" ) )
  }

  lazy val db = {
    val db_ = new OObjectDatabaseTx( uri )
    db_.open( "admin", "admin" )
    db_
  }

  def jsonToDocument( doc: ODocument, className: String, json: JsValue ): ODocument = {
    val result = ( if ( doc == null ) new ODocument else doc )
    for ( f ← json.asInstanceOf[ JsObject ].fields ) {
      result.field( f._1, f._2 )
    }
    result.setClassName( className )
    result
  }

  def documentToJson( doc: ODocument ): JsArray = {
    val result = doc.fieldNames().map( field ⇒ {
      doc.fieldType( field ) match {
        case STRING ⇒ Json.obj( ( field, doc.field[ String ]( field ) ) )
      }
    } )
    JsArray( result.toList )
  }

  def create( entity: String ) = Action( parse.json ) { implicit request ⇒
    db.begin( TXTYPE.NOTX )
    val doc = jsonToDocument( null, entity, request.body )
    doc.save
    val id = doc.getIdentity.toString()
    doc.reset
    db.commit
    val json = Json.obj( ( "oid", id ) ) ++ request.body.as[ JsObject ]
    Ok( json )
  }

  def find( entity: String, id: String ) = Action { implicit request ⇒
    val doc = db.query[ java.util.List[ ODocument ] ]( new OSQLSynchQuery[ ODocument ]( s"select from $id" ) )
    Ok( documentToJson( doc.get( 0 ) ) )
  }

  def findAll( entity: String, criteria: String ) = Action { implicit request ⇒
    InternalServerError
  }

  def edit( entity: String, id: String ) = Action( parse.json ) { implicit request ⇒
    val doc = db.query( new OSQLSynchQuery[ ODocument ]( s"select from $id" ) )
    db.begin( TXTYPE.NOTX )
    jsonToDocument( doc, entity, request.body ).save
    db.commit()
    Ok( "" )
  }

  def delete( entity: String, id: String ) = Action {
    InternalServerError
  }

  def addToSet( entity: String, id: String, set: String ) = Action( parse.json ) { implicit request ⇒
    InternalServerError
  }

  def pullFromSet( entity: String, id: String, set: String, value: String ) = Action { implicit request ⇒
    InternalServerError
  }

  def editSet( entity: String, id: String, set: String ) = Action( parse.json ) { implicit request ⇒
    InternalServerError
  }
}