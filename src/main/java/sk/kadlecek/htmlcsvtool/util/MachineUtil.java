package sk.kadlecek.htmlcsvtool.util;

public class MachineUtil {

    public static int getNumberOfCpuCores() {
        return Runtime.getRuntime().availableProcessors();
    }

}
