package com.example.nuvo.mbluetoothmultipledevices.model;

public class VersionSendMessage extends SendMessage {

    public void setStart() {
        super.setStart();

        bytes.add((byte) 0x01);
        bytes.add((byte) 0x00);
        bytes.add((byte) 0x00);
        bytes.add((byte) 0x00);
    }
}
