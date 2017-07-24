package xy.hippocampus.cadenza.controller.ytapi.base;

/**
 * Created by Xavier Yin on 6/15/17.
 */

public interface IAsyncTaskActCallback<X, Y, Z> {
    void onActPreExecute();

    void onActProgressUpdate(Y... y);

    void onActPostExecute(Z z);

    void onActCancelled(Exception lastError);

    void onActCancelFinished();
}
