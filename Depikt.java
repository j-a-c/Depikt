/*
 *
 * Main class
 * Author: Joshua A. Campbell
 * joshuac.com
 *
 */


public class Depikt
{

    final static int WRITE = 0;
    final static int READ = 1;

    public static void main(String[] args)
    {

        if(args.length != 3)
        {
            //intro text
            System.out.println("\n");
            System.out.println("Depikt - joshuac.com");
            System.out.println("Usage: java Depikt -args input output");
            System.out.println("Args:");
            System.out.println("\t -w : write executable 'input' to bitmap image 'output'");
            System.out.println("\t -r : read executable 'output' from bitmap image 'input'");
            System.out.println("\n");
        }
        else
        {
            if(args[0].equals("-r"))
            {
                readMode(args[1], args[2]);
            }
            else if(args[0].equals("-w"))
            {
                writeMode(args[1], args[2]);
            }
        }
    }//end main

    //read mode
    //
    public static void readMode(String inputName, String outputName)
    {
        //TODO
        //retrieve executable from bitmap image file
        Depiktor depikt = new Depiktor(inputName, outputName, READ);
        depikt.decolor();

    }//end readMode

    public static void writeMode(String inputName, String outputName)
    {
        //write executable to bitmap image file
        Depiktor depikt = new Depiktor(inputName, outputName, WRITE);
        depikt.color();

    }//end writeMode
}
