package ru.turikhay.util.async;

import ru.turikhay.util.U;

public abstract class ExtendedThread extends Thread {
   private static int threadNum;
   private final ExtendedThread.ExtendedThreadCaller caller;
   private String blockReason;

   public ExtendedThread(String name) {
      super(name + "#" + threadNum++);
      this.caller = new ExtendedThread.ExtendedThreadCaller((ExtendedThread.ExtendedThreadCaller)null);
   }

   public ExtendedThread() {
      this("ExtendedThread");
   }

   public ExtendedThread.ExtendedThreadCaller getCaller() {
      return this.caller;
   }

   public void startAndWait() {
      super.start();

      while(!this.isThreadBlocked()) {
         U.sleepFor(100L);
      }

   }

   public abstract void run();

   protected synchronized void blockThread(String reason) {
      if (reason == null) {
         throw new NullPointerException();
      } else {
         this.checkCurrent();
         this.blockReason = reason;
         this.threadLog("Thread locked by:", this.blockReason);

         while(this.blockReason != null) {
            try {
               this.wait();
            } catch (InterruptedException var3) {
               return;
            }
         }

         this.threadLog("Thread has been unlocked");
      }
   }

   public synchronized void unblockThread(String reason) {
      if (reason == null) {
         throw new NullPointerException();
      } else if (!reason.equals(this.blockReason)) {
         throw new IllegalStateException("Unlocking denied! Locked with: " + this.blockReason + ", tried to unlock with: " + reason);
      } else {
         this.blockReason = null;
         this.notifyAll();
      }
   }

   public boolean isThreadBlocked() {
      return this.blockReason != null;
   }

   public boolean isCurrent() {
      return Thread.currentThread().equals(this);
   }

   protected void checkCurrent() {
      if (!this.isCurrent()) {
         throw new IllegalStateException("Illegal thread!");
      }
   }

   protected void threadLog(Object... o) {
      U.log("[" + this.getName() + "]", o);
   }

   public class ExtendedThreadCaller extends RuntimeException {
      private static final long serialVersionUID = -9184403765829112550L;

      private ExtendedThreadCaller() {
      }

      // $FF: synthetic method
      ExtendedThreadCaller(ExtendedThread.ExtendedThreadCaller var2) {
         this();
      }
   }
}
