package com.younger.tool;


public class ReflectUtil {

	private static ReflectUtil instance;
	
	private ReflectUtil(){
		
	}
	
	public static ReflectUtil instance(){
		if(instance ==null){
			instance = new ReflectUtil();
		}
		return instance;
	}
	
	  /**
	   * Invokes the named method of the provided class name.
	   * @param className the name of the class
	   * @param methodName the name of the method to invoke
	   * @param args the command line arguments
	   */
	  public static void invokeMethod(String className, String methodName, 
	                                  String [] args) {
	    try {
	      Class.forName(className)
	        .getMethod(methodName, new Class[] {String[].class})
	        .invoke(null, new Object[] {args});
	    } catch (Exception e) {
	      InternalError error = new InternalError(methodName);
	      error.initCause(e);
	      throw error;
	    }
	  }

	  /**
	   * Invokes the main method of the provided class name.
	   * @param className the name of the class
	   * @param args the command line arguments
	   */
	  public static void invokeMain(String className, String[] args) {
	    try {
	      Class.forName(className)
	        .getMethod("main", new Class[] {String[].class})
	        .invoke(null, new Object[] {args});
	    } catch (Exception e) {
	      InternalError error = new InternalError("invoke Main error!");
	      error.initCause(e);
	      throw error;
	    }
	  }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
