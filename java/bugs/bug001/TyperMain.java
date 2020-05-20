

public class TyperMain {

	  public static void main(String args[]) {
		    Typer parser;
		    if (args.length == 0) {
		      System.out.println("Typer :  Reading from standard input . . .");
		      parser = new Typer(System.in);
		    } else if (args.length == 1) {
		      System.out.println("Typer :  Reading from file " + args[0] + " . . .");
		      try {
		        parser = new Typer(new java.io.FileInputStream(args[0]));
		      } catch (java.io.FileNotFoundException e) {
		        System.out.println("Typer :  File " + args[0] + " not found.");
		        return;
		      }
		    } else {
		      System.out.println("Typer :  Usage is one of:");
		      System.out.println("         java ASN.1 inputfile");
		      return;
		    }
		    try {
		      parser.ModuleDefinitionList();
		      System.out.println("Typer :  ASN.1 file parsed successfully.");
		    } catch (ParseException e) {
		      e.printStackTrace();
		    }
		  }

}
