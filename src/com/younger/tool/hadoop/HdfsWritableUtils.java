package com.younger.tool.hadoop;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;
import org.slf4j.Logger;

import com.younger.data.Data;

public class HdfsWritableUtils  extends MapWritable{

	private static final  Logger log = org.slf4j.LoggerFactory.getLogger(HdfsWritableUtils.class);
	
	/**
	 * write collection to dataoutput
	 * @param out
	 * @param s
	 */
	public static <T> void writeCollection(DataOutput out,Collection<T> s) {
	try {
		if(s==null||s.isEmpty()){
			out.writeBoolean(false); //step 1
			return;
		}
		out.writeBoolean(true);
		out.writeInt(s.size());//step 2
		int run=0;
		for(Iterator<T> iterator= s.iterator(); iterator.hasNext(); run++){
			T d = iterator.next();
			if(run==0){
			WritableUtils.writeString(out,d.getClass().getName());//step 3
			}
			if(d instanceof Integer){
				out.writeInt((Integer)d);
			}
			else if(d instanceof Double)
			out.writeDouble((Double)d);
			else if (d instanceof Float) {
				out.writeFloat((Float) d);
			}
			else if (d instanceof Boolean) {
				out.writeBoolean((Boolean) d);
			}
			else if (d instanceof Byte) {
				out.writeByte((Byte) d);
			}
			else if (d instanceof Character) {
				out.writeChar((Character) d);
			}
			else if (d instanceof Short) {
				out.writeShort((Short) d);
			}
			else if (d instanceof Long) {
				out.writeLong((Long) d);
			}
			else if (d instanceof String) {
//				Text.writeString(out,(String)d);
				WritableUtils.writeString(out, (String)d);
			}else if(d instanceof Enum){
				WritableUtils.writeEnum(out,(Enum<?>)d);
			}else{
				((Writable)d).write(out); //step 4
				    }
			}
	}catch(Exception e){
		System.err.println(e.getMessage());
		e.printStackTrace();
	}
		
	}
	
