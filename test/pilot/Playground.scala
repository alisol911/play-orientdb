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

@RunWith( classOf[ JUnitRunner ] )
class Playground extends Specification {
  "create" in {
  
    var uri = "local:/tmp/pilotdb"
    var db = new OObjectDatabaseTx( uri )
    if ( !db.exists ) {
      db.create()
    } else {
      db.open( "admin", "admin" )
    }

    db.begin( TXTYPE.NOTX );
    val doc = new ODocument();

    doc.field( "id", 1 );
    doc.field( "name", "A" );
    doc.setClassName( "Pilot" );
    doc.save();
    doc.reset();
    db.commit();
    db.close()
  }

}