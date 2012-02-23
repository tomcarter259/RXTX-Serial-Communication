import java.io.*;
import java.util.Enumeration;
import gnu.io.*;

/**
 * Provides functionality for connecting to a serial port and transmitting bytes
 * across it.
 *
 * Requires RXTX to be installed!
 * Download from: http://rxtx.qbang.org
 *
 * @author Tom Carter (tom@tomcarter.org.uk)
 */
public class SerialComms {
	static CommPortIdentifier portID;
	static SerialPort         serialPort;
	static OutputStream       outputStream;
	static boolean            outputBufferEmptyFlag=false;

	/**
	 * Disconnects from the serial port.
	 *
	 */
	public void disconnect()
	{
		serialPort.close();
		System.out.println("[SerialComms] Port disconnected");
	}

	/**
	 * Connects to the serial port.
	 *
	 * @param portName The name of the port to connect to
	 * @return The success or failure of connecting
	 */
	public boolean connect(String portName)
	{
		boolean success = true;
		Enumeration portList;
		boolean portFound = false;

		portList = CommPortIdentifier.getPortIdentifiers();

		System.out.println("[SerialComms] Connecting to: " + portName);

		while (portList.hasMoreElements() && !portFound)
		{
			portID = (CommPortIdentifier) portList.nextElement();

			if (portID.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				System.out.println("[SerialComms] -- Trying " + portID.getName());
				if (portID.getName().equals(portName))
				{
					System.out.println("[SerialComms] -- Found port.");

					portFound = true;
					try
					{
						serialPort = (SerialPort) portID.open(
								"SimpleWrite", 2000);
					} catch (PortInUseException e)
					{
						System.out.println("[SerialComms] -- ERROR: port in use");
						success = false;
						continue;
					}

					try
					{
						outputStream = serialPort.getOutputStream();
					} catch (IOException e)
					{
						System.out.println(
								"[SerialComms] -- ERROR: failed to get output stream");
						success = false;
					}

					try
					{
						serialPort.setSerialPortParams(
								115200,
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
					} catch (UnsupportedCommOperationException e)
					{
						System.out.println(
								"[SerialComms] -- ERROR: failed to set port parameters");
						success = false;
					}

					try
					{
						serialPort.notifyOnOutputEmpty(true);
					} catch (Exception e)
					{
						System.out.println(
								"[SerialComms] -- ERROR: failed to set up event notification");
						success = false;
					}

				}
			}
		}

		if (!portFound)
		{
			success = false;
			System.out.println("[SerialComms] ERROR: port " + portName + " not found");
		}

		return success;
	}
}

