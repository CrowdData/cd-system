package core;

public class NGHandler {
	

public static String getDefaultNamespace(String datasetID){
	return Strings.DEFAULT_URI+datasetID+"/";	
}
public static  String getProvenanceString(String datasetID){
		return Strings.DEFAULT_URI+datasetID+"/provenance/";
	}
public static String getMetadataString(String datasetID){
	return Strings.DEFAULT_URI+datasetID+"/metadata/";
	}
public static String getSchemaString(String datasetID){
	return Strings.DEFAULT_URI+datasetID+"/schema/";
}
public  static String getDataString(String datasetID){
	return Strings.DEFAULT_URI+datasetID+"/data/";		
}
public  static String getKAString(String datasetID){
	return Strings.DEFAULT_URI+datasetID+"/ka/";
}




}
