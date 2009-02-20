package uk.co.pekim.ealing.datatype;


interface Marshaller {
	public BaseDataType createDataType(byte[] data);
}
