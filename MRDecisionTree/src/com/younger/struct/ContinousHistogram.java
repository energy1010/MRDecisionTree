package com.younger.struct;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * ContinousHistogram for numerous attribute
 * < preNum, postNum>
 * 	m_preClassNum = preClassNum;
 *	m_postClassNum = postClassNum;
 * @author Administrator
 *
 */
public class ContinousHistogram extends AbstractHistogram{

	public ContinousHistogram(){
		
	}
	public ContinousHistogram(Dictionary<Integer, Integer> preClassNum,
			Dictionary<Integer, Integer> postClassNum) {
		super();
		this.m_preClassNum = preClassNum;
		this.m_postClassNum = postClassNum;
	}
	public static final long serialVersionUID = -194123450607427345L;
	
	
	
	private Dictionary<Integer, Integer> m_postClassNum= null;



	/**
	 * <classIndex classNum>
	 */
	private Dictionary<Integer, Integer> m_preClassNum= null;


	/**
	 * @return <classValue ,num>
	 */
	public Dictionary<Integer, Integer> getPostClassNum() {
		return m_postClassNum;
	}


   /**
    * @return <classValue ,num>
    */
	public Dictionary<Integer, Integer> getPreClassNum() {
		return m_preClassNum;
	}



	public void setPostClassNum(Dictionary<Integer, Integer> postClassNum) {
		this.m_postClassNum = postClassNum;
	}



	public void setPreClassNum(Dictionary<Integer, Integer> preClassNum) {
		this.m_preClassNum = preClassNum;
	}
	
	
	public  void serialize(String fileName) throws Exception{
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
		outputStream.writeObject(this);
		outputStream.close();
	}
	
	public ContinousHistogram deserialize(String fileName) throws Exception{
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
		ContinousHistogram histogram = (ContinousHistogram)inputStream.readObject();
		inputStream.close();
		return histogram;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	}
	

}
