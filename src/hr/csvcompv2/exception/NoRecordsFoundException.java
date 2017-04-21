package hr.csvcompv2.exception;

public class NoRecordsFoundException extends RuntimeException{

	private static final long serialVersionUID = 1997753355232807009L;

	public NoRecordsFoundException()
	{
	}

	public NoRecordsFoundException(String message)
	{
		super(message);
	}

	public NoRecordsFoundException(Throwable cause)
	{
		super(cause);
	}

	public NoRecordsFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NoRecordsFoundException(String message, Throwable cause, 
                                       boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
