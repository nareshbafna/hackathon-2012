package flipkart.hackday;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class TestActivity extends Activity {
	MediaRecorder recorder;
	ExtAudioRecorder extAudioRecorder;
	File audiofile = null;
	private static final String TAG = "SoundRecordingActivity";
	private View startButton;
	private View stopButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startButton = findViewById(R.id.start);
		stopButton = findViewById(R.id.stop);
		Log.i(TAG, "Started");
		TextView output = (TextView) findViewById(R.id.txtview);
//		output.append(callService(""));
	}
	
	public void startRecording(View view) throws IOException {

		startButton.setEnabled(false);
		stopButton.setEnabled(true);
		File sampleDir = Environment.getExternalStorageDirectory();
		try {
			audiofile = File.createTempFile("sound", ".3gp", sampleDir);
		} catch (IOException e) {
			Log.e(TAG, "sdcard access error");
			return;
		}
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(audiofile.getAbsolutePath());
		recorder.prepare();
		recorder.start();
	}

	public void stopRecording(View view) {
		startButton.setEnabled(true);
		stopButton.setEnabled(false);
		recorder.stop();
		recorder.release();
		// Stop recording
		// Stop recording
		addRecordingToMediaLibrary();
		TextView output = (TextView) findViewById(R.id.txtview);
//		output.res
		String link = "http://www.flipkart.com/rum-and-whisky/p/itmd936rgtrfad8b?pid=";
		String pid = callService(audiofile.getAbsolutePath());
		link = link + pid;
//		output.append("<br> http://www.flipkart.com/rum-and-whisky/p/itmd936rgtrfad8b?pid=");
//		output.append(callService(audiofile.getAbsolutePath()));
		output.append(link);
//o		output.setMovementMethod(LinkMovementMethod.getInstance());
		Pattern pattern = Pattern.compile("http://www.flipkart.com");
		output.setLinksClickable(true);
		Linkify.addLinks(output, pattern, "");
//		Linkify.addlinks

	}

	protected void addRecordingToMediaLibrary() {
		ContentValues values = new ContentValues(4);
		long current = System.currentTimeMillis();
		values.put(MediaStore.Audio.Media.TITLE, "audio" + audiofile.getName());
		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gp");
		values.put(MediaStore.Audio.Media.DATA, audiofile.getAbsolutePath());
		ContentResolver contentResolver = getContentResolver();

		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Uri newUri = contentResolver.insert(base, values);

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
		
		
		Toast.makeText(this, "Added File " + newUri, Toast.LENGTH_LONG).show();
	}
	
	private String callService(String text) {
		
		String resp = postfile(text);
//		HttpClient httpclient = new DefaultHttpClient();
//	    try {
////	        HttpGet httpget = new HttpGet("http://172.17.104.55:8080/fazham/Dummy");
//	        HttpPost httppost = new HttpPost("http://172.17.81.86:8888/cms/staticcontent/upload");
//
//	        System.out.println("executing request " + httppost.getURI());
//
//	        // Create a response handler
//	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
//	        String responseBody = httpclient.execute(httppost, responseHandler);
//	        System.out.println("----------------------------------------");
//	        System.out.println(responseBody);
//	        System.out.println("----------------------------------------");
//	        return responseBody;
//
//	    } 
//	    catch(Exception e){
//	    	return "";
//	    }
//	    finally {
//	        // When HttpClient instance is no longer needed,
//	        // shut down the connection manager to ensure
//	        // immediate deallocation of all system resources
//	        httpclient.getConnectionManager().shutdown();
//	    }
		return resp;
   }

		private String postfile(String sourceFileUri) {
					
			String response = "";
			String upLoadServerUri = "http://172.17.81.86:8888/cms/staticcontent/upload";
			// String [] string = sourceFileUri;
			String fileName = sourceFileUri;
			
			HttpURLConnection conn = null;
			DataOutputStream dos = null;
			DataInputStream inStream = null;
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			String boundary = "*****";
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;
//			String responseFromServer = "";
			
			File sourceFile = new File(sourceFileUri);
			int serverResponseCode = 0;
			if (!sourceFile.isFile()) {
			Log.e("Huzza", "Source File Does not exist");
			return response;
			}
			try { // open a URL connection to the Servlet
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(upLoadServerUri);
			conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", fileName);
			dos = new DataOutputStream(conn.getOutputStream());
			
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			
			bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
			Log.i("Huzza", "Initial .available : " + bytesAvailable);
			
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			
			while (bytesRead > 0) {
			dos.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			
			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();
			
			Log.i("Upload file to server", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
			// close streams
			Log.i("Upload file to server", fileName + " File is written");
			fileInputStream.close();
			dos.flush();
			dos.close();
			} catch (MalformedURLException ex) {
			ex.printStackTrace();
			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
			} catch (Exception e) {
			e.printStackTrace();
			}
			//this block will give the response of upload link
			
			try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
			.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
			Log.i("Huzza", "RES Message: " + line);
			response = response + line;
			}
			rd.close();
			} catch (IOException ioex) {
			Log.e("Huzza", "error: " + ioex.getMessage(), ioex);
			}
			
			return response;  // like 200 (Ok)
			
		} // end upLoad2Server
		
}
