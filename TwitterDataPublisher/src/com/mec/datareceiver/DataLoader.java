package com.mec.datareceiver;

import javax.jms.Destination;

public interface DataLoader {
	void connect();
	void disconnect();
	boolean isConnected();
	int getNumOfMessagesProcessed();
}
