package controllers;

public interface Mimes {
    
    public interface Text {
	String HTML = "text/html";	
    }
    
    public interface Application {
	String JSON = "application/json";
	String JAVASCRIPT = "application/javascript";
    }
    
    String JAVA_PROPERTIES = "application/vnd.java-properties";
    String MICROSOFT_EXCEL = "application/vnd.ms-excel";
    String MICROSOFT_EXCEL_2007 = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
}
