package ivt.aos.ForFiles;
import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xandr on 09.05.16.
 */
public class SortBy {
    public List fileList;

    public File[] sortByName() {

        ArrayList res = new ArrayList(fileList.size());
        //копируем список
        res.addAll(fileList);
        //выполняем сортировку
        Collections.sort(res);
        //возвращаем результат
        File[] f  = new File[res.size()];
        res.toArray(f);
        return f;

    }
    public List sortBySize(List fileList) {

        ArrayList res = new ArrayList(fileList.size());
        //копируем список
        res.addAll(fileList);
        //выполняем сортировку
        Collections.sort(res);
        //возвращаем результат
        return res;

    }

}
