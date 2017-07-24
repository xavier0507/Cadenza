package xy.hippocampus.cadenza.controller.fragment.base;

/**
 * Created by Xavier Yin on 2017/7/21.
 */

public interface INotifyProgress {
    void notifyProgressAppear(boolean isBlocked);

    void notifyProgressDisappear();
}
