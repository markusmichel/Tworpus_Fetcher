package fetcher;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import twitter.Tweet;
import ui.FileDropFrame;

public class FetcherApp implements DropTargetListener,
		TweetFetcherProgressListener, FileManagerUpdateListener {
	
	public int max_threads = 30;

	private FileDropFrame fileDropFrame;
	private FileManager fileManager;
	
	private TweetFetcher[] fetchers = new TweetFetcher[max_threads];
	private long[] currentTweets = new long[max_threads];
	
	private int status = 1;
	
	private long start_time;
	private long end_time;
	private long tweets_to_fetch;
	private int startedThreads = 0;
	private int finished_threads = 0;
	private int invalid_tweets = 0;
	

	public static void main(String[] args) {
		new FetcherApp();
	}

	public FetcherApp() {
		init();
	}

	public void init() {
		fileDropFrame = new FileDropFrame("", this);
	}

	public void loadFile(File file) {
		fileManager = new FileManager(file, this);
		long start_with = 1;//Long.valueOf(fileManager.getLogString());
		tweets_to_fetch = fileManager.getLineCount();
		
		long step = (tweets_to_fetch-start_with)/max_threads;
		
		for(int i=0; i< max_threads; i++) {
			fetchers[i] = new TweetFetcher(this, fileManager, i);
			fetchers[i].setParams(i*step, i*step + step, step, true, true, 0);
			if(status == 1) fetchers[i].run();
		}
	}
	
	private long getCurrentTweets() {
		long result = 0;
		for(int i = 0; i < currentTweets.length; i++) {
			result += currentTweets[i];
		}
		return result;
	}

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
	}

	@Override
	public void drop(DropTargetDropEvent event) {
		System.out.println("Dropping things ...");
		event.acceptDrop(DnDConstants.ACTION_COPY);
		Transferable transferable = event.getTransferable();
		DataFlavor[] flavors = transferable.getTransferDataFlavors();
		for (DataFlavor flavor : flavors) {
			try {
				if (flavor.isFlavorJavaFileListType()) {
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) transferable
							.getTransferData(flavor);
					for (File file : files) {
						loadFile(file);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		event.dropComplete(true);
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
	}

	@Override
	public synchronized void onProgressUpdate(long currentTweets, long allTweets, int id) {
		this.currentTweets[id] = currentTweets;
		
		long now = getCurrentTweets();
		
		String log = max_threads+",";
		
		for(int i=0; i< this.currentTweets.length; i++) {
			log += this.currentTweets[i]+",";
		}
		fileManager.writeLogString(log);
		
		float percent = (100/(float)tweets_to_fetch)*now;
		BigDecimal result = round(percent,2);
		fileDropFrame.setProgressText(result + "%");
	}

	@Override
	public void onFileManagerError(String error) {
		status = -1;
	}

	@Override
	public void onTweetNotExsitingError(Tweet tweet) {
		invalid_tweets++;
	}

	@Override
	public synchronized void onStartDownloading() {
		if(startedThreads == 0) {
			start_time = System.currentTimeMillis();
			fileDropFrame.setDownloadBackground();
		}
		startedThreads++;
	}

	@Override
	public synchronized void onStopDownloading() {
		finished_threads++;
		if(finished_threads == startedThreads) {
			end_time = System.currentTimeMillis();
			System.out.println("stoped downloading at " + end_time);
			System.out.println(tweets_to_fetch-invalid_tweets + " tweets in " + (end_time-start_time)/60000 + " minutes");
			fileDropFrame.setFinishBackground();
		}
	}
	
	public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       
        return bd;
    }

}
