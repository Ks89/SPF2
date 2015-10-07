package it.polimi.spf.wfd.events;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Extends Otto Bus to extend it with the ability to post on the main thread
 */
public class NineBus extends Bus {

  private final Handler mainThread = new Handler(Looper.getMainLooper());
  private static NineBus bus;

  private NineBus(ThreadEnforcer t) {
    super(t);
  }

  public synchronized static Bus get() {
    if (bus == null) {
      bus = new NineBus(ThreadEnforcer.ANY);
    }
    return bus;
  }

  @Override public void post(final Object event) {
    Log.d(event.getClass().getSimpleName(), event.toString());

    if (Looper.myLooper() == Looper.getMainLooper()) {
      super.post(event);
    } else {
      mainThread.post(new Runnable() {
        @Override public void run() {
          NineBus.super.post(event);
        }
      });
    }
  }
}
