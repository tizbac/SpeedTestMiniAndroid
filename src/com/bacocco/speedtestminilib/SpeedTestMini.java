package com.bacocco.speedtestminilib;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;


public class SpeedTestMini {
	private String host;
	private int port;
	private ProgressReportListener report = null;
	private static final long BUFFERSIZE = 8192;
	private static final long REPORTINTERVAL = BUFFERSIZE*10;
	private static final long UPLOAD_SIZE = 31625365L;
	
	public static void main(String args[])
	{
		SpeedTestMini inst = new SpeedTestMini("192.168.80.17", 80);
		inst.setProgressReportListener(new ProgressReportListener() {
			
			@Override
			public void reportCurrentDownloadSpeed(long bits) {
				System.out.println(bits);
				
			}

			@Override
			public void reportCurrentUploadSpeed(long bits) {
				// TODO Stub di metodo generato automaticamente
				
			}
		});
		try {
			System.out.println(inst.doDownloadtest());
		} catch (IOException e) {
			// TODO Blocco catch generato automaticamente
			e.printStackTrace();
		}
	}
	
	public SpeedTestMini(String host, int port) {
		this.host = host;
		this.port = port;

		
	}
	
	public void setProgressReportListener(ProgressReportListener lis)
	{
		report = lis;
	}
	
	public long doUploadtest() throws UnknownHostException, IOException
	{
		Socket sock = new Socket(host,port);
		PrintWriter pw = new PrintWriter(sock.getOutputStream());
		//InputStreamReader istream = new InputStreamReader(sock.getInputStream());
		pw.println("POST /speedtest/upload.php HTTP/1.1");
		pw.println("Host: "+host);
		pw.println("Cache-Control: max-age=0");
		pw.println("Content-Length: "+UPLOAD_SIZE);
		pw.println("Connection: close");
		pw.println("");
		pw.flush();
		long bytestot = 1;
		long bytesint = 0;
		long byteswritten = 1;
		long start = System.currentTimeMillis();
		
		
		long intermediate = start;
		byte buffer[] = new byte[(int) BUFFERSIZE];
		for ( int i = 0; i < BUFFERSIZE; i++ )
			buffer[i] = (byte) (Math.random()*255);
		while ( bytestot < UPLOAD_SIZE )
		{
			sock.getOutputStream().write(buffer);
			byteswritten = buffer.length;
			bytesint += byteswritten;
			bytestot += byteswritten;
			if ( bytesint >= REPORTINTERVAL )
			{
				long took = System.currentTimeMillis() - start;  
				long bits = (long)((double)(bytestot*8)/((double)took/1000.0));
				
				if ( report != null )
					report.reportCurrentUploadSpeed(bits);
				
				bytesint = 0;
				intermediate = System.currentTimeMillis();
			}
		}
		
		long took = System.currentTimeMillis() - start; 
		long bits = (long)((double)(bytestot*8)/((double)took/1000.0));
		
		sock.close();
		return bits;
	}
	
	public long doDownloadtest() throws UnknownHostException, IOException
	{
		Socket sock = new Socket(host,port);
		PrintWriter pw = new PrintWriter(sock.getOutputStream());
		//InputStreamReader istream = new InputStreamReader(sock.getInputStream());
		pw.println("GET /speedtest/random4000x4000.jpg HTTP/1.1");
		pw.println("Host: "+host);
		pw.println("Cache-Control: max-age=0");
		pw.println("Connection: close");
		pw.println("");
		pw.flush();
		long bytestot = 1;
		long bytesint = 0;
		long bytesread = 1;
		sock.getInputStream().read();
		long start = System.currentTimeMillis();
		
		
		long intermediate = start;
		byte buffer[] = new byte[(int) BUFFERSIZE];
		while ( ( bytesread = sock.getInputStream().read(buffer) ) != -1)
		{
			bytesint += bytesread;
			bytestot += bytesread;
			if ( bytesint >= REPORTINTERVAL )
			{
				long took = System.currentTimeMillis() - start;  
				long bits = (long)((double)(bytestot*8)/((double)took/1000.0));
				
				if ( report != null )
					report.reportCurrentDownloadSpeed(bits);
				
				bytesint = 0;
				intermediate = System.currentTimeMillis();
			}
		}
		
		long took = System.currentTimeMillis() - start; 
		long bits = (long)((double)(bytestot*8)/((double)took/1000.0));
		
		sock.close();
		return bits;
	}
	
	
}
