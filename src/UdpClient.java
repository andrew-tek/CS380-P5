import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class UdpClient {
	static int MINSIZE = 20;
	public static void main(String[] args) throws UnknownHostException, IOException {
		try (Socket socket = new Socket("codebank.xyz", 38005)) {
			OutputStream out = socket.getOutputStream();
			System.out.println("Connected to server...");
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            System.out.println("Source Address: " + socket.getRemoteSocketAddress().toString());
            byte[] packet = new byte[24];
            packet[0] = 0x45;
			packet[1] = 0;
			packet[2] = (byte) (24 >>> 8);
			packet[3] = (byte) 24;
			//Identification
			packet[4] = 0;
			packet[5] = 0;
//			Flags and Offset
			packet[6] = 0x40;
			packet[7] = 0;
			packet[8] = 50;
			packet[9] = 17;
            
 //           byte [] packet = new byte [32];
//            packet [0] = 0x45;
//			//TOS
//			packet[1] = 0;
//			//Length
//			packet[2] = (byte) (32 >>> 8);
//			packet[3] = (byte) 32;
//			//Identification
//			packet[4] = 0;
//			packet[5] = 0;
//			//Flags and Offset
//			packet[6] = 0x40;
//			packet[7] = 0;
//			//Time to Live
//			packet[8] = 50;
//			//Protocol
//			packet[9] = 6;
			//Source Address
			packet[12] = (byte) 0x6a;
			packet[13] = (byte) 0x64;
			packet[14] = (byte) 0xf5;
			packet[15] = (byte) 0x0d;
			//Destination Address
			packet[16] = (byte) 0x34;
			packet[17] = (byte) 0x25;
			packet[18] = (byte) 0x58;
			packet[19] = (byte) 0x9a;
			//Checksum	
			short check = checksum(packet);
			packet[10] = (byte) (check>>>8);
			packet[11] = (byte) check;
			
			
			packet[20] = (byte) 0xDE;
			packet[21] = (byte) 0xAD;
			packet[22] = (byte) 0xBE;
			packet[23] = (byte) 0xEF;
//			//UDP
//			byte [] udp = new byte [12];
//			//Length
//			packet[24] = 0;
//			packet[25] = 22;
//			packet[28] = (byte) 0xDE;
//			packet[29] = (byte) 0xAD;
//			packet[30] = (byte) 0xBE;
//			packet[31] = (byte) 0xEF;
//			udp[5] = 0;
//			udp[6] = 22;
//			udp[8] = (byte) 0xDE;
//			udp[9] = (byte) 0xAD;
//			udp[10] = (byte) 0xBE;
//			udp[11] = (byte) 0xEF;
//			
//			short check = checksum(udp);
//			packet[26] = (byte) (check >>>8);
//			packet[27] = (byte) check;
//			
            
            out.write(packet);
			for (int l = 0; l < 4; l++) {
				
				System.out.printf("%x",is.read());
			}
			System.out.println();
			
			
			
			
//			int data = 1;
//			for (int i = 0; i < 12; i++) {
//				data *= 2;
//				int size = MINSIZE + data;
//				byte [] packet = new byte [size];
//				//Version and HLen
//				packet [0] = 0x45;
//				//TOS
//				packet[1] = 0;
//				//Length
//				packet[2] = (byte) (size >>> 8);
//				packet[3] = (byte) size;
//				//Identification
//				packet[4] = 0;
//				packet[5] = 0;
//				//Flags and Offset
//				packet[6] = 0x40;
//				packet[7] = 0;
//				//Time to Live
//				packet[8] = 50;
//				//Protocol
//				packet[9] = 6;
//				//Source Address
//				packet[12] = (byte) 0x6a;
//				packet[13] = (byte) 0x64;
//				packet[14] = (byte) 0xf5;
//				packet[15] = (byte) 0x0d;
//				//Destination Address
//				packet[16] = (byte) 0x34;
//				packet[17] = (byte) 0x25;
//				packet[18] = (byte) 0x58;
//				packet[19] = (byte) 0x9a;
//				//Checksum
//				short check = checksum(packet);
//				packet[10] = (byte) (check>>>8);
//				packet[11] = (byte) check;
//				//Assume data is 0
//				out.write(packet);
//				System.out.println("Packet: " + (i + 1));
//				System.out.println("Data Length: " + data);
//				System.out.println(br.readLine());	
//			}
		}
		
	}
	public static short checksum(byte[] b) {
		int checkSum = 0;
		int value, length;
		if (b.length % 2 == 1)
			length = b.length / 2 + 1;
		else
			length = b.length / 2;
		for (int i = 0; i < length; i++) {
				
			try  {
				value = (((b[i * 2] << 8) & 0xFF00) | ((b[i * 2 + 1]) & 0xFF));
				checkSum += value;
			}
			catch (Exception IndexOutOfBoundsException) {
				checkSum += (b[i * 2] << 8 & 0xFF00);
				
			}
			if ((checkSum & 0xFFFF0000) > 0) {
				//checkSum += (b[i] << 8 & 0xFF00);
				checkSum = checkSum & 0xFFFF;
				checkSum++;
			}
					
		}
		return (short)(~checkSum & 0xFFFF);
		
	}
}
