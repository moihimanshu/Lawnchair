package app.lawnchair.compatlib;

import android.app.IApplicationThread;
import android.window.IRemoteTransition;
import android.window.RemoteTransition;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class RemoteTransitionCompat {

    public abstract RemoteTransition getRemoteTransition(
            @NonNull IRemoteTransition remoteTransition,
            @Nullable IApplicationThread appThread,
            @Nullable String debugName);
}
