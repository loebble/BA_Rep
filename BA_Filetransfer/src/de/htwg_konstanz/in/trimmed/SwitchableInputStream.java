package de.htwg_konstanz.in.trimmed;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ellen Wieland
 * 
 */
public class SwitchableInputStream extends InputStream {

	public InputStream inputStream;
	private BlockingQueue<InputStream> newInputStreams;

	private int numberOfBytesReceived;
	private int numberOfBytesToRead;
	public boolean readyToBeClosed = false;

	private boolean isSwitchException = false;

	public SwitchableInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		this.newInputStreams = new LinkedBlockingQueue<InputStream>();
		this.numberOfBytesToRead = 0;
		this.numberOfBytesReceived = 0;
	}

	public void switchInputStream(InputStream inputStream) {
		this.newInputStreams.add(inputStream);
	}

	/**
	 * @param numberOfBytesToRead
	 *            the numberOfBytesToRead to set
	 */
	public void setNumberOfBytesToRead(int numberOfBytesToRead) {
		System.out.println("setNumberofBytestoRead to : " + numberOfBytesToRead);
		this.numberOfBytesToRead = numberOfBytesToRead;
	}

	// delegate work to internal input stream

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		inputStream.mark(readlimit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#markSupported()
	 */
	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int data = 0;
		try {
			if (this.numberOfBytesToRead != 0) {
				System.out
						.println("numberOfBytesToRead > numberOfBytesReceived ? :"
								+ numberOfBytesToRead
								+ " / "
								+ numberOfBytesReceived);
				// just switch the stream, because there are no Bytes to Read
				if (numberOfBytesToRead <= 0) {
					internStreamSwitch();
					return this.read(b, off, len);
					// are there Bytes to read Left?
				} else if (numberOfBytesToRead > numberOfBytesReceived) {
					int bytesToReadLeft = numberOfBytesToRead
							- numberOfBytesReceived;
					// are there more bytes left that the array can handle?
					// if so don't switch the stream.
					if (bytesToReadLeft > b.length) {
						data = this.inputStream.read(b, 0, b.length);
						numberOfBytesReceived += data;
						return data;
					} else {
						data = this.inputStream.read(b, 0, bytesToReadLeft);
						//has there been an end of file its because of socketclosing on the other side
						if (data == -1) {
							System.out.println("bin drin");
							internStreamSwitch();
							return this.read(b, off, len);
						}
						// only switch the stream
						// if all the bytes which are left has been read
						if (data == bytesToReadLeft) {
							System.out.println(bytesToReadLeft
									+ " bytes read over old connection!");
							System.out.println("switching the Stream!!!!");
							// then switch it
							internStreamSwitch();
						} else {
							System.out.println("awkward!!!!!!!!!!");
							numberOfBytesReceived += data;
							return data;
						}
						numberOfBytesReceived += data;
						return data;
					}
				} else if (numberOfBytesToRead == numberOfBytesReceived) {
					internStreamSwitch();
					return this.read(b);
				}

			} else
			data = inputStream.read(b, off, len);


		} catch (SocketException e) {
			if (isSwitchException) {
				try {
					internStreamSwitch();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.err
						.println("InputstreamException because of switching!!!");
				return this.read(b, off, len);
				// Do Nothing
			} else {
				// e.printStackTrace();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read(byte[])
	 */
	@Override
	public int read(byte[] b) {
		int data = 0;
		try {
			if (numberOfBytesToRead != 0) {
				System.out
						.println("numberOfBytesToRead > numberOfBytesReceived ? :"
								+ numberOfBytesToRead
								+ " / "
								+ numberOfBytesReceived);
				// just switch the stream, because there are no Bytes to Read
				if (numberOfBytesToRead <= 0) {
					internStreamSwitch();
					return this.read(b);
					// are there Bytes to read Left?
				} else if (numberOfBytesToRead > numberOfBytesReceived) {
					int bytesToReadLeft = numberOfBytesToRead
							- numberOfBytesReceived;
					// are there more bytes left that the array can handle?
					// if so don't switch the stream.
					if (bytesToReadLeft > b.length) {
						data = this.inputStream.read(b, 0, b.length);
						numberOfBytesReceived += data;
						return data;
					} else {
						data = this.inputStream.read(b, 0, bytesToReadLeft);
						//has there been an end of file its because of socketclosing on the other side
						if (data == -1) {
							System.out.println("bin drin");
							internStreamSwitch();
							return this.read(b);
						}
						// only switch the stream
						// if all the bytes which are left has been read
						if (data == bytesToReadLeft) {
							System.out.println( " last "+bytesToReadLeft
									+"bytes read over old connection!");
							System.out.println("switching the Stream!!!!");
							// then switch it
							internStreamSwitch();
							return data;
						} else {
							System.out.println("awkward!!!!!!!!!!");
							numberOfBytesReceived += data;
							System.out.println("new numberOfBytesReceived : "+ numberOfBytesReceived);
							return data;
						}
					}
				} else if (numberOfBytesToRead == numberOfBytesReceived) {
					internStreamSwitch();
					return this.read(b);
				}

			} else
			data = inputStream.read(b);


		} catch (SocketException e) {
			if (isSwitchException) {
				try {
					internStreamSwitch();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.err
						.println("InputstreamException because of switching!!!");
				return this.read(b);
				// Do Nothing
			} else {
				// e.printStackTrace();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}

		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int data = 0;
		try {
			System.out.println("Someone called read()!!!");
			data = inputStream.read();
			System.out.println("DATA recieved: " + data);
		} catch (SocketException e) {
			if (isSwitchException) {
				System.err
						.println("InputstreamException because of switching!!!");
				System.out.println("switching the Stream!!!!");
				try {
					internStreamSwitch();
					return this.read();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// Do Nothing
			} else {
				// e.printStackTrace();
			}
		}
		if (data != -1) {
			numberOfBytesReceived += data;
		} else {
			System.out.println("-------------------1 ");
		}

		return data;

	}

	public void internStreamSwitch() throws InterruptedException {
		System.out.println("trying to switch...");
		synchronized (this) {
			this.notify();
			this.wait();
		}
		inputStream = newInputStreams.take();
		System.out.println("take hat geklappt!");
		this.numberOfBytesReceived = 0;
		this.numberOfBytesToRead = 0;
//		System.out.println("here comes the switch synchronized");
//		synchronized (this.inputStream) {
//			System.out.println("im SInputStream Block!");
//			this.inputStream.notify();
//			System.out.println("Aus dem SInputStream Block!");
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		inputStream.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}

	public void setSwitchException(boolean isSwitchException) {
		this.isSwitchException = isSwitchException;
	}

	public boolean isSwitchException() {
		return isSwitchException;
	}
}
