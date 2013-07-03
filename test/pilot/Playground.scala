package pilot

import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import play.api.libs.json._

import com.orientechnologies.orient.core.id.ORecordId
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import com.orientechnologies.orient.core.db.`object`.ODatabaseObject
import com.orientechnologies.orient.`object`.db.ODatabaseObjectPool
import com.orientechnologies.orient.`object`.db.OObjectDatabaseTx
import com.orientechnologies.orient.core.tx.OTransaction.TXTYPE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

@RunWith( classOf[JUnitRunner] )
class Playground extends Specification {
  "create" in {

    val db = new ODatabaseDocumentTx( "memory:test" )
    db.create

    val doc1 = db.newInstance
    doc1.setClassName( "MyDocument" )
    doc1.field( "f1", 11 )
    db.save( doc1 )

    val id = doc1.getIdentity.toString()

    val result1 = db.query[java.util.List[ODocument]]( new OSQLSynchQuery[ODocument]( "select from " + id ) )
    val doc2 = result1.get( 0 )

    val doc3 = db.newInstance
    doc3.field( "f1", 12 )
    doc3.field( "f2", 21 )
    val doc4 = doc2.merge( doc3, true, true )
    doc4.save
   
    val result2 = db.query[java.util.List[ODocument]]( new OSQLSynchQuery[ODocument]( "select from " + id ) ).get( 0 )

    val doc5 = db.newInstance
    doc5.field( "f1", 13 )
    val doc6 = result2.merge( doc5, true, true )
    doc6.save
   
    val result3 = db.query[java.util.List[ODocument]]( new OSQLSynchQuery[ODocument]( "select from " + id ) ).get( 0 )

    println( result3 )
    db.close
  }

}