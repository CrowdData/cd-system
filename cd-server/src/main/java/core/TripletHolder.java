package core;

public class TripletHolder<T,U,V> {

	T a=null;
	U b=null;
	V c=null;
	
	
public TripletHolder(T a,U b, V c){
	
	this.a=a;
	this.b=b;
	this.c=c;
	
}

T getSubject(){ return a;}
U getProperty(){ return b;}
V getObject(){ return c;}	
}
