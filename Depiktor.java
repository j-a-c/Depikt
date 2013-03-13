/*
 *
 * Handles reading/writing bitmap/exec file
 *
 * Author: Joshua A. Campbell
 * joshuac.com
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Depiktor
{

    //constant(s)
    private int BYTES_PER_PIXEL = 24;
    //constructor constants
    private final int WRITE = 0;
    private final int READ = 1;
    private final int HIDE = 2;
    private final int FIND = 3;

    //file names
    private String inputName;
    private String outputName;


    //size of data
    private int size = 0;
    //width/height in pixels (bytes per row / bytes per pixel)
    private int width = 0;
    private int height = 0;
    //padding needed
    private int padding = 0;
    //starting address, of the byte where the bitmap image data (pixel array) can be found
    private int offset = 0;

    //constructor
    //@params: input and output file names, mode
    //  mode:   0: -w
    //          1: -r
    public Depiktor(String inputName, String outputName, int mode)
    {
        if(mode == WRITE | mode == HIDE)
        {
            this.inputName = inputName;
            this.outputName = outputName + ".bmp";

        }
        else if(mode == READ)
        {
            this.inputName = inputName + ".bmp"; 
            this.outputName = outputName;
        }
    }

    /*
     * Methods to hide an executable in an existing image file
     */

    public void hide()
    {
        //input executable 
        FileInputStream input = null;
        //original bitmap
        FileInputStream orig = null;
        //output bitmap
        FileOutputStream output = null;

        //initialize streams
        try
        {
            input = new FileInputStream(this.inputName);
        } 
        catch(java.io.FileNotFoundException e)
        {
            System.out.println(this.inputName + " not found.");  
        }
        try
        {
            orig = new FileInputStream(this.outputName);
        }
        catch(java.io.FileNotFoundException e)
        {
            System.out.println(this.outputName + " not found.");
        }    
        try
        {
            output = new FileOutputStream("depikt_" + this.outputName + ".bmp", true);
        }
        catch(java.io.FileNotFoundException e)
        {
            System.out.println("Unable to create depikt_" + this.outputName + ".bmp"); 
        }    

        
        //operations
        readBMPHeader(orig);
        mergeDataAndBitmap(input,orig,output);

        //close streams
        try
        {
            input.close();
            orig.close();
            output.close();
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error closing streams in hide().");
        }

        System.out.println("Executable hidden.");
    }//end hide

    //@params:
    //      executable, original bitmap, output bitmap
    public void mergeDataAndBitmap(FileInputStream input, FileInputStream, orig, FileOutputStream output)
    {
        try
        {
            //skip BMP header and jump
            orig.skip(offset-14); 
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error in mergeDataAndBitmap()");
        }
    }

    /*
     * Methods to read from bitmap image file
     */

    //retrieves an executable from an image file
    public void decolor()
    {

        //input bitmap image file 
        FileInputStream input = null;
        //output executable 
        FileOutputStream output = null;

        //initialize streams
        try
        {
            input = new FileInputStream(this.inputName);
        } 
        catch(java.io.FileNotFoundException e)
        {
            System.out.println(this.inputName + " not found.");  
        }
        try
        {
            output = new FileOutputStream(this.outputName, true);
        }
        catch(java.io.FileNotFoundException e)
        {
            System.out.println(this.outputName + " not found.");
        }    

        //operations
        readBMPHeader(input);
        readData(input, output);
        
        try
        {
            //close streams
            input.close();
            output.close();
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error closing streams in decolor().");
        }

        System.out.println("Executable written.");
    }//end decolor

    //read the BMP header of the bitmap image file
    public void readBMPHeader(FileInputStream input)
    {
        try
        {
            //skip first two bytes (magic number)
            input.skip(2);
            //record size of BMP file (in bytes)
            size = input.read() | input.read() << 8 | input.read() << 16 | input.read() << 24;
            //next two byte (used in the spec) hold padding
            padding = input.read() | (input.read() << 8);
            //skip two bytes (unused)
            input.skip(2);
            //pixel data offset
            offset = input.read() | input.read() << 8 | input.read() << 16 | input.read() << 24;
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error in readBMPHeader()");
        }
    }//end readBMPHeader 

    //read executable data from the bitmap image file
    //and write to output
    public void readData(FileInputStream input, FileOutputStream output)
    {
        try
        {
            //TODO assumes the smallest DIB header
            //skip the 40 = 54 - 14 remaining bytes in the BMP and DIB headers
            input.skip(40);
            //remaining bytes - padding at end, belong to the program
            int remaining = (size - offset) - padding;

            for(int i = 0; i < remaining; i++)
                output.write( input.read() );
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error in readData()");
        }

    }//end readData

    /*
     * Methods to write to bitmap image file
     */
    
    //transforms the executable into a bitmap image file
    public void color()
    {

        //input executable 
        FileInputStream input = null;
        //output bitmap 
        FileOutputStream output = null;

        //initialize streams
        try
        {
            input = new FileInputStream(this.inputName);
        } 
        catch(java.io.FileNotFoundException e)
        {
            System.out.println(this.inputName + " not found.");  
        }
        try
        {
            output = new FileOutputStream(this.outputName, true);
        }
        catch(java.io.FileNotFoundException e)
        {
            System.out.println(this.outputName + " not found.");
        }

        //operations
        writeHeaders(input, output);
        writeData(input, output);
        writePadding(output);

        try
        {
        //close streams
        input.close();
        output.close();
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error closing streams in color()");
        }
        
        System.out.println("Width (px): " + width + "\n"
                + "Height (px): " + height);

    }//end color

    //writes the BMP and DIB headers to the bitmap image file
    public void writeHeaders(FileInputStream input, FileOutputStream output)
    {
        try
        {
            //size of data in bytes
            size = input.available();
            //number of rows (for a square image)
            int nrows = (int) Math.ceil( Math.sqrt( size/3 ) );
            //bytes per row
            padding = 3 * nrows;
            width = height = padding / BYTES_PER_PIXEL;
            //make row have 4 byte alignment
            padding += (padding % 4); 
            //total padding needed
            padding = (padding * nrows) - size;

            //create BMP and DIB headers
            byte[] headers = generateHeaders(size, padding, width, height);

            //write headers to file
            output.write(headers);
            
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error in writeHeaders() in Depiktor");
        }
    }//end writeHeaders

    //writes the executable data to the bitmap data file
    public void writeData(FileInputStream input, FileOutputStream output)
    {
        try{
       
            //array of bytes read from input
            byte[] byteArray = new byte[1024];
            
            //write all executable data to bitmap image file
            int bytesRead = input.read(byteArray);
            while(bytesRead != -1)
            {
                output.write(byteArray, 0, bytesRead);
                bytesRead = input.read(byteArray);
            }

        }
        catch(java.io.IOException e)
        {
            System.out.println("Error in writeData() in Depiktor");
        }
    }//end writeData

    //writes the padding to the bitmap data file
    public void writePadding(FileOutputStream output)
    {
        try
        {
            for(int i = 0; i < padding; i++)
                output.write(0);
        }
        catch(java.io.IOException e)
        {
            System.out.println("Error in writePadding()");
        }
    }//end writePadding

    //generates the BMP and DIB headers
    //DIB version is Windows DIB header BITMAPINFOHEADER
    public byte[] generateHeaders(int size, int padding, int width, int height){
        //contains the headers
        byte[] headers = new byte[54];  

        /*
         * BMP header
         */

        //total bitmap file size
        //BMP header size + DIB header size + data size + padding
        int totalFileSize = 14 + 40 + size + padding;
        //total data size
        int totalDataSize = size + padding;

        //magic number
        headers[0] = 0x42; 
        headers[1] = 0x4D;
        //size of BMP file
        headers[2] = (byte)  (totalFileSize & 0x000000FF);
        headers[3] = (byte) ((totalFileSize & 0x0000FF00) >> 8);
        headers[4] = (byte) ((totalFileSize & 0x00FF0000) >> 16);
        headers[5] = (byte) ((totalFileSize & 0xFF000000) >> 24);
        //hold amount of padding (unused otherwise)
        headers[6] = (byte)  (padding & 0x00FF);
        headers[7] = (byte) ((padding & 0xFF00) >> 8);
        //unused
        headers[8] = 0x00;
        headers[9] = 0x00;
        //offset where bitmap data can be found
        headers[10] = 0x36;
        headers[11] = 0x00;
        headers[12] = 0x00;
        headers[13] = 0x00;

        /*
         * DIB Header
         */

        //bytes in header (counting these) = 40
        headers[14] = 0x28;
        headers[15] = 0x00;
        headers[16] = 0x00;
        headers[17] = 0x00;
        //width of bitmap in pixels
        headers[18] = (byte)  (width & 0x000000FF);
        headers[19] = (byte) ((width & 0x0000FF00) >> 8);
        headers[20] = (byte) ((width & 0x00FF0000) >> 16);
        headers[21] = (byte) ((width & 0xFF000000) >> 24);
        //height of bitmap in pixels
        //positive for bottom to top pixel order
        headers[22] = (byte)  (height & 0x000000FF);
        headers[23] = (byte) ((height & 0x0000FF00) >> 8);
        headers[24] = (byte) ((height & 0x00FF0000) >> 16);
        headers[25] = (byte) ((height & 0xFF000000) >> 24);
        //number of color planes being used
        headers[26] = 0x01;
        headers[27] = 0x00;
        //bits per pixel
        headers[28] = (byte)  (BYTES_PER_PIXEL & 0x00FF);
        headers[29] = (byte) ((BYTES_PER_PIXEL & 0xFF00) >> 8);
        //pixel array compression used
        headers[30] = 0x00;
        headers[31] = 0x00;
        headers[32] = 0x00;
        headers[33] = 0x00;
        //size of raw data in pixel array including padding
        headers[34] = (byte)  (totalDataSize & 0x000000FF);
        headers[35] = (byte) ((totalDataSize & 0x0000FF00) >> 8);
        headers[36] = (byte) ((totalDataSize & 0x00FF0000) >> 16);
        headers[37] = (byte) ((totalDataSize & 0xFF000000) >> 24);
        //horizontal resoltion (2835 pixels/meter)
        headers[38] = 0x13;
        headers[39] = 0x0B;
        headers[40] = 0x00;
        headers[41] = 0x00;
        //vertical resoltion (2835 pixels/meter)
        headers[42] = 0x13;
        headers[43] = 0x0B;
        headers[44] = 0x00;
        headers[45] = 0x00;
        //colors in palette - 0 = 2^n
        headers[46] = 0x00;
        headers[47] = 0x00;
        headers[48] = 0x00;
        headers[49] = 0x00;
        //important colors - 0 = all
        headers[50] = 0x00;
        headers[51] = 0x00;
        headers[52] = 0x00;
        headers[53] = 0x00;
        
        return headers;
    }//end generateHeaders

}
