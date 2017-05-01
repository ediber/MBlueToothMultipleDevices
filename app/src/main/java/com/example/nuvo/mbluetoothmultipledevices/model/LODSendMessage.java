package com.example.nuvo.mbluetoothmultipledevices.model;

public class LODSendMessage extends SendMessage {


    public void setStart() {
        super.setStart();

        bytes.add((byte) 0x21);
        bytes.add((byte) 0x00);
        bytes.add((byte) 0x00);
        bytes.add((byte) 0x00);
    }

    public void setStop() {
        super.setStart();

        bytes.add((byte) 0x22);
        bytes.add((byte) 0x00);
        bytes.add((byte) 0x00);
        bytes.add((byte) 0x00);
    }

}
