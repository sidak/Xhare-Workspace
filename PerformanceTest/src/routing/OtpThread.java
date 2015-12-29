package routing;

public class OtpThread extends Thread{
	private int idx;
	private int mod;
	private Otp otp;
	public OtpThread(int idx, int mod, Otp otp){
		this.idx = idx;
		this.mod = mod;
		this.otp = otp;
	}
	public void run(){
		otp.doRouting(idx, mod);
	}
}
