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

    val db = new ODatabaseDocumentTx( "memory:test" );
    db.create();

    val doc = db.newInstance();
    doc.reset();
    doc.setClassName( "MyDocument" );
    doc.field( "f1", 11 );
    db.save( doc );

    val id = doc.getIdentity().toString();

    val result = db.query[java.util.List[ODocument]]( new OSQLSynchQuery[ODocument]( "select from " + id ) );
    val d = result.get(0)
    System.out.println( d.toJSON() );

    db.close();
  }

}