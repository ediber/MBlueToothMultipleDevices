package com.example.nuvo.mbluetoothmultipledevices.model;

import com.example.nuvo.mbluetoothmultipledevices.utils.ConvertUtil;

import java.util.List;


public class ReceiveMessage {



    public enum MessageType {
        LOD,
        VERSION;
    }

    protected List<Integer> mLst;
    protected int payLoadPosition = 4;
    private MessageType mType;

    public ReceiveMessage(List<Integer> lst, MessageType type) {
        mLst = lst;
        mType = type;
    }

    public String getPayloadInBinary() {
        String payload = "";

        for (int i = 5; i < mLst.size() - 2; i++) {
            int binaryIndex = mLst.get(i) & 0xFF;
            String binaryStr = ConvertUtil.decimalToBinary(binaryIndex);
            payload = payload + binaryStr;
        }
        return payload;
    }

    public String toHexa() {
        return ConvertUtil.decimalToHexString(mLst);
    }

    public MessageType getmType() {
        return mType;
    }
}
