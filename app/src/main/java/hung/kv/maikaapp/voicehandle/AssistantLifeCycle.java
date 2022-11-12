package hung.kv.maikaapp.voicehandle;

public interface AssistantLifeCycle {
    void onDetectKeyword();
    void onCommand(String commandContent);
    void onResponse();
    void start();
    void finish();
}
