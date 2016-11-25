package ivt.aos.DevImage;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xandr on 19.05.16.
 */
public class CreateHeaderForENVI {
    public String getInformationForEnviHeader(int samples_count_columns, int lines_count_row )
    {
        Date StartPro = new Date(System.currentTimeMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");
        String dataCreation = timeFormat.format(StartPro).toString();

        String result_string
                      = "ENVI " + "\n" +
                        "description = { NDVI Transform Result ["+dataCreation+ "]} " + "\n" +
                        "samples = " + samples_count_columns + "\n" +
                        "lines   =  " + lines_count_row + "\n" +
                        "bands   = 1 " + "\n" +
                        "header offset = 0 " + "\n" +
                        "file type = ENVI Standard" + "\n" +
                        "data type = 1" + "\n" +
                        "interleave = bsq" + "\n" +
                        "sensor type = Unknown" + "\n" +
                        "byte order = 0" ;


        return result_string;
    }
}
