package com.sctuopuyi.packageinfos.updateApk;

/**
 * 下载进度回调接口
 *
 * @author fengmlo
 */
public interface ProgressListener {

	/**
	 * 下载进度回调
	 *
	 * @param bytesRead     已下载的数据
	 * @param contentLength 总数据量
	 * @param done          是否完成
	 */
	void update(long bytesRead, long contentLength, boolean done);
}
