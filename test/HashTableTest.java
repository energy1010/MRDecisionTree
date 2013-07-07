import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.collections.MapIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.core.Statistics;


public class HashTableTest {
	
	private static final Logger log = LoggerFactory.getLogger(HashTableTest.class);
	
	private Dictionary<Integer, Integer> dictionary = new Hashtable<Integer, Integer>();
	public void init(){
		dictionary.put(1, 1);
		dictionary.put(2, 2);
		dictionary.put(3, 3);
	}
	
	public void change(){
		int oldNum = this.dictionary.get(1);
		this.dictionary.put(1, ++oldNum);
	}
	
	public HashTableTest(){
		init();
	}
	
	
	public void testCopy(){
		List<Integer> aList = new ArrayList<Integer>();
		List<Integer> bList = new ArrayList<Integer>();
		for(int i=0;i<10;i++){
			aList.add(i);
		}
		Collections.copy(bList, aList.subList(2, 5));
//		 log.debug("{} change: {}", aList.toString(), bList.toString());
		System.out.println();
	}
	
	public static void main(String[] args) {
	HashTableTest hashTableTest =	new HashTableTest() ;
//	System.out.println();
//	hashTableTest.change();
//	System.out.println();
//	hashTableTest.testCopy();
//		MapIterator mapIterator = hashTableTest.dictionary.
		Statistics.normalProbability(0.5);
	}

}
