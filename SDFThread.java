import java.util.List;

public class SDFThread implements Runnable {
	/*
	 * divide the string in 3 parts. 30000 = 10000 strings per thread. measure
	 * the time.
	 */
	private Thread t;
	private String threadName;
	private List<String> lstLabels=null;
	private int [] scr;
	private int count=0;
	
	public SDFThread(String pName, List<String> pLstLabels) {
		threadName = pName;
		lstLabels = pLstLabels;
		scr = new int[pLstLabels.size()];
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		for (String elem : lstLabels) {
			String s[] = elem.split(";");
			scr[count++] = SDFTest.f(s[0], s[1], 100);
		}
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Thread: "+threadName+" TotalTime for "+scr.length+" pair of labels is: " + totalTime);
	}

	public void start() {
		if (t == null) {
			t = new Thread(this, threadName);
			t.start();
		}
	}

}
