package com.example.nuvo.mbluetoothmultipledevices.model;


import com.example.nuvo.mbluetoothmultipledevices.utils.ConvertUtil;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class SendMessage {
    protected ArrayList<Byte> bytes;
    protected int payLoadPosition = 4;
    private int payloadCount = 0;


//    public abstract int getSize();

    public byte[] toBytes() {
        return ConvertUtil.ByteListToArray(bytes);
    }

    public void addPayload(int num) {
        bytes.add(payLoadPosition, ConvertUtil.intToHexByte(num));
        payLoadPosition ++;
        payloadCount ++;
        setPayloadCount(payloadCount);
    }

    private void setPayloadCount(int count) {
        Byte countByte = ConvertUtil.intToHexByte(count);
        bytes.set(2, countByte);
    }

    protected void setStart() {
        bytes = new ArrayList<>(Arrays.asList(
                (byte) 0xDA,
                (byte) 0xDE,
                (byte) 0x00
        ));
    }

    protected void setStop() {
        bytes = new ArrayList<>(Arrays.asList(
                (byte) 0xDA,
                (byte) 0xDE,
                (byte) 0x00
        ));
    }
}
