/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.younger.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Yasser Ganjisaffar <ganjisaffar at gmail dot com>
 */

public class Timer {
	
	private static Timer instance = null;
	
	private static final Logger log= LoggerFactory.getLogger(Timer.class);
	
	private Timer(){
		
	}
	
	private Timer(long start){
		this.start = start;
	}
	
	public static Timer Instance(){
		if(instance==null){
			instance = new Timer();
		}
		return instance;
	}
	
	public static Timer Instance(long start){
		if(instance==null){
			instance = new Timer(start);
		}
		return instance;
	}
	
	private long start;
	
	public void start() {
		log .debug("Time start at "+start);
		start = System.currentTimeMillis();
	}
	
	
	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	/**
	 * 1 s = 1000 ms
	 * 1 second = 1000 millis
	 * @return
	 */
	public long getElapsedMillis() {
		long end = System.currentTimeMillis();
		log.debug("Time elapsed :" +end+" millis");
		return end - start;
	}
	
	public long getElapsedSeconds() {
		long sec=getElapsedMillis() / 1000;
		log.debug("Time elapsed :" +sec+" seconds");
		return sec;
	}


	  /**
	   * Formats a time interval in milliseconds to a String in the form "hours:minutes:seconds:millis"
	   */
	  public static String elapsedTime(long milli) {
	    long seconds = milli / 1000;
	    milli %= 1000;
	    
	    long minutes = seconds / 60;
	    seconds %= 60;
	    
	    long hours = minutes / 60;
	    minutes %= 60;
	    
	    return hours + "h " + minutes + "m " + seconds + "s " + milli;
	  }
}
