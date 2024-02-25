package app.lawnchair.compatlib;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Handler;
import android.view.RemoteAnimationAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ActivityOptionsCompat {

    @NonNull
    public abstract ActivityOptions makeCustomAnimation(
            @NonNull Context context,
            int enterResId,
            int exitResId,
            @NonNull final Handler callbackHandler,
            @Nullable final Runnable startedListener,
            @Nullable final Runnable finishedListener);

    @NonNull
    public abstract ActivityOptions makeRemoteAnimation(
            @Nullable RemoteAnimationAdapter remoteAnimationAdapter,
            @Nullable Object remoteTransition,
            @Nullable String debugName);
}
