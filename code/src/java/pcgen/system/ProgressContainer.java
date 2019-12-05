package pcgen.system;

public interface ProgressContainer
{
    int getMaximum();

    int getProgress();

    String getMessage();

    void setValues(int progress, int maximum);

    void setValues(String message, int progress, int maximum);

    void setProgress(int progress);

    void setProgress(String message, int progress);

    void setMaximum(int maximum);

    void fireProgressChangedEvent();
}
