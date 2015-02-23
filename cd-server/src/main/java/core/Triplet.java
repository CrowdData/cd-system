package core;

public class Triplet<T, U, V> {

	T a=null;
	U b=null;
	V c=null;
	
	
public Triplet(T a,U b, V c){
	
	this.a=a;
	this.b=b;
	this.c=c;
	
}

T getBind(){ return a;}
U getObject(){ return b;}
V getDatatype(){ return c;}	
}
