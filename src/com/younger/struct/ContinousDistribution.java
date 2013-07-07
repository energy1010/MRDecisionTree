package com.younger.struct;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

import com.younger.tool.MathUtil;


	/**
	 * the distribution for Continuous attribute
	 * @author apple
	 *
	 */
	public class ContinousDistribution implements Writable{
		private double m_mean;
		private double m_std;
		
		public ContinousDistribution(){
			
		}
		
		public ContinousDistribution(double m_mean, double m_std) {
			super();
			this.m_mean = m_mean;
			this.m_std = m_std;
		}

		public double getMean() {
			return this.m_mean;
		}
		
		public void setMean(double m_mean) {
			this.m_mean = m_mean;
		}
		
		public double getStd() {
			return this.m_std;
		}
		public void setStd(double m_std) {
			this.m_std = m_std;
		}
		
		/**
		 * get the normal probability for value 
		 * @param value
		 * @return
		 */
		public  double getProbabilityForValue(double value){
//			return Statistics.normalProbability( (value-m_mean)/ m_std );
			return MathUtil.normalProbability(value,m_mean,m_std);
		}
	
		@Override
		public String toString() {
//			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
			return String.format("%s,%s",m_mean,m_std);
		}

		
		@Override
		public void write(DataOutput out) throws IOException {
//			VectorWritable vectorWritable = new VectorWritable();
			out.writeDouble(m_mean);
			out.writeDouble(m_std);
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			m_mean = in.readDouble();
			m_std = in. readDouble();
		}
	}
	
