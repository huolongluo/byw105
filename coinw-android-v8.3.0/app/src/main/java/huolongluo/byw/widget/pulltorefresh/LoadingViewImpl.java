package huolongluo.byw.widget.pulltorefresh;

public interface LoadingViewImpl {

	void startLoad();

	void stopLoadByError();

	void stopLoadByEmpty();

	void stopLoadByEmpty1();

	void stopLoad();
}
