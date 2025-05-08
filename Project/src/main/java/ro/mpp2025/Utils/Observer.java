package ro.mpp2025.Utils;

public interface Observer {
    void notification();
    void addSubject(Subject subject);
}
