import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import com.younger.tool.hadoop.HdfsWritableUtils;

/**
 * Tests MapWritable
 */
public class TestHdfsWritableUtils extends TestCase {
  /** the test */
  @SuppressWarnings("unchecked")
  public void testMapWritable() {
    Text[] keys = {
        new Text("key1"),
        new Text("key2"),
        new Text("Key3"),
    };
    
    BytesWritable[] values = {
        new BytesWritable("value1".getBytes()),
        new BytesWritable("value2".getBytes()),
        new BytesWritable("value3".getBytes())
    };

    MapWritable inMap = new MapWritable();
    for (int i = 0; i < keys.length; i++) {
      inMap.put(keys[i], values[i]);
    }
    OutputStream outputStream = null;
	try {
		outputStream = new FileOutputStream("/Users/apple/Desktop/test.da");
	} catch (FileNotFoundException e2) {
		e2.printStackTrace();
	}
    DataOutputStream dataOutputStream =new DataOutputStream(outputStream);
    try {
		inMap.write(dataOutputStream);
	} catch (IOException e1) {
		e1.printStackTrace();
	}
    
    MapWritable outMap = new MapWritable(inMap);
    assertEquals(inMap.size(), outMap.size());
    
    for (Map.Entry<Writable, Writable> e: inMap.entrySet()) {
      assertTrue(outMap.containsKey(e.getKey()));
      assertEquals(0, ((WritableComparable) outMap.get(e.getKey())).compareTo(
          e.getValue()));
    }
    
    try {
		DataInputStream dataInputStream = new DataInputStream(new FileInputStream("/Users/apple/Desktop/test.da"));
		MapWritable mapWritable2 = new MapWritable();
		try {
			mapWritable2.readFields(dataInputStream);
			  for (Map.Entry<Writable, Writable> e: mapWritable2.entrySet()) {
				  System.out.println(String.format("key:%s , value:%s",
						  HdfsWritableUtils.getValueFromWritable(e.getKey()), 
						  HdfsWritableUtils.getValueFromWritable(e.getValue())) );
			      assertTrue(outMap.containsKey(e.getKey()));
			      assertEquals(0, ((WritableComparable) outMap.get(e.getKey())).compareTo(
			          e.getValue()));
			    }
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	} catch (FileNotFoundException e1) {
		e1.printStackTrace();
	}
    
    // Now for something a little harder...
    
//    Text[] maps = {
//        new Text("map1"),
//        new Text("map2")
//    };
//    
//    MapWritable mapOfMaps = new MapWritable();
//    mapOfMaps.put(maps[0], inMap);
//    mapOfMaps.put(maps[1], outMap);
//    
//    MapWritable copyOfMapOfMaps = new MapWritable(mapOfMaps);
//    for (int i = 0; i < maps.length; i++) {
//      assertTrue(copyOfMapOfMaps.containsKey(maps[i]));
//      MapWritable a = (MapWritable) mapOfMaps.get(maps[i]);
//      MapWritable b = (MapWritable) copyOfMapOfMaps.get(maps[i]);
//      assertEquals(a.size(), b.size());
//      for (Writable key: a.keySet()) {
//        assertTrue(b.containsKey(key));
//        
//        // This will work because we know what we put into each set
//        
//        WritableComparable aValue = (WritableComparable) a.get(key);
//        WritableComparable bValue = (WritableComparable) b.get(key);
//        assertEquals(0, aValue.compareTo(bValue));
//      }
//    }
  }

  /**
   * Test that number of "unknown" classes is propagated across multiple copies.
   */
  /**
   * Assert MapWritable does not grow across calls to readFields.
   * @throws Exception
   * @see <a href="https://issues.apache.org/jira/browse/HADOOP-2244">HADOOP-2244</a>
   */
  public void testMultipleCallsToReadFieldsAreSafe() throws Exception {
    // Create an instance and add a key/value.
    MapWritable m = new MapWritable();
    final Text t = new Text(getName());
    m.put(t, t);
    // Get current size of map.  Key values are 't'.
    int count = m.size();
    // Now serialize... save off the bytes.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    m.write(dos);
    dos.close();
    // Now add new values to the MapWritable.
    m.put(new Text("key1"), new Text("value1"));
    m.put(new Text("key2"), new Text("value2"));
    // Now deserialize the original MapWritable.  Ensure count and key values
    // match original state.
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    DataInputStream dis = new DataInputStream(bais);
    m.readFields(dis);
    assertEquals(count, m.size());
    assertTrue(m.get(t).equals(t));
    dis.close();
  }

}
