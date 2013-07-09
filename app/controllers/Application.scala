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
import com.orientechnologies.orient.core.record.impl.ODocument
import com.orientechnologies.orient.core.metadata.schema.OType._
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.core.db.record.OTrackedList
object Application extends Controller {

  lazy val uri = current.configuration.getString( "orientdb.uri" ).get

  def index = Action {
    Ok( views.html.index( "" ) )
  }

  lazy val db = {
    val db_ = new ODatabaseDocumentTx( uri )
    def dbIsExists: Boolean = {
      try {
        db_.exists
      } catch {
        case e: UnsupportedOperationException ⇒ true
      }
    }
    if ( !dbIsExists )
      db_.create
    else
      db_.open( "admin", "admin" )
    db_
  }

  private def jsonToDocument( className: String, json: JsValue ): ODocument = {
    val result = db.newInstance[ODocument]( className )
    result.fromJSON( json.toString )
  }

  private def findById( id: String ): ODocument = {
    val result = db.query[java.util.List[ODocument]]( new OSQLSynchQuery[ODocument]( s"select from $id" ) )
    result.get( 0 )
  }

  def create( entity: String ) = Action( parse.json ) { implicit request ⇒
    db.begin( TXTYPE.NOTX )
    val doc = jsonToDocument( entity, request.body )
    doc.save
    val id = doc.getIdentity.toString()
    doc.reset
    db.commit
    val json = Json.obj( ( "oid", id ) ) ++ request.body.as[JsObject]
    Ok( json )
  }

  def find( entity: String, id: String ) = Action { implicit request ⇒
    Ok( Json.parse( findById( id ).toJSON() ) )
  }

  def findAll( entity: String, criteria: String ) = Action { implicit request ⇒
    val list = db.query[java.util.List[ODocument]]( new OSQLSynchQuery[ODocument]( s"select from $entity" + ( if ( criteria != "" ) s" where $criteria" ) ) )
    val result = list.map( ( d: ODocument ) ⇒ Json.parse( d.toJSON() ) ).toList
    Ok( JsArray( result ) )
  }

  def edit( entity: String, id: String ) = Action( parse.json ) { implicit request ⇒
    val doc = findById( id )
    db.begin( TXTYPE.NOTX )
    val newDoc = doc.merge( jsonToDocument( entity, request.body ), true, true )
    newDoc.save
    db.commit()
    Ok( "" )
  }

  def delete( entity: String, id: String ) = Action {
    findById( id ).delete();
    NoContent
  }

  def addToSet( entity: String, id: String, set: String ) = Action( parse.json ) { implicit request ⇒
    val doc = findById( id )
    db.begin( TXTYPE.NOTX )
    val newDoc = doc.merge( jsonToDocument( entity, Json.obj( set -> JsArray( Seq( request.body \ "value" ) ) ) ), true, true )
    newDoc.save
    db.commit()
    NoContent
  }

  def pullFromSet( entity: String, id: String, set: String, value: String ) = Action { implicit request ⇒
    val doc = findById( id )
    val list: OTrackedList[_] = doc.field( set )
    val itemToDelete = ( new ODocument ).fromJSON[ODocument]( ( Json.parse( value ) ).toString )
    db.begin( TXTYPE.NOTX )
    list.remove( list.indexOf( itemToDelete.field( "value" ) ) )
    doc.field( set, list )
    doc.save
    db.commit()
    NoContent
  }

  def editSet( entity: String, id: String, set: String ) = Action( parse.json ) { implicit request ⇒
    val doc = findById( id )
    val list: OTrackedList[_] = doc.field( set )
    val itemToEdit = ( new ODocument ).fromJSON[ODocument]( request.body.toString )
    db.begin( TXTYPE.NOTX )
    list.set( list.indexOf( itemToEdit.field( "oldValue" ) ), itemToEdit.field( "value" ) )
    doc.field( set, list )
    doc.save
    db.commit()
    NoContent
  }
}