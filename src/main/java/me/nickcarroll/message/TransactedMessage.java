package me.nickcarroll.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TransactedMessage {

    private final int id;
    private final long sentTimestamp;
    private final long receivedTimestamp;

    public TransactedMessage(int id, long sentTimestamp, long receivedTimestamp) {
        this.id = id;
        this.sentTimestamp = sentTimestamp;
        this.receivedTimestamp = receivedTimestamp;
    }

    public int getMessageId() {
        return id;
    }

    public long getSentTimestamp() {
        return sentTimestamp;
    }

    public long getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public long getRoundTripTimeInNanos() {
        return receivedTimestamp - sentTimestamp;
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append(id);
        toStringBuilder.append(sentTimestamp);
        toStringBuilder.append(receivedTimestamp);
        return toStringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TransactedMessage) {
            TransactedMessage other = (TransactedMessage) obj;
            return this.id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
