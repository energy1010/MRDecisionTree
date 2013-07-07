package com.younger.constant;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class Enum {
	
	
	/**
	 * attribute type
	 * @author apple
	 *
	 */
	public enum AttrType implements Writable{
		
		Continuous,
		Category,
		Ignored,
		Label;

		public boolean isNumerical() {
	      return this == Continuous;
	    }
	    
	    public boolean isCategorical() {
	      return this == Category;
	    }
	    
	    public boolean isLabel() {
	      return this == Label;
	    }
	    
	    public boolean isIgnored() {
	    	return this==Ignored;
	    }

		@Override
		public void write(DataOutput out) throws IOException {
			WritableUtils.writeEnum(out,this);
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			WritableUtils.readEnum(in, AttrType.class);
//			 Text.writeString(out, enumVal.name()); 
		}

	}

}
