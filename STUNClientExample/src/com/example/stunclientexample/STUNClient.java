package com.example.stunclientexample;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Random;

import android.os.Handler;

class STUNClient implements Runnable
{
	private String ip;
	private String error;
	private String STUNhost;
	private int port;
	private int failCounter;
	private Handler handler;
	private DatagramSocket clientSocket;
	
	private int GetUnsignedByte(byte val)
	{
		return ((int)val) & 0xFF;
	}
	private int GetUnsignedShort(short val)
	{
		return ((int)val) & 0xFFFF;
	}

	public STUNClient(int trysCount,Handler handler,String stunServer)
	{
		this.failCounter=trysCount;
		this.handler=handler;
		this.ip=null;
		this.port=-1;
		this.STUNhost = stunServer;
	}
	
	private short convertntohs(byte[] value)
	{
		ByteBuffer buf = ByteBuffer.wrap(value);
		return buf.getShort();
	}
	 
	@Override
	public void run() 
	{
		try
		{
			clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(2000);
			InetAddress IPAddress = InetAddress.getByName(this.STUNhost);
			byte[] sendData = new byte[20];//network bytes order
			byte[] respData = new byte[32];//network bytes order
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 19302);
			DatagramPacket receivePacket = new DatagramPacket(respData, respData.length);
			sendData[0]=0;
			sendData[1]=1;//STUN_BINDREQ    0x0001
			
			sendData[2]=0;
			sendData[3]=0;//length = 0
			
			sendData[4]=0x42;
			sendData[5]=(byte) 0xA4;
			sendData[6]=0x12;
			sendData[7]=0x21;//4 magic bytes = 0x2112A442
			
			Random random = new Random();
			for(int i=8;i<20;i++)
			{
				sendData[i]=(byte)random.nextInt(255);//set last 12 bytes to rand vals
			}
			
			for(int i=0;i<this.failCounter;i++)
			{
				clientSocket.send(sendPacket);
				clientSocket.receive(receivePacket);
				//clientSocket.close();
				
				if(respData[0]==1 && respData[1]==1)//STUN_BINDRESP   0x0101
				{// last 6 bytes -> 2 bytes port+4 bytes IP 
					ip=Integer.toString(this.GetUnsignedByte(respData[28]))+"."+Integer.toString(this.GetUnsignedByte(respData[29]))+"." 
							+Integer.toString(this.GetUnsignedByte(respData[30]))+"."+Integer.toString(this.GetUnsignedByte(respData[31]));
					byte[] portbuf=new byte[2];
					portbuf[0]=respData[26];
					portbuf[1]=respData[27];
					this.port=this.GetUnsignedShort(this.convertntohs(portbuf));
					this.handler.sendEmptyMessage(0);
					clientSocket.setSoTimeout(0);
					break;
				}
			}
			this.handler.sendEmptyMessage(0);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			//System.out.println(e.getMessage());
			this.error=this.error+e.getMessage()+"\r\n";
			e.printStackTrace();
			//this.handler.sendEmptyMessage(0);
		}	
	}
	public String GetIp()
	{
		return this.ip;
	}
	public int GetPort()
	{
		return this.port;
	}
	public String GetErrors()
	{
		return this.error;
	}
	public DatagramSocket GetUDPSock()
	{
		return this.clientSocket;
	}
}
