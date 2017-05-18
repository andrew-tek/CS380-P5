//Andrew Tek
//CS 380
//Project 5

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class UdpClient {
	static int MINSIZE = 28;
	public static void main(String[] args) throws UnknownHostException, IOException {
		try (Socket socket = new Socket("codebank.xyz", 38005)) {
			OutputStream out = socket.getOutputStream();
			System.out.println("Connected to server...");
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            System.out.println("Source Address: " + socket.getRemoteSocketAddress().toString());
            byte[] packet = new byte[24];
            long avgTime = 0;
            packet[0] = 0x45;
			packet[1] = 0;
			packet[2] = (byte) (24 >>> 8);
			packet[3] = (byte) 24;
			//Identification
			packet[4] = 0;
			packet[5] = 0;
			//Flags and Offset
			packet[6] = 0x40;
			packet[7] = 0;
			packet[8] = 50;
			packet[9] = 17;
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
            out.write(packet);
            System.out.print("Handshake Response: ");
			for (int l = 0; l < 4; l++) {
				System.out.printf("%x",is.read());
			}
			System.out.println();
			byte [] port = new byte [2];
			port[0] = (byte) is.read();
			port[1] = (byte) is.read();
			int val = ((port[0] & 0xff) << 8) | (port[1] & 0xff);
			System.out.println("Port Number: " + val + "\n");
			
			int data = 1;
			
			for (int i = 0; i < 12; i++) {
				data *= 2;
				
				int size = MINSIZE + data;
				packet = new byte [size];
				System.out.println("Packet: " + (i + 1));
				System.out.println("Data Length: " + data);
				//Version and HLen
				packet [0] = 0x45;
				//TOS
				packet[1] = 0;
				//Length
				packet[2] = (byte) (size >>> 8);
				packet[3] = (byte) size;
				//Identification
				packet[4] = 0;
				packet[5] = 0;
				//Flags and Offset
				packet[6] = 0x40;
				packet[7] = 0;
				//Time to Live
				packet[8] = 50;
				//Protocol
				packet[9] = 17;
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
				check = checksum(packet);
				packet[10] = (byte) (check>>>8);
				packet[11] = (byte) check;
				//UDP Segment
				//Source Port
				packet[20] = 12;
				packet[21] = 13;
				//Destination port
				packet[22] = port[0];
				packet[23] = port[1];
				//Length
				packet[24] = (byte) ((8 + data) >>> 8);
				packet[25] = (byte) (8 + data);
				
				//Data
				Random rand = new Random();
				byte [] randomData = new byte [size - 28];
				rand.nextBytes(randomData);
				int k = 0;
				for (int j = 28; j < size; j++) {
					packet[j] = randomData[k];
					k++;
				}
				byte [] udp = new byte [20 + data];
				int m = 0;
				//Source Address
				udp[0] = (byte) 0x6a;
				udp[1] = (byte) 0x64;
				udp [2] = (byte) 0xf5;
				udp[3] = (byte) 0x0d;
				//Destination Address
				udp [4] = (byte) 0x34;
				udp [5] = (byte) 0x25;
				udp [6] = (byte) 0x58;
				udp[7] = (byte) 0x9a;
				udp[8] = 0;
				udp[9] = 17;
				udp[10] = (byte) ((8 + data) >>> 8);
				udp [11] = (byte) (8 + data);
				m = 12;
				for (int j = 20; j < size; j++) {
					udp[m] = packet[j];
					m++;
				}

				//Checksum
				check = checksum(udp);
				packet[26] = (byte)(check >>> 8);
				packet[27] = (byte)check;
				long startTime = System.currentTimeMillis();
				out.write(packet);
				System.out.print("Response: ");
				System.out.printf("%x",is.read());
				long endTime = System.currentTimeMillis();
				for (int l = 0; l < 3; l++) {
					System.out.printf("%x",is.read());
				}
				avgTime += endTime - startTime;
				System.out.println("\nRound Trip Time: " + (endTime - startTime) + " ms\n");
		}
			System.out.println("Average Trip Time: " + (avgTime / 12) + " ms");
		}
		catch (Exception e) {
			System.out.println("Unable to connect to server.");
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
