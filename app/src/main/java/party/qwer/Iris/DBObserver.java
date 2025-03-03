package party.qwer.Iris;

public class DBObserver {
    private final KakaoDB kakaoDb;
    private final ObserverHelper observerHelper;
    private Thread pollingThread;

    public DBObserver(KakaoDB kakaoDb, ObserverHelper observerHelper) {
        this.kakaoDb = kakaoDb;
        this.observerHelper = observerHelper;
    }

    public void startPolling() {
        if (pollingThread == null || !pollingThread.isAlive()) {
            pollingThread = new Thread(() -> {
                while (true) {
                    observerHelper.checkChange(kakaoDb);
                    try {
                        long pollingInterval = Configurable.getInstance().getDbPollingRate();
                        Thread.sleep(pollingInterval);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Polling thread interrupted: " + e);
                        break;
                    }
                }
            });
            pollingThread.setName("DB-Polling-Thread");
            pollingThread.start();
            System.out.println("DB Polling thread started.");
        } else {
            System.out.println("DB Polling thread is already running.");
        }
    }

    public void stopPolling() {
        if (pollingThread != null && pollingThread.isAlive()) {
            pollingThread.interrupt();
            pollingThread = null;
            System.out.println("DB Polling thread stopped.");
        }
    }
}