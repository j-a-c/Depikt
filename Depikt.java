/*
 *
 * Main class
 * Author: Joshua A. Campbell
 * joshuac.com
 *
 */


public class Depikt
{

    //Depiktor constructor constants
    private final static int WRITE = 0;
    private final static int READ = 1;
    private final static int HIDE = 2;
    private final static int FIND = 3;

    public static void main(String[] args)
    {

        if(args.length != 3)
        {
            //intro text
            System.out.println("\n");
            System.out.println("Depikt - joshuac.com");
            System.out.println("Joshua A. Campbell\n");
            System.out.println("Usage: java Depikt -arg in out\n");
            System.out.println("Args:");
            System.out.println("\t-w : write executable 'in' to new bitmap image 'out'");
            System.out.println("\t\tBitmap 'output' should not exist already.");
            System.out.println("\t-h : hide executable 'in'  in an existing bitmap image 'out'");
            System.out.println("\t\tA new image called out_depikt.bmp will be created");
            System.out.println("\t-r : read executable 'out' from bitmap image 'in' (of -w format)");
            System.out.println("\t\tExecutable 'output' should not exist.");
            System.out.println("\t-f : find executable 'out' from a bitmap image 'in' (of -h format)");
            System.out.println("\n");
        }
        else
        {
            String mode = args[0];

            if(mode.equals("-r"))
            {
                readMode(args[1], args[2]);
            }
            else if(mode.equals("-w") || mode.equals("-h"))
            {
                writeMode(args[1], args[2], mode);
            }
        }
    }//end main

    //read mode
    //-r : read executable from a bitmap image
    //-f : find an executable hidden in a bitmap image
    public static void readMode(String inputName, String outputName)
    {
        //TODO
        //retrieve executable from bitmap image file
        Depiktor depikt = new Depiktor(inputName, outputName, READ);
        depikt.decolor();

    }//end readMode

    //write mode
    //-w : write executable to a new bitmap image
    //-h : hide executable in a pre-existing image, but rename it
    public static void writeMode(String inputName, String outputName, String mode)
    {
        //write executable to bitmap image file
        if(mode.equals("-w"))
        {
            Depiktor depikt = new Depiktor(inputName, outputName, WRITE);
            depikt.color();
        }
        else //hide executable
        {
            Depiktor depikt = new Depiktor(inputName, outputName + "_depikt", HIDE);
            depikt.hide();
        }

    }//end writeMode
}
