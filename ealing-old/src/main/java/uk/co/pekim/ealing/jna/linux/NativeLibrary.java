package uk.co.pekim.ealing.jna.linux;

import com.sun.jna.Native;

/**
 * Native library utility methods.
 * 
 * @author Mike D Pilsbury
 */
class NativeLibrary {
	/**
	 * Load a native library from a list of possible alternative names.
	 * The names will be tried in the order they are provided, until one loads
	 * successfully, or until they have all been tried.
	 * 
	 * @param libraryNames the library name  alternatives to try.
	 * @param interfaceClass the class the loaded library should be represented as.
	 * @return a library interface representing the succesfully loaded library.
	 * @throws UnsatisfiedLinkError if none of the name alternatives results in a successful load.
	 */
	@SuppressWarnings("unchecked")
	static <T> T load(String[] libraryNames, Class<T> interfaceClass) {
		StringBuilder exceptionMessages = new StringBuilder();
		
		for (int ln = 0; ln < libraryNames.length; ln++) {
			String libraryName = libraryNames[ln];
			
			try {
				return (T) Native.loadLibrary(libraryName, interfaceClass);
			} catch (UnsatisfiedLinkError ule) {
				if (exceptionMessages.length() != 0) {
					exceptionMessages.append(", ");
				}
				exceptionMessages.append(ule.getMessage());
			}
		}
		
		throw new UnsatisfiedLinkError(exceptionMessages.toString());
	}
}
