package ivt.aos.DevImage;

/**
 * Created by dev on 17.04.16.
 */
import java.nio.*;
import java.io.*;
import java.util.Calendar;
public final class ImageRGB {
    // ------------------- TIFF CONSTANTS -------------------
    //private final static short BYTE = 1;
    private final static short ASCII = 2;
    private final static short SHORT = 3;
    private final static short LONG = 4;
    private final static short RATIONAL = 5;

    private final static short HEADER_TIFF_MAGIC_NUMBER = 42;

    // ------------------- HEADER -------------------
    private final static byte[] HEADER_BYTEORDER =
            ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? new byte[]{'I', 'I'} : new byte[]{'M', 'M'};
    private final static int HEADER_FIRST_IFD_OFFSET = 8;

    // ------------------- IMAGE FILE DIRECTORY -------------------

    private final static short IFD_ENTRY_COUNT = 16;

    // IFD ENTRIES FOR FULL-RGB IMAGES
    private final static short IFD_NEW_SUBFILE_TYPE = 254;
    private final static short IFD_IMAGE_WIDTH = 256;
    private final static short IFD_IMAGE_LENGTH = 257;
    private final static short IFD_BITS_PER_SAMPLE = 258;
    private final static short IFD_COMPRESSION = 259;
    private final static short IFD_PHOTOMETRIC_INTERPOLATION = 262;
    private final static short IFD_STRIP_OFFSETS = 273;
    private final static short IFD_SAMPLES_PER_PIXEL = 277;
    private final static short IFD_ROWS_PER_STRIP = 278;
    private final static short IFD_STRIP_BYTE_COUNTS = 279;
    private final static short IFD_X_RESOLUTION = 282;
    private final static short IFD_Y_RESOLUTION = 283;
    private final static short IFD_RESOLUTION_UNIT = 296;

    // OPTIONAL IFD ENTRIES
    private final static short IFD_SOFTWARE = 305;
    private final static short IFD_DATE_TIME = 306;
    private final static short IFD_IMAGE_DESCRIPTION = 270;

    private final static byte[] SOFTWARE = "Put your software name here".getBytes();
    private final static byte[] IMAGE_DESCRIPTION = "Put an image description here".getBytes();

    private final static int HEADER_SIZE = ( 2 * 2 ) + ( 4 * 1 );
    private final static int IFD_SIZE = 2 + 12 * IFD_ENTRY_COUNT + 4;
    private final static int LONG_VARIABLES_SIZE = 6 + 8 + 8 + 20 + SOFTWARE.length + 1 + IMAGE_DESCRIPTION.length + 1;

    // IFD OFFSETS
    private final static int BITS_PER_SAMPLE_OFFSET = 206;
    private final static int STRIPS_OFFSETS = 248 + SOFTWARE.length + 1 + IMAGE_DESCRIPTION.length + 1;
    private final static int X_RESOLUTION_OFFSET = 212;
    private final static int Y_RESOLUTION_OFFSET = 220;
    private final static int SOFTWARE_OFFSET = 248;
    private final static int IMAGE_DESCRIPTION_OFFSET = 248 + SOFTWARE.length + 1;

    // IFD VARIABLES' OFFSETS
    private final static int IMAGE_WIDTH_OFFSET = 30;
    private final static int IMAGE_HEIGHT_OFFSET = 42;
    private final static int ROWS_PER_STRIP_OFFSET = 114;
    private final static int STRIP_BYTE_COUNTS_OFFSET = 126;
    private final static int DATE_TIME_OFFSET = 228;

    /** This ByteBuffer will hold the TIFF header, the IFD and some of the other metadata (>4 bytes values) */
    private final static ByteBuffer metadata = ByteBuffer.allocate(HEADER_SIZE + IFD_SIZE + LONG_VARIABLES_SIZE).order(ByteOrder.nativeOrder());

    /** A Calendar instance that gets update on every capture */
    private final static Calendar calendar = Calendar.getInstance();

    /** A buffer to hold the formatted current date & time */
    private final static byte[] dateTime = new byte[20];

