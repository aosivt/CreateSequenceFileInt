//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ivt.aos;

import ivt.aos.Main;
import ivt.aos.DevImage.CreateHeaderForENVI;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.SequenceFile.Writer.Option;

public class CreateSeqFromLargeTiff {
    public CreateSeqFromLargeTiff() {
    }

    public void funcCreateSeqFromTiff() throws IOException {
        Date StartPro = new Date(System.currentTimeMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-mm-dd\'T\'HH:mm:ss");
        byte band_number = 0;
        File TiffPath = new File(Main.mainpath + "inputfile");
        File OutPutPath = new File(Main.mainpath + "outputfile");
        File[] outlistFile = OutPutPath.listFiles();
        File[] listFile = TiffPath.listFiles();
        System.out.println("Количество файлов в дирректории:" + listFile.length);

        for(int i = 0; i < listFile.length; ++i) {
            if(listFile[i].getName().indexOf("B1") > 0) {
                band_number = 1;
            } else if(listFile[i].getName().indexOf("B2") > 0) {
                band_number = 2;
            } else if(listFile[i].getName().indexOf("B3") > 0) {
                band_number = 3;
            } else if(listFile[i].getName().indexOf("B4") > 0) {
                band_number = 4;
            } else if(listFile[i].getName().indexOf("B5") > 0) {
                band_number = 5;
            } else if(listFile[i].getName().indexOf("B6") > 0) {
                band_number = 6;
            } else if(listFile[i].getName().indexOf("B7") > 0) {
                band_number = 7;
            } else if(listFile[i].getName().indexOf("B8") > 0) {
                band_number = 8;
            }

            FileInputStream is = null;
            Rectangle sourceRegion = null;

            try {
                is = new FileInputStream(Main.mainpath + "inputfile/" + listFile[i].getName());
                ImageInputStream e = ImageIO.createImageInputStream(is);
                Iterator readers = ImageIO.getImageReaders(e);
                if(readers.hasNext()) {
                    ImageReader reader = (ImageReader)readers.next();
                    reader.setInput(e);
                    int h = reader.getHeight(reader.getMinIndex());
                    int w = reader.getWidth(reader.getMinIndex());
                    PrintWriter createrheader = new PrintWriter(Main.mainpath + "outputfile/" + listFile[i].getName() + ".hdr", "UTF-8");
                    createrheader.println((new CreateHeaderForENVI()).getInformationForEnviHeader(w, h));
                    createrheader.close();
                    ImageReadParam param = reader.getDefaultReadParam();
                    BufferedImage image = null;
                    Path outPath = new Path(Main.mainpath + "outputfile/" + listFile[i].getName() + ".hsf");
                    Configuration confHadoop = new Configuration();
                    Writer writer = null;
                    Option optPath = Writer.file(outPath);
                    Option optKey = Writer.keyClass(IntWritable.class);
                    Option optVal = Writer.valueClass(BytesWritable.class);
                    Option optCom = Writer.compression(CompressionType.NONE);
                    writer = SequenceFile.createWriter(confHadoop, new Option[]{optPath, optKey, optVal, optCom});

                    try {
                        for(int e1 = 0; e1 < h; ++e1) {
                            sourceRegion = new Rectangle(0, e1, w, 1);
                            param.setSourceRegion(sourceRegion);
                            image = reader.read(0, param);
                            byte[] array_value_satelite = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
                            byte[] array_configuration = new byte[]{(byte)0, (byte)0, (byte)0, band_number};
                            byte[] sum_array = ArrayUtils.addAll(array_configuration, array_value_satelite);
                            Object var37 = null;
                            Object var38 = null;
                            sourceRegion = null;
                            image = null;
                            writer.append(new IntWritable(e1 + 1), new BytesWritable(sum_array));
                            Object var39 = null;
                        }
                    } catch (IOException var34) {
                        var34.printStackTrace();
                    } finally {
                        IOUtils.closeStream(writer);
                        System.out.println("Начало созданя файла последовательности: " + timeFormat.format(StartPro));
                        System.out.println("Конец созданя файла последовательности: " + timeFormat.format(new Date(System.currentTimeMillis())));
                    }
                }
            } catch (FileNotFoundException var36) {
                var36.printStackTrace();
            }
        }

    }
}
