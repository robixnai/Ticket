package br.com.cast.ticket.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import br.com.cast.ticket.R;
import br.com.cast.ticket.app.TicketApplication;

/**
 * @author Ezequiel Messore.
 */
public enum TicketType {
    BUS(1, R.string.enum_bus), SUBWAY(2, R.string.enum_subway);

    private static final Map<Integer,TicketType> sLookup = new HashMap<Integer,TicketType>();

    static {
        for(TicketType menu : EnumSet.allOf(TicketType.class)) {
            sLookup.put(menu.getId(), menu);
        }
    }

    private int mId;
    private int mDescriptionRes;

    TicketType(final int id, final int descriptionRes) {
        mId = id;
        mDescriptionRes = descriptionRes;
    }

    public int getId() {
        return mId;
    }

    @Override
    public String toString() {
        return TicketApplication.getContext().getString(mDescriptionRes);
    }

    public static TicketType valueOf(int id) {
        if (sLookup.containsKey(id)) {
            return sLookup.get(id);
        } else {
            return null;
        }
    }

    public static TicketType getInstance(String name) {
        for (TicketType type : values()) {
            if (TicketApplication.getContext().getString(type.mDescriptionRes).equals(name)) {
                return type;
            }
        }
        return null;
    }
}
