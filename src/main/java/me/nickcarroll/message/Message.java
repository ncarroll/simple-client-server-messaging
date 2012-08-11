package me.nickcarroll.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A domain object for storing information in a message that can be sent to the Server and back again.
 * Able to serialize the object to and from XML.  NOTE: This is a simple XML conversion approach that I would not
 * specifically use in a production environment.  It is just simple to implement using regular expressions, and
 * a template.
 *
 * The message stores an ID and the sent timestamp so that no state needs to be persisted in order to measure the
 * round trip time and average round trip time of messages.
 */
public class Message {

    private static final String MESSAGE_TEMPLATE = "<message id=\"%d\" sentTimestamp=\"%d\"/>";
    private static final String MESSAGE_PATTERN = "<message id=\"(\\d*)\" sentTimestamp=\"(\\d*)\"/>";
    private static final int ID_GROUP = 1;
    private static final int SENT_TIMESTAMP_GROUP = 2;

    private final int id;
    private final long sentTimestamp;

    public Message(int id, long sentTimestamp) {
        this.id = id;
        this.sentTimestamp = sentTimestamp;
    }

    public int getId() {
        return id;
    }

    public long getSentTimestamp() {
        return sentTimestamp;
    }

    public String getAsXML() {
        return String.format(MESSAGE_TEMPLATE, id, sentTimestamp);
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        toStringBuilder.append(id);
        toStringBuilder.append(sentTimestamp);
        return toStringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Message) {
            Message other = (Message) obj;
            return this.id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * Simple method to convert an XML message into a Message.
     * Would probably use XPath and assert that the message conforms to a DTD first.  However this
     * implementation makes a simplifying assumption that a regular expression should be good
     * enough for this particular coding challenge.
     *
     * @param xml Message formatted as XML.
     * @return A message using the ID and sent timestamp extracted from xml.
     */
    public static Message fromXML(String xml) {
        Pattern message = Pattern.compile(MESSAGE_PATTERN);
        Matcher match = message.matcher(xml);
        if (match.matches()) {
            String id = match.group(ID_GROUP);
            String sentTimestampMillis = match.group(SENT_TIMESTAMP_GROUP);
            return new Message(Integer.valueOf(id), Long.valueOf(sentTimestampMillis));
        }
        throw new RuntimeException("Unable to parse message: " + xml);
    }
}
