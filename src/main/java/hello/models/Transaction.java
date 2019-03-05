package hello.models;

public class Transaction {
    private String id;
    private String status;
    private long timeoutSeconds = 60;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the timeoutSeconds
     */
    public long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * @param timeoutSeconds the timeoutSeconds to set
     */
    public void setTimeoutSeconds(long timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
