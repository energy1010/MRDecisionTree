package com.younger.exception;


/**
 * Exception that is raised by an object that is unable to process 
 * data with missing values.
 *
 */
public class NoSupportForMissingValuesException
  extends Exception {

  /** for serialization */
  private static final long serialVersionUID = 5161175307725893973L;

  /**
   * Creates a new NoSupportForMissingValuesException with no message.
   *
   */
  public NoSupportForMissingValuesException() {

    super();
  }

  /**
   * Creates a new NoSupportForMissingValuesException.
   *
   * @param message the reason for raising an exception.
   */
  public NoSupportForMissingValuesException(String message) {

    super(message);
  }
}
