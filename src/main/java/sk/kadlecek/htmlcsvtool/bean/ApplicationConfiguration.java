package sk.kadlecek.htmlcsvtool.bean;

public class ApplicationConfiguration {

    private final Integer  maxConcurrentThreads;
    private final Integer maxFilesToProcessPerThread;

    public ApplicationConfiguration(Integer maxConcurrentThreads, Integer maxFilesToProcessPerThread) {
        this.maxConcurrentThreads = maxConcurrentThreads;
        this.maxFilesToProcessPerThread = maxFilesToProcessPerThread;
    }

    public Integer getMaxConcurrentThreads() {
        return maxConcurrentThreads;
    }

    public Integer getMaxFilesToProcessPerThread() {
        return maxFilesToProcessPerThread;
    }
}