	/**
	 * @deprecated
	 * @param out
	 * @param s
	 * @param classType
	 */
	public static <T> void writeCollection(DataOutput out,Collection<T> s,Class classType) {
		try {
		if(s==null||s.isEmpty()){
			out.writeBoolean(false);
			return;
		}
		out.writeBoolean(true);
		out.writeInt(s.size());
		int run=0;
		for(Iterator<T> iterator= s.iterator(); iterator.hasNext(); run++){
			T d = iterator.next();
			if(run==0){
			WritableUtils.writeString(out,d.getClass().getName());
			}
			if(classType.equals(Integer.class)){
				out.writeInt((Integer)d);
			}
			else if(classType.equals(Double.class))
			out.writeDouble((Double)d);
			else if( classType.equals(Float.class)) {
				out.writeFloat((Float) d);
			}
			else if (classType.equals(Boolean.class)) {
				out.writeBoolean((Boolean) d);
			}
			else if (classType.equals(Byte.class)) {
				out.writeByte((Byte) d);
			}
			else if (classType.equals(Character.class)) {
				out.writeChar((Character) d);
			}
			else if (classType.equals(Short.class)) {
				out.writeShort((Short) d);
			}
			else if (classType.equals(Long.class)) {
				out.writeLong((Long) d);
			}
			else if (classType.equals(String.class)) {
				WritableUtils.writeString(out, (String)d);
			}	
			else if(classType.equals(Enum.class)){
				WritableUtils.writeEnum(out,(Enum<?>)d);
			}else{
				((Writable)d).write(out);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static <T> Collection<T> readCollection(DataInput in){
		try {
		Boolean isEmpty= in.readBoolean();
		if(!isEmpty) return null;
		int size = in.readInt();
		assert(size>0);
		Collection<T> collection = new Vector<T>(size);
		String className = WritableUtils.readString(in);
		Class	classType = Class.forName(className);
		for(int i=0;i<size;i++){
			 Object d = classType.newInstance();
			if(classType.equals(Integer.class)){
				d= in.readInt();
			}
			else if(classType.equals( Double.class))
			d=in.readDouble();
			else if (classType.equals(Float.class)) {
				d=in.readFloat();
			}
			else if (classType.equals(Boolean.class)) {
				d= in.readBoolean();
			}
			else if (classType.equals(Byte.class)) {
				d=in.readByte();
			}
			else if (classType.equals(Character.class)) {
				d=in.readChar();
			}
			else if (classType.equals(Short.class)) {
				d = in.readShort();
			}
			else if (classType.equals(Long.class)) {
				d = in.readLong();
			}
			else if (classType.equals(String.class)) {
				d = WritableUtils.readString(in);
			}else if(classType.equals(Enum.class)){
				d = WritableUtils.readEnum(in, classType);
			}
			else{
				((Writable)d).readFields(in);
			}
			collection.add((T) d);
		}
		return collection;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	/**
	 * Convert from writable 
	 * </br> return Vector
	 * @param writables
	 * @param srcClassType
	 * @return¡¡Vector<Object> S S
	 */
	public static  Collection<Object> convertFromWritable(Collection<? extends Writable> writables,Class srcClassType) {
		Collection<Object> collections =  new Vector<Object>();
		 Writable d;
		 Object value ;
			for(int i=0;i<writables.size();i++){
				d = (Writable) writables.toArray()[i];
				value = getValueFromWritable(d);
				collections.add(value);
			}	
		return collections;
	}
	
	public static Collection<Writable> convertToWritable(Collection<?> collection,Class srcClassType) {
		if(collection==null||collection.isEmpty()) return null;
		Collection<Writable> collectionWritables = new Vector<Writable>(collection.size());
		for (Object obj : collection) {
			collectionWritables.add(setValueToWritable(obj));
		}
		return collectionWritables;
	}
	
	
	
	public static void main(String[] args) throws IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, ClassNotFoundException, NoSuchMethodException {
		testCollection();
	}
	
	 public static void testCollection() throws IOException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, ClassNotFoundException, NoSuchMethodException {
		 
		 Text[] keys = {
		        new Text("key1"),
		        new Text("key2"),
		        new Text("Key3"),
		    };
		    IntWritable[] keys1= {
			        new IntWritable(1),
			        new IntWritable(2),
			        new IntWritable(3),
			        new IntWritable(4)
			    };
		    
		    DoubleWritable[] keys2={
		    		new DoubleWritable(1.0),
		    		new DoubleWritable(2.0),
		    		new DoubleWritable(3.0)
		    };
		    Vector<Writable> vector = new Vector<Writable>();
		    for(int i=0;i<keys2.length;i++){
		    	vector.add(keys2[i]);
		    }
//		 Collection collection =   HdfsWritableUtils.convertFromWritable(vector, DoubleWritable.class);
//		 Collection collection =   HdfsWritableUtils.convertFromWritable(vector, Text.class);
//		 Collection collection1 =   HdfsWritableUtils.convertFromWritable(vector, IntWritable.class, int.class);
//		 for (int i = 0; i < keys.length; i++) {
//			System.out.println(collection.toArray()[i]);
//			System.out.println(collection.toArray()[i].getClass());
//		}
		 Collection<Data> datas = new LinkedList<Data>();
		 for(int i=0;i<5;i++){
		 datas.add(new Data(i));
		 }
		Collection<Writable> writables= convertToWritable(datas,Data.class);
		 String pathname="/Users/apple/Desktop/1.dat";
		 File file = new File(pathname);
		 if(file.exists()){
			 file.delete();
			 file.createNewFile();
		 }
		 DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
		 writeCollection(out, datas);
		 DataInputStream in = new DataInputStream(new FileInputStream("/Users/apple/Desktop/1.dat"));
		Collection<Data> collection2 = readCollection(in);
		 log.debug(collection2.toString());
//		    BytesWritable[] values = {
//		        new BytesWritable("value1".getBytes()),
//		        new BytesWritable("value2".getBytes()),
//		        new BytesWritable("value3".getBytes())
//		    };
		 	Map<Writable, Writable> inMap = new HashMap<Writable, Writable>(keys.length);
//		    HdfsMapWritable inMap = new HdfsMapWritable();
		    for (int i = 0; i < keys.length; i++) {
		      inMap.put(keys[i], keys1[i]);
		    }
		    Map map = convertFromMapWritable(inMap);
		    Map map1 =convertToMapWritable(map);
//		    MapWritable outMap = new MapWritable(inMap);
//		    assertEquals(inMap.size(), outMap.size());
		    //		    for (Map.Entry<Writable, Writable> e: inMap.entrySet()) {
//		      assertTrue(outMap.containsKey(e.getKey()));
//		      assertEquals(0, ((WritableComparable) outMap.get(e.getKey())).compareTo(
//		          e.getValue()));
//		    }
//		    
//		    // Now for something a little harder...
//		    
//		    Text[] maps = {
//		        new Text("map1"),
//		        new Text("map2")
//		    };
//		    
//		    MapWritable mapOfMaps = new MapWritable();
//		    mapOfMaps.put(maps[0], inMap);
//		    mapOfMaps.put(maps[1], outMap);
//		    
//		    MapWritable copyOfMapOfMaps = new MapWritable(mapOfMaps);
//		    for (int i = 0; i < maps.length; i++) {
//		      assertTrue(copyOfMapOfMaps.containsKey(maps[i]));
//		      MapWritable a = (MapWritable) mapOfMaps.get(maps[i]);
//		      MapWritable b = (MapWritable) copyOfMapOfMaps.get(maps[i]);
//		      assertEquals(a.size(), b.size());
//		      for (Writable key: a.keySet()) {
//		        assertTrue(b.containsKey(key));
//		        
//		        // This will work because we know what we put into each set
//		        
//		        WritableComparable aValue = (WritableComparable) a.get(key);
//		        WritableComparable bValue = (WritableComparable) b.get(key);
//		        assertEquals(0, aValue.compareTo(bValue));
//		      }

	 }
	
	 
	 /**
	  * get the value from Writable
	  * if can't get the value then return itself
	  * @param writable
	  * @return
	  */
	 public static Object getValueFromWritable(Writable writable){
		 Object value;
				if(writable instanceof IntWritable ){
					value = (Integer)((IntWritable)writable).get();
				}
				else if(writable instanceof DoubleWritable ) {
					value = (Double)((DoubleWritable)writable).get();
				}
				else if (writable instanceof FloatWritable ) {
					value = (Float)((FloatWritable)writable).get();
				}
				else if (writable instanceof BooleanWritable ) {
					value = (Boolean)((BooleanWritable)writable).get();
				}
				else if (writable instanceof ByteWritable ) {
					value = (Byte)((ByteWritable)writable).get();
				}
				else if (writable instanceof BytesWritable) {
					value = new String ( ((BytesWritable)writable).getBytes());
				}
				else if (writable instanceof LongWritable ) {
					value = (Long)((LongWritable)writable).get();
				}
				else if (writable instanceof Text ) {
					value = new String(((Text) writable).getBytes());
				}
				else{
					value = writable;
//					value = null;
//					throw new IOException("error type");
				}
				return value;
	 }
	 
	 

	 
	 /**
	  * 
	  * @param obj
	  * Note: except other class return null
	  * @return
	  */
	 public static Writable setValueToWritable(Object obj){
		 Writable value ;
			if( (obj instanceof Integer)||obj.equals(int.class) ){
				value = new IntWritable((Integer)obj);
			}
			else if(obj instanceof Double || obj.equals(double.class) ) {
				value = new DoubleWritable((Double)obj);
			}
			else if (obj instanceof Float ||obj.equals(float.class) ) {
				value = new FloatWritable((Float)obj);
			}
			else if (obj instanceof Boolean ) {
				value =  new BooleanWritable((Boolean)obj);
			}
			else if (obj instanceof Byte ) {
				value =  new ByteWritable((Byte)obj);
			}
			else if (obj instanceof Long ||obj.equals(long.class) ) {
				value =  new LongWritable((Long)obj);
			}
			else if (obj instanceof String ) {
				value = new Text((String) obj);
			}
			else{
				if(!(obj instanceof Writable))
					value = null;
//				obj.getClass().asSubclass(obj.getClass());
				value = (Writable) obj;
			}
			return value; 
	 }
	 
	 
	 public static Map<Writable,Writable> convertToMapWritable(Map<?, ?> map){
		 if(map==null||map.isEmpty()){
			 return null;
		 }
		 Map<Writable ,Writable> mapWritableMap;
		 if(map instanceof Hashtable){
			 mapWritableMap= new Hashtable<Writable, Writable>(map.size());
		 }else{
			 mapWritableMap = new HashMap<Writable, Writable>(map.size());
		 }
		 for(Map.Entry<?,?> entry : map.entrySet()){
			Object keyObject= entry.getKey();
			Object valueObject = entry.getValue();
			Writable keyWritable = setValueToWritable(keyObject);
			Writable valueWritable = setValueToWritable(valueObject);
			mapWritableMap.put(keyWritable, valueWritable);
		 }
		 return mapWritableMap;
	 }
	 
	 
	 public static Map<?,?> convertFromMapWritable(Map<Writable, Writable> inMap){
		 if(inMap==null||inMap.isEmpty()){
			 return null;
		 }
		 Map map ;
		 if(inMap instanceof Hashtable){
			 map= new Hashtable(inMap.size());
		 }else{
			 map = new HashMap(inMap.size());
		 }
		 for(Entry<Writable, Writable> entry:  inMap.entrySet()){
			Writable keyObject= entry.getKey();
			Writable valueObject = entry.getValue();
			Object keyWritable = getValueFromWritable(keyObject);
			Object valueWritable = getValueFromWritable(valueObject);
			map.put(keyWritable, valueWritable);
		
		 }
		 return map;
	 }
	 
	 
	 /**
	  * write the class which implements Map interface
	  * @param map {@link java.util.Map}
	  * @param out
	  */
	 public static  void writeMap(Map<?,?> map, DataOutput out) {
			try {
				if(map==null||map.isEmpty()){
					out.writeBoolean(false);
					return;
				}
				out.writeBoolean(true);
				int mapClassString;
				if(map instanceof Hashtable){
					mapClassString = 0;//"hashtable";
				}else if(map instanceof LinkedHashMap){
					mapClassString = 1; 
//				}else if(map instanceof SortedMap){
//					mapClassString  = 2;
//				}else if(map instanceof TreeMap){
//					mapClassString = 3;
				}else{
					mapClassString = 2; //hashmap
				}
				out.writeInt(map.size());
				out.writeInt(mapClassString);
				MapWritable mapWritable = new MapWritable(); 
				Map<Writable, Writable> map2=	convertToMapWritable(map);
				mapWritable.putAll(map2);
				mapWritable.write(out);
			} catch (IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}

	 public static <K,V> Map<K, V> readMap(DataInput in){
		 try {
			boolean isEmpty = in.readBoolean();
			if(isEmpty) return null;
			int mapSize = in.readInt();
			int mapClassString = in.readInt();
			Map<K, V> map ;
			switch (mapClassString) {
			case 0:
				map=new Hashtable<K, V>(mapSize);
				break;
			case 1:
				map = new LinkedHashMap<K, V>(mapSize); 
				break;
//				map = new SortedMap<K, V>(mapSize);
//			case 2:
//				map =  new TreeMap<K, V>(mapSize);
			default:
				map = new HashMap<K, V>(mapSize);
				break;
			}
			MapWritable mapWritable = new MapWritable();
			mapWritable.readFields(in);
			Set<Map.Entry<Writable, Writable>> set = mapWritable.entrySet();
			for(Map.Entry<Writable, Writable> e:set){
				map.put(((K)getValueFromWritable(e.getKey())), ((V)getValueFromWritable(e.getValue())));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		 return null;
	 }
	 
}
