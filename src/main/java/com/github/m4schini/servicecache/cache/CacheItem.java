package com.github.m4schini.servicecache.cache;

import java.io.*;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class CacheItem implements Serializable {
    @Serial
    private final static long serialVersionUID = 1;
    private final static int MAX_AGE = 1;
    private final static int TIMEUNIT = Calendar.MINUTE;

    private final Date timestamp;
    private final String value;
    private final boolean empty;

    public CacheItem(String value) {
        this.empty = false;
        this.value = value;
        this.timestamp = new Date();
    }

    public CacheItem() {
        this.empty = true;
        this.value = null;
        this.timestamp = null;
    }

    public <T> T getValue() {
        return (T) CacheItem.deserialize(value);
    }

    public Date getExpirationDate() {
        if (timestamp == null) throw new UnsupportedOperationException("CacheItem is empty");

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTime(timestamp);
        expirationDate.add(TIMEUNIT, MAX_AGE);
        return expirationDate.getTime();
    }

    public boolean isExpired() {
        return isExpired(MAX_AGE, TIMEUNIT);
    }

    public boolean isExpired(int maxAge) {
        return isExpired(maxAge, Calendar.SECOND);
    }

    private boolean isExpired(int maxAge, int timeunit) {
        if (timestamp == null) throw new UnsupportedOperationException("CacheItem is empty");

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTime(timestamp);
        expirationDate.add(timeunit, maxAge);
        var now = new Date();
        return expirationDate.getTime().getTime() < now.getTime(); // .before didn't work because of unknown reasons
    }

    public boolean isCached() {
        return !empty;
    }


    /**
     * Read the object from Base64 string.
     * @see <a href="https://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string">Source</a>
     */
    public static Object deserialize(String s ) {
        try {
            if (s == null) return null;
            byte [] data = Base64.getDecoder().decode( s );
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream( data ) );
            Object o  = ois.readObject();
            ois.close();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Write the object to a Base64 string.
     * @see <a href="https://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string">Source</a>
     */
    public static String serialize( Serializable o ) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( o );
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }
}