    static {
        // INITIALISE METADATA BUFFER
        metadata.put(HEADER_BYTEORDER);
        metadata.putShort(HEADER_TIFF_MAGIC_NUMBER);
        metadata.putInt(HEADER_FIRST_IFD_OFFSET);

        metadata.putShort(IFD_ENTRY_COUNT);

        metadata.putShort(IFD_NEW_SUBFILE_TYPE);
        metadata.putShort(LONG);
        metadata.putInt(1);
        metadata.putInt(0);

        metadata.putShort(IFD_IMAGE_WIDTH);
        metadata.putShort(LONG);
        metadata.putInt(1);
        metadata.putInt(0); // PUT IMAGE WIDTH HERE

        metadata.putShort(IFD_IMAGE_LENGTH);
        metadata.putShort(LONG);
        metadata.putInt(1);
        metadata.putInt(0); // PUT IMAGE LENGTH HERE

        metadata.putShort(IFD_BITS_PER_SAMPLE);
        metadata.putShort(SHORT);
        metadata.putInt(3);
        metadata.putInt(BITS_PER_SAMPLE_OFFSET);

        metadata.putShort(IFD_COMPRESSION);
        metadata.putShort(SHORT);
        metadata.putInt(1);
        metadata.putInt(1);

        metadata.putShort(IFD_PHOTOMETRIC_INTERPOLATION);
        metadata.putShort(SHORT);
        metadata.putInt(1);
        metadata.putInt(2);

        metadata.putShort(IFD_STRIP_OFFSETS);
        metadata.putShort(LONG);
        metadata.putInt(1);
        metadata.putInt(STRIPS_OFFSETS);

        metadata.putShort(IFD_SAMPLES_PER_PIXEL);
        metadata.putShort(SHORT);
        metadata.putInt(1);
        metadata.putInt(3);

        metadata.putShort(IFD_ROWS_PER_STRIP);
        metadata.putShort(SHORT);
        metadata.putInt(1);
        metadata.putInt(0); // PUT ROWS PER STRIP HERE

        metadata.putShort(IFD_STRIP_BYTE_COUNTS);
        metadata.putShort(LONG);
        metadata.putInt(1);
        metadata.putInt(0); // PUT STRIP BYTE COUNTS HERE

        metadata.putShort(IFD_RESOLUTION_UNIT);
        metadata.putShort(SHORT);
        metadata.putInt(1);
        metadata.putInt(1);

        metadata.putShort(IFD_X_RESOLUTION);
        metadata.putShort(RATIONAL);
        metadata.putInt(1);
        metadata.putInt(X_RESOLUTION_OFFSET);

        metadata.putShort(IFD_Y_RESOLUTION);
        metadata.putShort(RATIONAL);
        metadata.putInt(1);
        metadata.putInt(Y_RESOLUTION_OFFSET);

        metadata.putShort(IFD_DATE_TIME);
        metadata.putShort(ASCII);
        metadata.putInt(20);
        metadata.putInt(DATE_TIME_OFFSET);

        metadata.putShort(IFD_SOFTWARE);
        metadata.putShort(ASCII);
        metadata.putInt(SOFTWARE.length + 1);
        metadata.putInt(SOFTWARE_OFFSET);

        metadata.putShort(IFD_IMAGE_DESCRIPTION);
        metadata.putShort(ASCII);
        metadata.putInt(IMAGE_DESCRIPTION.length + 1);
        metadata.putInt(IMAGE_DESCRIPTION_OFFSET);

        // We have only one IFD
        metadata.putInt(0);

        // Bits per Sample
        metadata.putShort((short)8).putShort((short)8).putShort((short)8);

        // X,Y Resolution
        metadata.putLong(1).putLong(1);

        // Skip DATE-TIME
        metadata.position(metadata.position() + 20);

        metadata.put(SOFTWARE);
        metadata.put((byte)0);
        metadata.put(IMAGE_DESCRIPTION);
        metadata.put((byte)0);
    }

    /**
     * Writes an image to a TIFF image format file.
     * @param w the image width
     * @param h the image height
     * @param pixels the image pixel data
     * @param output the file to write the image to
     * @throws IOException If an IO error occurs
     */
    public final static void writeImage(int w, int h, byte[] pixels, File output) throws IOException {
        FileOutputStream fos = new FileOutputStream(output);

        metadata.putInt(IMAGE_WIDTH_OFFSET, w);
        metadata.putInt(IMAGE_HEIGHT_OFFSET, h);
        metadata.putInt(ROWS_PER_STRIP_OFFSET, h);
        metadata.putInt(STRIP_BYTE_COUNTS_OFFSET, w * h * 3);

        updateTime();
        metadata.position(DATE_TIME_OFFSET);
        metadata.put(dateTime);

        fos.write(metadata.array());
        fos.write(pixels);

        fos.close();
    }

    /**
     * Updates the calendar and puts the current
     * date and time to a buffer, specially formatted
     * to the TIFF Date & Time format.
     * Example: "2003:04:02 18:30:15\0"
     */
    private final static void updateTime() {
        // Reset current date and time
        for ( int i = 0; i < dateTime.length; i++ )
            dateTime[i] = 0;

        calendar.setTimeInMillis(System.currentTimeMillis());

        int value = calendar.get(Calendar.YEAR);
        dateTime[0] = (byte)( '0' + ( value / 1000 ) );
        dateTime[1] = (byte)( '0' + ( ( value % 1000 ) / 100 ) );
        dateTime[2] = (byte)( '0' + ( ( value % 100 ) / 10 ) );
        dateTime[3] = (byte)( '0' + ( value % 10 ) );

        dateTime[4] = ':';

        value = calendar.get(Calendar.MONTH);
        dateTime[5] = (byte)( '0' + ( value / 10 ) );
        dateTime[6] = (byte)( '0' + ( value % 10 ) );

        dateTime[7] = ':';

        value = calendar.get(Calendar.DAY_OF_MONTH);
        dateTime[8] = (byte)( '0' + ( value / 10 ) );
        dateTime[9] = (byte)( '0' + ( value % 10 ) );

        dateTime[10] = ' ';

        value = calendar.get(Calendar.HOUR_OF_DAY);
        dateTime[11] = (byte)( '0' + ( value / 10 ) );
        dateTime[12] = (byte)( '0' + ( value % 10 ) );

        dateTime[13] = ':';

        value = calendar.get(Calendar.MINUTE);
        dateTime[14] = (byte)( '0' + ( value / 10 ) );
        dateTime[15] = (byte)( '0' + ( value % 10 ) );

        dateTime[16] = ':';

        value = calendar.get(Calendar.MINUTE);
        dateTime[17] = (byte)( '0' + ( value / 10 ) );
        dateTime[18] = (byte)( '0' + ( value % 10 ) );
    }
}

