package test.mec.datareceiver;

public class Java7Test {
	final static String value = "one";

	public static void main(String[] args) {
		switch (value) {
		case "one":
			System.out.println("value is one");
			break;
			
		default:
			System.out.println("value is not one");
			break;
		}		
	}

}
